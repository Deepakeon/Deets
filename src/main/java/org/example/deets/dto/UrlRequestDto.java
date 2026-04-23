package org.example.deets.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UrlRequestDto {
    @NotBlank
    @URL(message = "Invalid url")
    private String longUrl;
}
