package com.custom.provision.entity;

import android.content.Context;

import com.custom.provision.R;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: created by huhuaijun on 2025/8/5 11:29
 * Function:
 */
public enum Language {
    // 定义枚举常量，每个常量代表一种语言
    CHINESE_SIMPLIFIED("zh", R.string.language_china),
    CHINESE_TRADITIONAL("TW", R.string.language_china_tw),
    ENGLISH("US", R.string.language_english),
    KOREAN("KR", R.string.language_korean),
    JAPANESE("JP", R.string.language_japanese);


    private final String languageCode;
    private final int languageNameResId;  // 语言名称的资源ID

    // 私有构造函数
    Language(String languageCode, int languageNameResId) {
        this.languageCode = languageCode;
        this.languageNameResId = languageNameResId;
    }

    // Getter方法
    public String getLanguageCode() {
        return languageCode;
    }

    public int getLanguageNameResId() {
        return languageNameResId;
    }

    // 根据国家代码查找语言（忽略大小写）
    public static Language fromCountryCode(String countryCode) {
        if (countryCode == null) return ENGLISH;  // 默认值

        for (Language lang : values()) {
            if (lang.languageCode.equalsIgnoreCase(countryCode)) {
                return lang;
            }
        }
        return ENGLISH;  // 找不到时返回英语
    }

    // 获取所有支持的国家代码
    public static List<String> getAllCountryCodes() {
        return Arrays.stream(values())
                .map(Language::getLanguageCode)
                .collect(Collectors.toList());
    }

    // 获取显示名称（需要Context）
    public String getDisplayName(Context context) {
        return context.getString(languageNameResId);
    }
}