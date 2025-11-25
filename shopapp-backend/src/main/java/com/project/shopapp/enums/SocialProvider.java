package com.project.shopapp.enums;

public enum SocialProvider {
    LOCAL("local"),
    GOOGLE("google"),
    FACEBOOK("facebook"),
    GITHUB("github");

    private final String provider;

    SocialProvider(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return this.provider;
    }

    public static SocialProvider fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return LOCAL;
        }
        for (SocialProvider sp : SocialProvider.values()) {
            if (sp.provider.equalsIgnoreCase(text.trim())) {
                return sp;
            }
        }
        throw new IllegalArgumentException("Mạng xã hội không được hỗ trợ: " + text);
    }
}
