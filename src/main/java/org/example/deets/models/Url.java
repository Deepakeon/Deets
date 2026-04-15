package org.example.deets.models;

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
                name = "idx_code",
                columnList = "code"
        )
)
public class Url {
    @Id
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "long_url", nullable = false, unique = true)
    private String longUrl;
}
