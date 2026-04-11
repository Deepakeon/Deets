package org.example.deets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UrlService {

    private final UrlRepository repository;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Autowired
    public UrlService(UrlRepository repository){
        this.repository = repository;
    }

    public String getOrShortenUrl(String longUrl){
        Optional<Url> alreadyPresentUrl = getUrlByLongUrl(longUrl);
        if(alreadyPresentUrl.isPresent()){
            return alreadyPresentUrl.get().getShortUrl();
        }else{
            return shortenUrl(longUrl);
        }
    }

    public String shortenUrl(String longUrl){
        UUID id = UUID.randomUUID();
        String base62EncodedId = Base62.encodeUuid(id);
        String shortUrl = "%s/%s".formatted(appBaseUrl, base62EncodedId);
        Url url = Url.builder().id(id).longUrl(longUrl).shortUrl(shortUrl).build();
        repository.save(url);
        return shortUrl;
    }

    public Optional<Url> getUrlByEncoding(String encoding){
        String shortUrl = "%s/%s".formatted(appBaseUrl, encoding);
        return repository.findUrlByShortUrl(shortUrl);
    }

    public Optional<Url> getUrlByLongUrl(String longUrl){
        return repository.findUrlByLongUrl(longUrl);
    }

}
