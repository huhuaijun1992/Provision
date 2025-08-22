package com.custom.provision.utils;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;
import android.os.RemoteException;
import android.util.Log;

import com.android.internal.app.LocalePicker;

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
    final static String TAG = "LanguageUtils";


    public static void setLanguage(Locale locale) {
        Log.d(TAG, "setLanguage: " + locale.getLanguage() + locale.getDisplayScript());
        try {
            LocalePicker.updateLocale(locale);
        } catch (Exception e) {
            Log.e(TAG, "setLanguage: ", e);
        }
    }

    public static boolean setRegion(String newCountry) {
        LogUtils.d("setRegion: " + newCountry);
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale locale = new Locale.Builder()
                    .setRegion(newCountry)
                    .setLanguage(defaultLocale.getLanguage())
                    .setScript(defaultLocale.getScript())
                    .build();
            LocalePicker.updateLocale(locale);
            LogUtils.d("setRegion: success ,region:" + locale.getDisplayCountry());
            return true;
        } catch (Exception e) {
            LogUtils.d("setRegion: failed " + newCountry + e.getMessage());
        }
        return false;
    }

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

    public static List<Locale> getRegionList() {
        List<Locale> list = new ArrayList<>();
        for (String countryCode : Locale.getISOCountries()) {
            Locale locale = new Locale("", countryCode);
            list.add(locale);
        }

        // 排序
        list.sort(Comparator.comparing(l -> l.getDisplayCountry(Locale.ENGLISH)));
        return list;
    }


}
