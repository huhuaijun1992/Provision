package com.custom.provision.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
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
public class WifiInstance {
    final String TAG = this.getClass().getSimpleName();
    static final  int  START_SCAN = 1000;

    boolean isScanning = false;


    WifiReceiver wifiReceiver;
    android.net.wifi.WifiManager mWifiManager;
    Context context;
    MutableLiveData<List<WifiNetwork>> wifiNetworks = new MutableLiveData<>(new ArrayList<WifiNetwork>());

    HandlerThread handlerThread = new HandlerThread("asyncThread");
    Handler handler;
    private WifiInstance() {
    }

    static class SingletonHolder {
        private static final WifiInstance INSTANCE = new WifiInstance();
    }

    public static WifiInstance getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        mWifiManager = (android.net.wifi.WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()){
            @Override
            public void dispatchMessage(@NonNull Message msg) {
                super.dispatchMessage(msg);
                if (msg.what == START_SCAN){
                    if (mWifiManager!=null && isScanning){
                       boolean result = mWifiManager.startScan();
                        Log.d(TAG, "dispatchMessage: startScan"+ result);
                        handler.sendEmptyMessageDelayed(START_SCAN,1000*10);
                    }
                }

            }
        };
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
        if (!wifiEnable()) {
           boolean result =  mWifiManager.setWifiEnabled(true);
            Log.d(TAG, "startScan: open wifi:"+ result);
        }
        handler.sendEmptyMessageDelayed(START_SCAN,500);

    }

    public boolean wifiEnable(){
        boolean wifiEnable = mWifiManager.isWifiEnabled();
        Log.d(TAG, " wifiEnable:"+ wifiEnable);
        return wifiEnable;
    }


    public void stopScan() {
        isScanning = false;
        handler.removeMessages(START_SCAN);
        context.unregisterReceiver(wifiReceiver);
        if (mWifiManager.isWifiEnabled()) {
            boolean result =mWifiManager.setWifiEnabled(false);
            Log.d(TAG, "stopScan: close wifi :"+ result);
        }
    }

    private void updateWifiList() {
        List<ScanResult> results = mWifiManager.getScanResults();
        WifiInfo current = mWifiManager.getConnectionInfo();

        List<WifiNetwork> scanned = wifiNetworks.getValue();
        scanned.clear();
        for (ScanResult result : results) {
            boolean isConnected = current != null && current.getSSID() != null &&
                    current.getSSID().replace("\"", "").equals(result.SSID);

            scanned.add(new WifiNetwork(
                    result.SSID,
                    result.BSSID,
                    WifiManager.calculateSignalLevel(result.level, 5),
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

    /**
     * 连接到指定WiFi网络
     * @param ssid 网络SSID
     * @param password 密码
     * @param encryptionType 加密类型 (WEP, WPA, WPA2, OPEN)
     * @return true表示连接请求已发送成功
     */
    public boolean connectToWifi(String ssid, String password, String encryptionType) {
        if (mWifiManager == null) {
            Log.e(TAG, "WifiManager is not initialized");
            return false;
        }

        // 确保WiFi已开启
        if (!mWifiManager.isWifiEnabled()) {
            boolean enabled = mWifiManager.setWifiEnabled(true);
            if (!enabled) {
                Log.e(TAG, "Failed to enable WiFi");
                return false;
            }
            // 等待WiFi启用
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 创建WiFi配置
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";

        // 根据加密类型设置配置
        switch (encryptionType.toUpperCase()) {
            case "WEP":
                wifiConfig.wepKeys[0] = "\"" + password + "\"";
                wifiConfig.wepTxKeyIndex = 0;
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
            case "WPA2":
                wifiConfig.preSharedKey = "\"" + password + "\"";
                break;
            case "NONE":
            case "OPEN":
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            default:
                Log.e(TAG, "Unsupported encryption type: " + encryptionType);
                return false;
        }

        // 添加网络配置
        int netId = mWifiManager.addNetwork(wifiConfig);
        if (netId == -1) {
            Log.e(TAG, "Failed to add network configuration");
            return false;
        }

        // 启用网络
        mWifiManager.disconnect();
        boolean success = mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();

        Log.d(TAG, "WiFi connection " + (success ? "initiated" : "failed"));
        return success;
    }

    /**
     * 断开当前WiFi连接
     * @return true表示断开成功
     */
    public boolean disconnectFromWifi() {
        if (mWifiManager == null) {
            Log.e(TAG, "WifiManager is not initialized");
            return false;
        }

        // 检查当前是否已连接
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
        if (connectionInfo == null || connectionInfo.getNetworkId() == -1) {
            Log.d(TAG, "Not currently connected to any WiFi");
            return false;
        }

        // 断开连接
        boolean success = mWifiManager.disconnect();
        Log.d(TAG, "WiFi disconnection " + (success ? "successful" : "failed"));
        return success;
    }

    /**
     * 获取当前连接的WiFi信息
     * @return WifiInfo对象，如果未连接则返回null
     */
    public WifiInfo getConnectedWifiInfo() {
        if (mWifiManager == null) {
            return null;
        }
        return mWifiManager.getConnectionInfo();
    }

    /**
     * 检查是否连接到指定SSID的WiFi
     * @param ssid 要检查的SSID
     * @return true表示已连接
     */
    public boolean isConnectedTo(String ssid) {
        WifiInfo wifiInfo = getConnectedWifiInfo();
        if (wifiInfo == null || wifiInfo.getSSID() == null) {
            return false;
        }
        return wifiInfo.getSSID().replace("\"", "").equals(ssid);
    }



    class WifiReceiver extends BroadcastReceiver {
        private Runnable onWifiChanged;

        public WifiReceiver(Runnable onWifiChanged) {
            this.onWifiChanged = onWifiChanged;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                Log.d(TAG, "onReceive: SCAN_RESULTS_AVAILABLE_ACTION");
                if (onWifiChanged != null) {
                    Log.d(TAG, "onReceive:  onWifiChanged.run()");
                    onWifiChanged.run();
                   
                }
            }
        }
    }



    public MutableLiveData<List<WifiNetwork>> getWifiNetworks() {
        return wifiNetworks;
    }
}
