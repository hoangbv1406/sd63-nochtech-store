package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RefreshTokenDTO {

    @NotBlank(message = "Refresh token is required")
    @JsonProperty("refreshToken")
    private String refreshToken;

}
