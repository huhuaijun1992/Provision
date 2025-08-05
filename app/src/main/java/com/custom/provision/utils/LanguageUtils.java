package com.custom.provision.utils;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;
import android.os.RemoteException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Author: created by huhuaijun on 2025/8/5 12:04
 * Function:
 */
public class LanguageUtils {
    public static void changeSystemLanguage(Context context, Locale locale) {
        try {
            // 1. 设置默认语言
            Locale.setDefault(locale);

            // 2. 创建新配置
            Configuration config = new Configuration();
            config.setLocales(new LocaleList(locale));

            // 3. 更新系统配置（系统权限）
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                // 反射更新系统配置（系统 App 有权限）
                Method updateConfigurationMethod = ActivityManager.class
                        .getMethod("updatePersistentConfiguration", Configuration.class);
                updateConfigurationMethod.invoke(am, config);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLanguageOnly(String newLanguage) {
        try {
            IActivityManager am = ActivityManager.getService();
            Configuration config = am.getConfiguration();
            Locale currentLocale = config.getLocales().get(0);

            // 保持原来的国家
            String country = currentLocale.getCountry();

            Locale newLocale = new Locale(newLanguage, country);
            config.setLocales(new LocaleList(newLocale));
            config.userSetLocale = true;
            am.updatePersistentConfiguration(config);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void setRegionOnly(String newCountry) {
        try {
            IActivityManager am = ActivityManager.getService();
            Configuration config = am.getConfiguration();
            Locale currentLocale = config.getLocales().get(0);

            // 保持原来的语言
            String language = currentLocale.getLanguage();

            Locale newLocale = new Locale(language, newCountry);
            config.setLocales(new LocaleList(newLocale));
            config.userSetLocale = true;
            am.updatePersistentConfiguration(config);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static List<Map.Entry<String, Locale>> getRegion(){
        // Map<CountryCode, Locale>
        Map<String, Locale> countryMap = new HashMap<>();

        for (Locale locale : Locale.getAvailableLocales()) {
            String language = locale.getLanguage();
            String country = locale.getCountry();
            String variant = locale.getVariant();

            // 忽略无国家或有变种的 locale
            if (language.isEmpty() || country.isEmpty() || !variant.isEmpty()) continue;

            // 以国家代码去重（保留第一个遇到的）
            countryMap.putIfAbsent(country, locale);
        }

        // 构造 Entry 列表用于排序
        List<Map.Entry<String, Locale>> entries = new ArrayList<>(countryMap.entrySet());

        // 按英文国家名排序
        entries.sort(Comparator.comparing(entry -> entry.getValue().getDisplayCountry(Locale.ENGLISH)));
        return entries;
    }


}
