package com.custom.provision.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.ServiceManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.android.internal.statusbar.IStatusBarService;

public class GestureUtils {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        if (lastClickTime == 0)
            return flag;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    public static int getForeFsgNavBar(Context context){
        return Settings.Global.getInt(
                context.getContentResolver(),
                "force_fsg_nav_bar",
                0
        );
    }

    public static boolean forbidenFsgNavBar(Context context){
        return Settings.Global.putInt(
                context.getContentResolver(),
                "force_fsg_nav_bar",
                1
        );
    }



    /**
     * 普通沉浸模式（适合调试或非系统签名 App）
     */
    public static void hideNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Window window = activity.getWindow();
            // 允许内容延伸到状态栏
            window.setDecorFitsSystemWindows(false);

            // 设置状态栏透明
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars());
//                controller.hide(WindowInsets.Type.statusBars());
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                );
//                controller.setSystemBarsAppearance(
//                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, // 浅色状态栏图标
//                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
//                );
            }
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    /**
     * 系统签名 App 专用，彻底禁用导航栏（不会被手势呼出）
     * 需要权限：android.permission.STATUS_BAR_SERVICE
     */
    public static void disableNavigationBarSystem() {
        try {
            IStatusBarService statusBar = IStatusBarService.Stub.asInterface(
                    ServiceManager.getService("statusbar")
            );
            if (statusBar != null) {
                // 0x00000002 对应 DISABLE_NAVIGATION 按钮
                statusBar.disable(0x00000002, null, "NavBarHider");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复导航栏
     */
    public static void enableNavigationBarSystem() {
        try {
            IStatusBarService statusBar = IStatusBarService.Stub.asInterface(
                    ServiceManager.getService("statusbar")
            );
            if (statusBar != null) {
                statusBar.disable(0, null, "NavBarHider");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
