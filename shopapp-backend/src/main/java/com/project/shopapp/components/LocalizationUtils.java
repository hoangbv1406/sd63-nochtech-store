package com.project.shopapp.components;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LocalizationUtils {
    private final MessageSource messageSource;

    public String getLocalizedMessage(String messageKey, Object... params) {
        Locale locale = LocaleContextHolder.getLocale();

        try {
            return messageSource.getMessage(messageKey, params, locale);
        } catch (NoSuchMessageException e) {
            return messageKey;
        }
    }
}
