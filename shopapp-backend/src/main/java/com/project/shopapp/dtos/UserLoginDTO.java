package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class UserLoginDTO extends SocialAccountDTO {

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

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

}
