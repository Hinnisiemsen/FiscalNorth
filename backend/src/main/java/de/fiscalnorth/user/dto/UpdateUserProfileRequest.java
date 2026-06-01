package de.fiscalnorth.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @NotBlank @Size(min = 2, max = 100) String userName,
        @NotBlank @Pattern(regexp = "en|de|fr|es") String locale
) {
}
