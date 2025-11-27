package com.project.shopapp.responses.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.enums.SocialProvider;
import com.project.shopapp.models.SocialAccount;
import com.project.shopapp.models.User;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("is_active")
    private boolean active;

    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    private String googleAccountId;

    @JsonProperty("role_name")
    private String roleName;

    @JsonProperty("wallet_balance")
    private BigDecimal walletBalance;

    public static UserResponse fromUser(User user) {
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .address(user.getAddress())
                .active(user.isActive())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .build();

        if (user.getSocialAccounts() != null && !user.getSocialAccounts().isEmpty()) {
            for (SocialAccount account : user.getSocialAccounts()) {
                if (SocialProvider.FACEBOOK.name().equalsIgnoreCase(account.getProvider()) || SocialProvider.FACEBOOK.getProvider().equalsIgnoreCase(account.getProvider())) {
                    response.setFacebookAccountId(account.getProviderId());
                } else if (SocialProvider.GOOGLE.name().equalsIgnoreCase(account.getProvider()) || SocialProvider.GOOGLE.getProvider().equalsIgnoreCase(account.getProvider())) {
                    response.setGoogleAccountId(account.getProviderId());
                }
            }
        }

        if (user.getWallet() != null) {
            response.setWalletBalance(user.getWallet().getBalance());
        }

        return response;
    }

}
