package org.example.deets.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deets.exceptions.UrlNotFoundException;
import org.example.deets.models.Url;
import org.example.deets.repository.UrlRepository;
import org.example.deets.utils.RandomCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final int maxRetryCount = 10;
    private final UrlRepository repository;
    private final RedisService redisService;
    private final RandomCodeGenerator keyGeneratorService;
    private final ObjectMapper objectMapper;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Value("${cache.ttl:3600}")
    private long ttlSeconds;

    private String getUrlWithCode(String code){
        return "%s/%s".formatted(appBaseUrl, code);
    }

    private Url saveUrlToDb(Url url){
        try {
            redisService.setValueWithExpiry(url.getCode(), objectMapper.writeValueAsString(url), ttlSeconds);
            redisService.setValueWithExpiry(url.getLongUrl(), objectMapper.writeValueAsString(url), ttlSeconds);
            redisService.setValueWithExpiry(String.valueOf(url.getId()), objectMapper.writeValueAsString(url), ttlSeconds);
            return repository.save(url);
        } catch (DataIntegrityViolationException dev) {
            log.error("Duplicate code collision on save: {}", url.getCode(), dev);
            throw new IllegalArgumentException("Alias already exists");
        } catch (JsonProcessingException jpe){
            log.error("Json processing exception");
            return null;
        }
    }

    public String getOrShortenUrl(String longUrl){
        Optional<Url> alreadyPresentUrl = getUrlByLongUrl(longUrl);
        return alreadyPresentUrl.map(url -> getUrlWithCode(url.getCode())).orElseGet(() -> {
            log.info("Created short url cache miss");
            return shortenUrl(longUrl);
        });
    }

    public String shortenUrlWithRetry(String longUrl){
        for(int attempt=1; attempt<=maxRetryCount; attempt++){
            String code = keyGeneratorService.generateRandomCode();
            if(getUrlByCode(code).isEmpty()){
                return code;
            }else{
                log.warn("Code collision attempt {}/10: {}", attempt, code);
            }
        }

        throw new RuntimeException("Failed to generate unique code after %d attempts".formatted(maxRetryCount));
    }

    public String shortenUrl(String longUrl){
        String base62EncodedId = shortenUrlWithRetry(longUrl);
        Url url = Url.builder().longUrl(longUrl).code(base62EncodedId).build();
        saveUrlToDb(url);
        log.info("New url saved to db with id: {}, longUrl: {}", url.getId(), url.getLongUrl());
        return getUrlWithCode(base62EncodedId);
    }

    public Optional<Url> getUrlByCode(String code){
        return redisService.getOrCache(
                code,
                Url.class,
                () -> repository.findUrlByCode(code)
        );
    }

    public Optional<Url> getUrlById(UUID id){
        return redisService.getOrCache(
                String.valueOf(id),
                Url.class,
                () -> repository.findById(id)
        );
    }

    public Optional<Url> getUrlByLongUrl(String longUrl){
        return redisService.getOrCache(
                longUrl,
                Url.class,
                () -> repository.findUrlByLongUrl(longUrl)
        );
    }

    public Url updateUrl(UUID id, String code){
        Optional<Url> url = getUrlById(id);
        if(url.isEmpty()){
            throw new UrlNotFoundException("Url not found");
        }

        url.get().setCode(code);
        redisService.delete(new String[]{code, String.valueOf(id), url.get().getLongUrl()});
        log.info("Url updated successfully with id: {}, new: {}", url.get().getId(), url.get().getCode());
        return saveUrlToDb(url.get());
    }

}
