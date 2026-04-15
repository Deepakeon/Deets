package org.example.deets.services;

import org.example.deets.exceptions.UrlNotFoundException;
import org.example.deets.utils.Base62;
import org.example.deets.models.Url;
import org.example.deets.repository.UrlRepository;
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
            return alreadyPresentUrl.get().getCode();
        }else{
            return shortenUrl(longUrl);
        }
    }

    public String shortenUrl(String longUrl){
        UUID id = UUID.randomUUID();
        String base62EncodedId = Base62.encodeUuid(id);
        Url url = Url.builder().id(id).longUrl(longUrl).code(base62EncodedId).build();
        repository.save(url);
        return "%s/%s".formatted(appBaseUrl, base62EncodedId);
    }

    public Optional<Url> getUrlByEncoding(String encoding){
        return repository.findUrlByCode(encoding);
    }

    public Optional<Url> getUrlById(UUID id){
        return repository.findById(id);
    }

    public Optional<Url> getUrlByLongUrl(String longUrl){
        return repository.findUrlByLongUrl(longUrl);
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
