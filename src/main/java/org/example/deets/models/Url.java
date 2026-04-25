package org.example.deets.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name="urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    @Size(min = 8, max = 9, message = "Code must be exactly 8 characters")
    private String code;

    @Column(name = "long_url", nullable = false, unique = true)
    private String longUrl;
}
