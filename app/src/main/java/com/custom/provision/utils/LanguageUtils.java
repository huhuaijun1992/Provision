package com.custom.provision.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

import java.lang.reflect.Method;
import java.util.Locale;

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
}
