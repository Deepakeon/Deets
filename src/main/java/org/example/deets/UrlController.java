package org.example.deets;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }

    // TODO: add tests for creating a url, create the same url again, bad request
    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody @Valid UrlRequestDto urlRequest){
        return urlService.getOrShortenUrl(urlRequest.getLongUrl());
    }

    // TODO: add tests for fetching a correct encoding, fetching a non existent endocing
    @GetMapping("/{encoding}")
    public ResponseEntity<Void> redirectToUrl(@PathVariable String encoding){
        Url url = urlService.getUrlByEncoding(encoding).orElseThrow(() -> new UrlNotFoundException("url not found"));

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url.getLongUrl()))
                .build();
    }
}
