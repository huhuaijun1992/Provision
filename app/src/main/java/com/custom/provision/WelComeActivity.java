package com.custom.provision;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.custom.provision.fragment.LanguageSettingFragment;
import com.custom.provision.fragment.RegionFragment;
import com.custom.provision.fragment.SimCheckFragment;
import com.custom.provision.fragment.WelcomeFragment;
import com.custom.provision.fragment.WifiFragment;
import com.custom.provision.manager.WifiManager;

public class WelComeActivity extends AppCompatActivity {
    private static final String TAG = WelComeActivity.class.getName();
    private WelcomeFragment welcomeFragment;
    private LanguageSettingFragment languageSettingFragment;
    private WifiFragment wifiFragment;
    private RegionFragment regionFragment;
    private SimCheckFragment simCheckFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        init();
        showFragment(Operation.welcome);
//        finishSetup();
    }

    public void finishSetup() {
        try {
            setProvisioningState();
        }catch (Exception e){
            Toast.makeText(this,"权限错误",Toast.LENGTH_SHORT).show();
        }
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

    public void showFragment(Operation operation) {
        showFragment(operation, null);
    }

    public void showFragment(Operation operation, Bundle args) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        switch (operation) {
            case welcome:
                if (welcomeFragment == null) {
                    welcomeFragment = WelcomeFragment.newInstance();
                }
                fragment = welcomeFragment;
                break;
            case languge:
                if (languageSettingFragment == null) {
                    languageSettingFragment = LanguageSettingFragment.newInstance();
                }
                fragment = languageSettingFragment;
                break;
            case region:
                if (regionFragment == null) {
                    regionFragment = RegionFragment.newInstance();
                }
                fragment = regionFragment;
                break;
            case wifySetting:
                if (wifiFragment == null) {
                    wifiFragment = new WifiFragment();
                }
                fragment = wifiFragment;
                break;
            case checkSim:
                if (simCheckFragment == null) {
                    simCheckFragment = SimCheckFragment.newInstance(null);
                }
                fragment = simCheckFragment;
                break;
        }
        fragmentTransaction.replace(R.id.fragment_content, fragment, operation.name()).commit();
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

    private void init() {
        WifiManager.getInstance().init(this);
    }


}
