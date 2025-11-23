package com.project.shopapp.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-{2,}");

    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String temp = input.replace("Đ", "D").replace("đ", "d");
        String normalized = Normalizer.normalize(temp, Normalizer.Form.NFD);
        String slug = DIACRITICS.matcher(normalized).replaceAll("");

        slug = WHITESPACE.matcher(slug.trim()).replaceAll("-");
        slug = NONLATIN.matcher(slug).replaceAll("");
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");
        slug = slug.replaceAll("^-|-$", "");

        return slug.toLowerCase(Locale.ENGLISH);
    }
}
