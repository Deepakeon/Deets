package org.example.deets.controllers;

import jakarta.validation.Valid;
import org.example.deets.dto.UrlUpdateRequestDto;
import org.example.deets.models.Url;
import org.example.deets.exceptions.UrlNotFoundException;
import org.example.deets.dto.UrlRequestDto;
import org.example.deets.services.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }

    @PostMapping
    @RequestMapping("/api/urls")
    public ResponseEntity<String> shortenUrl(@RequestBody @Valid UrlRequestDto urlRequest){
        String code = urlService.getOrShortenUrl(urlRequest.getLongUrl());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(code);
    }

    @GetMapping("/{encoding}")
    public ResponseEntity<Void> redirectToUrl(@PathVariable String encoding){
        Url url = urlService.getUrlByEncoding(encoding).orElseThrow(() -> new UrlNotFoundException("url not found"));
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url.getLongUrl()))
                .build();
    }

    @PatchMapping("/api/urls/{id}")
    public ResponseEntity<Url> updateUrl(@PathVariable UUID id, @RequestBody @Valid UrlUpdateRequestDto urlUpdateRequestDto){
        Url url = urlService.updateUrl(id, urlUpdateRequestDto.getCode());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(url);
    }
}
