package com.custom.provision.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;

import androidx.lifecycle.MutableLiveData;

import com.custom.provision.entity.WifiNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Author: created by huhuaijun on 2025/8/5 18:41
 * Function:
 */
public class WifiManager {
    WifiReceiver wifiReceiver;
    android.net.wifi.WifiManager mWifiManager;
    Context context;
    MutableLiveData<List<WifiNetwork>> wifiNetworks = new MutableLiveData<>(new ArrayList<WifiNetwork>());

    private WifiManager() {
    }

    static class SingletonHolder {
        private static final WifiManager INSTANCE = new WifiManager();
    }

    public static WifiManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        mWifiManager = (android.net.wifi.WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void startScan() {
        if (wifiReceiver == null) {
            wifiReceiver = new WifiReceiver(new Runnable() {
                @Override
                public void run() {
                    updateWifiList();
                }
            });
        }
        context.registerReceiver(wifiReceiver, new IntentFilter(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }


    public void stopScan() {
        context.unregisterReceiver(wifiReceiver);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    private void updateWifiList() {
        List<ScanResult> results = mWifiManager.getScanResults();
        WifiInfo current = mWifiManager.getConnectionInfo();

        List<WifiNetwork> scanned = wifiNetworks.getValue();

        for (ScanResult result : results) {
            boolean isConnected = current != null && current.getSSID() != null &&
                    current.getSSID().replace("\"", "").equals(result.SSID);

            scanned.add(new WifiNetwork(
                    result.SSID,
                    result.BSSID,
                    mWifiManager.calculateSignalLevel(result.level, 5),
                    isConnected,
                    result.capabilities
            ));
        }
        Collections.sort(scanned, new Comparator<WifiNetwork>() {
            @Override
            public int compare(WifiNetwork a, WifiNetwork b) {
                if (a.isConnected && !b.isConnected) return -1;
                if (!a.isConnected && b.isConnected) return 1;
                return Integer.compare(b.level, a.level);
            }
        });
        wifiNetworks.setValue(scanned);
    }


    class WifiReceiver extends BroadcastReceiver {
        private Runnable onWifiChanged;

        public WifiReceiver(Runnable onWifiChanged) {
            this.onWifiChanged = onWifiChanged;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                if (onWifiChanged != null) {
                    onWifiChanged.run();
                }
            }
        }
    }

    public MutableLiveData<List<WifiNetwork>> getWifiNetworks() {
        return wifiNetworks;
    }
}
