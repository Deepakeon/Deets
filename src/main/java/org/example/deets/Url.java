package org.example.deets;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(
        name="urls",
        indexes = @Index(
                name = "IDX_short_url",
                columnList = "short_url"
        )
)
public class Url {
    @Id
    private UUID id;

    @Column(name = "short_url", nullable = false, unique = true)
    private String shortUrl;

    @Column(name = "long_url", nullable = false, unique = true)
    private String longUrl;
}
