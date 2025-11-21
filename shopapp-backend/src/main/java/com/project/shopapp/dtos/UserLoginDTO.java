package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.enums.SocialProvider;
import jakarta.validation.constraints.Min;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
public class UserLoginDTO extends SocialAccountDTO {

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("password")
    private String password;

    @Min(value = 1, message = "Role ID must be at least 1")
    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("fullname")
    private String fullname;

    @JsonProperty("profile_image")
    private String profileImage;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }

    public boolean isProviderValid() {
        if (!isSocialLogin()) return false;
        try {
            SocialProvider.valueOf(provider.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
