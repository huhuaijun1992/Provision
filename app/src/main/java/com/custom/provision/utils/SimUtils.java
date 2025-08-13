package com.custom.provision.utils;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;


public class SimUtils {

    public static boolean hasSimCard(Context context) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
            return subscriptionManager.getActiveSubscriptionInfoCount() > 0;
        } else {
            int simState = telephonyManager.getSimState();
            return simState != TelephonyManager.SIM_STATE_ABSENT && simState != TelephonyManager.SIM_STATE_UNKNOWN;
        }
    }

    public static boolean isMobileNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network[] allNetworks = cm.getAllNetworks();
            for (Network network : allNetworks) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                if (nc != null && nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                }
            }
            return false;
        } else {
            // 旧版兼容
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null
                    && activeNetwork.isConnected()
                    && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
    }
}

