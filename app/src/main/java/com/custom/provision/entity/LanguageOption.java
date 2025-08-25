package com.custom.provision.entity;

import android.content.Context;

import com.custom.provision.R;

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

    public static List<LanguageOption> getSupportedLanguages(Context context) {
        return Arrays.asList(
                new LanguageOption(context.getString(R.string.language_china),new Locale.Builder().setLanguage("zh").setRegion("CN").setScript("Hans").build()),
                new LanguageOption(context.getString(R.string.language_china_tw), new Locale.Builder().setLanguage("zh").setRegion("TW").setScript("Hant").build()),
                new LanguageOption(context.getString(R.string.language_english),Locale.US),
                new LanguageOption(context.getString(R.string.language_korean), Locale.KOREA),
                new LanguageOption(context.getString(R.string.language_japanese), Locale.JAPAN)
        );
    }
}
