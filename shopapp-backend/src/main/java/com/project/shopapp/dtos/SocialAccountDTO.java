package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.experimental.SuperBuilder;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
public abstract class SocialAccountDTO {

    @JsonProperty("provider")
    protected String provider;

    @JsonProperty("provider_id")
    protected String providerId;

    @JsonProperty("email")
    protected String email;

    @JsonProperty("name")
    protected String name;

    public boolean isSocialLogin() {
        return provider != null && !provider.trim().isEmpty()
                && providerId != null && !providerId.trim().isEmpty();
    }

}
