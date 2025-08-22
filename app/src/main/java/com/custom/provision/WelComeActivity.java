package com.custom.provision;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.custom.provision.fragment.BaseFragment;
import com.custom.provision.fragment.LanguageSettingFragment;
import com.custom.provision.fragment.RegionFragment;
import com.custom.provision.fragment.SimCheckFragment;
import com.custom.provision.fragment.WelcomeFragment;
import com.custom.provision.fragment.WifiFragment;
import com.custom.provision.manager.WifiInstance;
import com.custom.provision.utils.GestureUtils;
import com.custom.provision.utils.LogUtils;

public class WelComeActivity extends AppCompatActivity {
    private static final String TAG = WelComeActivity.class.getSimpleName();
    private WelcomeFragment welcomeFragment;
    private LanguageSettingFragment languageSettingFragment;
    private WifiFragment wifiFragment;
    private RegionFragment regionFragment;
    private SimCheckFragment simCheckFragment;
    private View rootLayout;

    private BaseFragment baseFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        init();
        GestureUtils.hideNavigationBar(this);
//        GestureUtils.disableNavigationBarSystem();
        showFragment(Operation.welcome);
        ViewGroup rootView = findViewById(android.R.id.content);
        final View contentView = rootView.getChildAt(0); // 页面内容根布局，调整它的高度

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int lastHeight = 0;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                int newHeight = screenHeight - keypadHeight;

                if (newHeight != lastHeight) {
                    lastHeight = newHeight;

                    // 动态设置内容布局高度
                    ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                    lp.height = newHeight;
                    contentView.setLayoutParams(lp);
                }
            }
        });

//        finishSetup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int navigationMode = GestureUtils.getForeFsgNavBar(this);
        LogUtils.d( "onResume: 手势导航：" + navigationMode);
    }

    public void finishSetup() {
        try {
            setProvisioningState();
        } catch (Exception e) {
            Toast.makeText(this, "权限错误", Toast.LENGTH_SHORT).show();
        }
        disableSelfAndFinish();
    }

    private void setProvisioningState() {
        LogUtils.d("Setting provisioning state");
        // Add a persistent setting to allow other apps to know the device has been provisioned.
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
    }

    private void disableSelfAndFinish() {
        // remove this activity from the package manager.
        PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName(this, WelComeActivity.class);
        LogUtils.d("Disabling itself (" + name + ")");
        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        // terminate the activity.
        finish();
    }

    public void showFragment(Operation operation) {
        showFragment(operation, null);
    }

    public void showFragment(Operation operation, Bundle args) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (operation) {
            case welcome:
                if (welcomeFragment == null) {
                    welcomeFragment = WelcomeFragment.newInstance();
                }
                baseFragment = welcomeFragment;
                break;
            case languge:
                if (languageSettingFragment == null) {
                    languageSettingFragment = LanguageSettingFragment.newInstance();
                }
                baseFragment = languageSettingFragment;
                break;
            case region:
                if (regionFragment == null) {
                    regionFragment = RegionFragment.newInstance();
                }
                baseFragment = regionFragment;
                break;
            case wifySetting:
                if (wifiFragment == null) {
                    wifiFragment = new WifiFragment();
                }
                baseFragment = wifiFragment;
                break;
            case checkSim:
                if (simCheckFragment == null) {
                    simCheckFragment = SimCheckFragment.newInstance(null);
                }
                baseFragment = simCheckFragment;
                break;
        }
        fragmentTransaction.replace(R.id.fragment_content, baseFragment, operation.name()).commit();
    }

    private String getSettings(ContentResolver resolver, String property,
                               String overriddenValue) {
        if (overriddenValue != null) {
            LogUtils.d( "Using OVERRIDDEN value " + overriddenValue + " for property " + property);
            return overriddenValue;
        }
        String value = Settings.Secure.getString(resolver, property);
        LogUtils.d( "Using value " + overriddenValue + " for property " + property);
        return value;
    }

    private void init() {
        WifiInstance.getInstance().init(this);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "禁止返回", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.d( "onConfigurationChanged: ");
        baseFragment.languageChange();
    }
}
