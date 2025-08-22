package com.custom.provision.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Author: created by huhuaijun on 2025/8/5 12:02
 * Function:
 */
public class LanguageOption {
    public final String label;
    public final Locale locale;

    public LanguageOption(String label, Locale locale) {
        this.label = label;
        this.locale = locale;
    }

    public String getLabel() {
        return label;
    }

    public Locale getLocale() {
        return locale;
    }

    public static List<LanguageOption> getSupportedLanguages() {
        return Arrays.asList(
                new LanguageOption("中文简体",new Locale.Builder().setLanguage("zh").setRegion("CN").setScript("Hans").build()),
                new LanguageOption("中文繁體", new Locale.Builder().setLanguage("zh").setRegion("TW").setScript("Hant").build()),
                new LanguageOption("English",Locale.US),
                new LanguageOption("한국어", Locale.KOREA),
                new LanguageOption("日本語", Locale.JAPAN)

        );
    }
}
