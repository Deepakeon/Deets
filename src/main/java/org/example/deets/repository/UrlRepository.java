package org.example.deets.repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.example.deets.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UrlRepository extends JpaRepository<Url, UUID> {
    Optional<Url> findUrlByCode(String code);
    Optional<Url> findUrlByLongUrl(String longUrl);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Url u WHERE u.id = :id")
    Optional<Url> findUrlByIdForUpdate(@Param("id") UUID id);
}
