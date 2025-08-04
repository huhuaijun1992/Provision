package com.custom.provision.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;

public class SystemSettingsHelper {
    private static final String TAG = "SystemSettingsHelper";

    // 使用反射访问Settings.Global
    public static void setDeviceProvisioned(Context context, boolean provisioned) {
        try {
            Class<?> globalClass = Class.forName("android.provider.Settings$Global");
            Method putInt = globalClass.getMethod("putInt",
                    ContentResolver.class, String.class, int.class);

            putInt.invoke(null, context.getContentResolver(),
                    "device_provisioned", provisioned ? 1 : 0);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set device_provisioned", e);
            // 降级方案：使用Settings.System（部分设备可能支持）
            Settings.System.putInt(context.getContentResolver(),
                    "device_provisioned", provisioned ? 1 : 0);
        }
    }

}
