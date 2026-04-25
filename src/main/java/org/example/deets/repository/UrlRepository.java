package org.example.deets.repository;

import org.example.deets.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UrlRepository extends JpaRepository<Url, UUID> {
    Optional<Url> findUrlByCode(String code);
    Optional<Url> findUrlByLongUrl(String longUrl);

}
