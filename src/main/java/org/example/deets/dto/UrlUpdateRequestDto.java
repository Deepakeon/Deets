package org.example.deets.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UrlUpdateRequestDto {
    @Size(min = 8, max = 9, message = "Code must be exactly 8 characters")
    private String code;

}
