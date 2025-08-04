package com.custom.provision;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class WelComeActivity extends AppCompatActivity {
    private static final String TAG = WelComeActivity.class.getName();
    private WelcomeFragment welcomeFragment = new WelcomeFragment();
    private LanguageSettingFragment languageSettingFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finishSetup();
    }

    private void finishSetup() {
        setProvisioningState();
        disableSelfAndFinish();
    }

    private void setProvisioningState() {
        Log.i(TAG, "Setting provisioning state");
        // Add a persistent setting to allow other apps to know the device has been provisioned.
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
    }

    private void disableSelfAndFinish() {
        // remove this activity from the package manager.
        PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName(this, WelComeActivity.class);
        Log.i(TAG, "Disabling itself (" + name + ")");
        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        // terminate the activity.
        finish();
    }

    private void showFragment(Operation operation){
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (operation){
            case welcome:
                break;
            case languge:
                break;
            case country:
                break;
            case wifySetting:
                break;
            case checkSim:
                break;
        }
    }

    private String getSettings(ContentResolver resolver, String property,
                              String overriddenValue) {
        if (overriddenValue != null) {
            Log.w(TAG, "Using OVERRIDDEN value " + overriddenValue + " for property " + property);
            return overriddenValue;
        }
        String value = Settings.Secure.getString(resolver, property);
        Log.w(TAG, "Using value " + overriddenValue + " for property " + property);
        return value;
    }


}
