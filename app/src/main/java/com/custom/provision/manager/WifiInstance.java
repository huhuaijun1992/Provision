package com.custom.provision.manager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;

import com.custom.provision.entity.WifiNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author: created by huhuaijun on 2025/8/5 18:41
 * Function:
 */
public class WifiInstance {
    static final String TAG = WifiInstance.class.getSimpleName();
    static final  int  START_SCAN = 1000;

    boolean isScanning = false;


    WifiReceiver wifiReceiver;
    WifiManager mWifiManager;
    Context context;
    MutableLiveData<List<WifiNetwork>> wifiNetworks = new MutableLiveData<>(new ArrayList<WifiNetwork>());

    HandlerThread handlerThread = new HandlerThread("asyncThread");
    Handler handler;
    ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onLost(Network network) {
            Log.d(TAG, "Wi-Fi 已断开");
        }

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Log.d(TAG, "Wi-Fi 已连接");
            updateWifiList();
        }
    };

    public MutableLiveData<WifiNetwork> currentWaitConnectWifiNetwork = new MutableLiveData<>();

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
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(wifiReceiver, filter);
        if (!wifiEnable()) {
           boolean result =  mWifiManager.setWifiEnabled(true);
            Log.d(TAG, "startScan: open wifi:"+ result);
        }
        isScanning = true;
        handler.sendEmptyMessageDelayed(START_SCAN,500);

    }

    public boolean wifiEnable(){
        boolean wifiEnable = mWifiManager.isWifiEnabled();
        Log.d(TAG, " wifiEnable:"+ wifiEnable);
        return wifiEnable;
    }


    public void stopScan() {
        try {
            isScanning = false;
            handler.removeMessages(START_SCAN);
            context.unregisterReceiver(wifiReceiver);
            if (mWifiManager.isWifiEnabled()) {
                boolean result =mWifiManager.setWifiEnabled(false);
                Log.d(TAG, "stopScan: close wifi :"+ result);
            }
        }catch (Exception e){
            Log.d(TAG, "stopScan: "+ e.getCause());
        }
    }

    private void updateWifiList() {
        List<ScanResult> results = mWifiManager.getScanResults();
        WifiInfo current = mWifiManager.getConnectionInfo();

        // 创建一个Map来存储每个SSID对应的最强信号ScanResult
        Map<String, ScanResult> strongestSignals = new HashMap<>();

        for (ScanResult result : results) {
            if (!TextUtils.isEmpty(result.SSID)) {
                // 如果这个SSID还没有记录，或者当前信号更强，则更新
                if (!strongestSignals.containsKey(result.SSID) ||
                        result.level > strongestSignals.get(result.SSID).level) {
                    strongestSignals.put(result.SSID, result);
                }
            }
        }

        List<WifiNetwork> scanned = new ArrayList<>();
        for (ScanResult result : strongestSignals.values()) {
            boolean isConnected = current != null && current.getSSID() != null &&
                    current.getSSID().replace("\"", "").equals(result.SSID);

            scanned.add(new WifiNetwork(
                    result.SSID,
                    WifiManager.calculateSignalLevel(result.level, 5),
                    isConnected,
                    result
            ));
        }

        // 排序：已连接的排在最前，其余按信号强度排序
        Collections.sort(scanned, new Comparator<WifiNetwork>() {
            @Override
            public int compare(WifiNetwork a, WifiNetwork b) {
                if (a.isConnected && !b.isConnected) return -1;
                if (!a.isConnected && b.isConnected) return 1;
                return Integer.compare(b.level, a.level);
            }
        });
           if (Looper.getMainLooper().getThread() == Thread.currentThread()){
               wifiNetworks.setValue(scanned);
           }else {
               wifiNetworks.postValue(scanned);
           }

    }

    /**
     * 连接到指定WiFi网络
     * @param ssid 网络SSID
     * @param password 密码
     * @param encryptionType 加密类型 (WEP, WPA, WPA2, OPEN)
     * @return true表示连接请求已发送成功
     */
    public boolean connectToWifi(String ssid, String password, String encryptionType) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\""; // SSID 需要加引号

        // 根据加密方式设置
        switch (encryptionType.toUpperCase()) {
            case "WPA2":
            case "WPA3":
                config.preSharedKey = "\"" + password + "\"";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                break;
            case "WEP":
                config.wepKeys[0] = "\"" + password + "\"";
                config.wepTxKeyIndex = 0;
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                break;
            default: // 开放网络
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        // 添加并连接网络
        int netId = wifiManager.addNetwork(config);
        if (netId != -1) {
            wifiManager.enableNetwork(netId, true); // true 表示强制连接
            wifiManager.saveConfiguration(); // 保存配置（可选）
            Log.d(TAG, "Wi-Fi 配置添加成功: " + ssid);
        } else {
            Log.e(TAG, "Wi-Fi 配置添加失败");
        }

        // 启用网络
        mWifiManager.disconnect();
        boolean success = mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();

        Log.d(TAG, "WiFi connection " + (success ? "initiated" : "failed"));
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
     * 根据 ScanResult 连接 Wi-Fi（自动判断版本）
     */
    @SuppressLint("MissingPermission")
    public  void connect(Context context, ScanResult scanResult, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            connectWithSuggestion(context, scanResult, password);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            connectWithSpecifier(context, scanResult, password);
        } else {
            connectOld(context, scanResult, password);
        }
    }

    /**
     * 断开 Wi-Fi（自动判断版本）
     */
    @SuppressLint("MissingPermission")
    public  void disconnect(Context context, String SSID, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            disconnectWithSuggestion(context, SSID, password);
            disconnectWithSpecifier(context);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            disconnectWithSpecifier(context);
        } else {
            disconnectOld(context);
        }
    }

    // ---------------- Android 9 及以下 ----------------
    @SuppressLint("MissingPermission")
    private static void connectOld(Context context, ScanResult scanResult, String password) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return;

        // 查找已有配置
        WifiConfiguration existingConfig = null;
        for (WifiConfiguration config : wifiManager.getConfiguredNetworks()) {
            if (config.SSID != null && config.SSID.equals("\"" + scanResult.SSID + "\"")) {
                existingConfig = config;
                break;
            }
        }

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + scanResult.SSID + "\"";

        if (scanResult.capabilities.contains("WEP")) {
            config.wepKeys[0] = "\"" + password + "\"";
            config.wepTxKeyIndex = 0;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        } else if (scanResult.capabilities.contains("WPA")) {
            config.preSharedKey = "\"" + password + "\"";
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        wifiManager.disconnect();

        int networkId;
        if (existingConfig != null) {
            networkId = wifiManager.updateNetwork(config);
        } else {
            networkId = wifiManager.addNetwork(config);
        }
        if (networkId == -1) {
            Log.e(TAG, "Failed to add/update Wi-Fi configuration for SSID: " + scanResult.SSID);
            return;
        }

        boolean enabled = wifiManager.enableNetwork(networkId, true);
        if (!enabled) {
            Log.e(TAG, "Failed to enable network with ID: " + networkId);
            return;
        }

//        boolean reconnect = wifiManager.reconnect();
//        if (!reconnect) {
//            Log.e(TAG, "Failed to reconnect to Wi-Fi");
//        } else {
//            Log.d(TAG, "Connecting to Wi-Fi SSID: " + scanResult.SSID);
//        }
    }

    @SuppressLint("MissingPermission")
    private static void disconnectOld(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean disconnectResult = wifiManager.disconnect();
        Log.d(TAG, "已断开 Wi-Fi（旧版 API）"+ disconnectResult);
    }

    // ---------------- Android 10 ----------------
    @SuppressLint("MissingPermission")
    private void connectWithSpecifier(Context context, ScanResult scanResult, String password) {
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder()
                .setSsid(scanResult.SSID);

        if (scanResult.capabilities.contains("WPA3")) {
            builder.setWpa3Passphrase(password);
        } else if (scanResult.capabilities.contains("WPA")) {
            builder.setWpa2Passphrase(password);
        } else {
            builder.setIsHiddenSsid(false);
        }

        WifiNetworkSpecifier specifier = builder.build();

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.requestNetwork(request, callback);
    }

    @SuppressLint("MissingPermission")
    private  void disconnectWithSpecifier(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        // 注册回调监听断开事件
        cm.requestNetwork(request, callback); // 会触发系统权限弹窗
        cm.unregisterNetworkCallback(callback); // 立即取消请求以断开
        Log.d(TAG, "已断开 Wi-Fi（Android 10）");
    }

    // ---------------- Android 11+ ----------------
    @SuppressLint("MissingPermission")
    private void connectWithSuggestion(Context context, ScanResult scanResult, String password) {

        boolean  wifiNetworkSuggestionExist = false;
        List<WifiNetworkSuggestion> networkSuggestions = mWifiManager.getNetworkSuggestions();
        if (networkSuggestions != null && !networkSuggestions.isEmpty()) {
            for (WifiNetworkSuggestion suggestion : networkSuggestions) {
                if (suggestion.getSsid().equals(scanResult.SSID)) {
                   wifiNetworkSuggestionExist = true;
                    break;
                }
            }
        }
        if (!wifiNetworkSuggestionExist){
            Log.d(TAG, "connectWithSuggestion: 无wifi config 添加配置");
            WifiNetworkSuggestion.Builder builder = new WifiNetworkSuggestion.Builder()
                    .setSsid(scanResult.SSID);
            if (scanResult.capabilities.contains("WPA3")) {
                builder.setWpa3Passphrase(password);
            } else if (scanResult.capabilities.contains("WPA")) {
                builder.setWpa2Passphrase(password);
            } else {
                builder.setIsHiddenSsid(false);
            }

            WifiNetworkSuggestion suggestion = builder.build();
            List<WifiNetworkSuggestion> suggestions = new ArrayList<>();
            suggestions.add(suggestion);
            int status = mWifiManager.addNetworkSuggestions(suggestions);
            if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                Log.d(TAG, "已添加 Wi-Fi 建议（Android 11+）: " + scanResult.SSID);
                connectWithSpecifier(context, scanResult, password);
            } else {
                Log.e(TAG, "添加 Wi-Fi 建议失败: " + status);
            }
        }else {
            Log.d(TAG, "connectWithSuggestion: 已有wifi配置，直接连接");
            connectWithSpecifier(context,scanResult,password);
        }
    }


    @SuppressLint("MissingPermission")
    private  void disconnectWithSuggestion(Context context, String SSID, String password) {
        List<WifiNetworkSuggestion> suggestions = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            suggestions = mWifiManager.getNetworkSuggestions();
            mWifiManager.removeNetworkSuggestions(suggestions);
        }
        Log.d(TAG, "已移除 Wi-Fi 建议（Android 11+）: " + SSID);
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
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.isConnected()) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    Log.d("WifiReceiver", "已连接 WiFi: " + ssid);
                } else {
                    Log.d("WifiReceiver", "WiFi 已断开");
                }
                updateWifiList();
            }

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                Log.d("WifiReceiver", "WiFi 开关状态: " + state);
                updateWifiList();

            }
        }
    }



    public MutableLiveData<List<WifiNetwork>> getWifiNetworks() {
        return wifiNetworks;
    }

    public MutableLiveData<WifiNetwork> getCurrentWaitConnectWifiNetwork() {
        return currentWaitConnectWifiNetwork;
    }

    /**
     * 获取 Wi-Fi 加密类型
     * @param scanResult ScanResult 对象
     * @return 加密类型（WPA3, WPA2, WEP, OPEN）
     */
    public static String getWifiEncryptionType(ScanResult scanResult) {
        String capabilities = scanResult.capabilities.toUpperCase(); // 转大写方便判断

        if (capabilities.contains("WPA3")) {
            return "WPA3";
        } else if (capabilities.contains("WPA2")) {
            return "WPA2";
        } else if (capabilities.contains("WPA")) {
            return "WPA";
        } else if (capabilities.contains("WEP")) {
            return "WEP";
        } else {
            return "OPEN"; // 开放网络
        }
    }
}
