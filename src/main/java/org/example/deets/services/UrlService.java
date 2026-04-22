package org.example.deets.services;

import lombok.RequiredArgsConstructor;
import org.example.deets.exceptions.UrlNotFoundException;
import org.example.deets.utils.Base62;
import org.example.deets.models.Url;
import org.example.deets.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository repository;
    private final RedisService redisService;

    @Value("${app.base-url}")
    private String appBaseUrl;

    private String getUrlWithCode(String code){
        return "%s/%s".formatted(appBaseUrl, code);
    }
    public String getOrShortenUrl(String longUrl){
        Optional<Url> alreadyPresentUrl = getUrlByLongUrl(longUrl);
        return alreadyPresentUrl.map(url -> getUrlWithCode(url.getCode())).orElseGet(() -> shortenUrl(longUrl));
    }

    public String shortenUrl(String longUrl){
        UUID id = UUID.randomUUID();
        String base62EncodedId = Base62.encodeUuid(id);
        Url url = Url.builder().id(id).longUrl(longUrl).code(base62EncodedId).build();
        repository.save(url);
        return getUrlWithCode(base62EncodedId);
    }

    public Optional<Url> getUrlByEncoding(String encoding){

        return repository.findUrlByCode(encoding);
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
        return repository.save(url.get());
    }

}
