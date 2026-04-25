package org.example.deets.services;

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

    @Value("${app.base-url}")
    private String appBaseUrl;

    private String getUrlWithCode(String code){
        return "%s/%s".formatted(appBaseUrl, code);
    }

    private Url saveUrlToDb(Url url){
        try {
            return repository.save(url);
        } catch (DataIntegrityViolationException dev) {
            log.error("Duplicate code collision on save: {}", url.getCode(), dev);
            throw new IllegalArgumentException("Alias already exists");
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
        Url url = repository.findUrlByIdForUpdate(id).orElseThrow(() -> {
            log.warn("Update failed url with id: {} not found", id);
            return new UrlNotFoundException("Url not found");
        });

        redisService.delete(new String[]{url.getCode(), String.valueOf(id)});
        url.setCode(code);
        log.info("Url updated successfully with id: {}, new: {}", url.getId(), url.getCode());
        return saveUrlToDb(url);
    }

}
