package org.example.deets;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UrlRepository extends JpaRepository<Url, UUID> {
    public Optional<Url> findUrlByShortUrl(String shortUrl);
    public Optional<Url> findUrlByLongUrl(String longUrl);
}
