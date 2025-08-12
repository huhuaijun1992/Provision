package com.custom.provision.entity;

import android.net.wifi.ScanResult;

import androidx.annotation.Nullable;

import com.custom.provision.manager.WifiInstance;

import java.util.Objects;

/**
 * Author: created by huhuaijun on 2025/8/5 18:02
 * Function:
 */
public class WifiNetwork {
    public String ssid;
    public int level;
    public boolean isConnected;
    public ScanResult scanResult;

    public WifiNetwork(String ssid, int level, boolean isConnected, ScanResult scanResult) {
        this.ssid = ssid;
        this.level = level;
        this.isConnected = isConnected;
        this.scanResult = scanResult;

    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WifiNetwork wifiNetwork = (WifiNetwork) obj;
        return ssid == wifiNetwork.ssid; // 仅比较 ssid
    }

    @Override
    public int hashCode() {
        return Objects.hash(ssid);
    }

    public String getSsid() {
        return ssid;
    }

    public String getwifiEncryptionType() {
        return WifiInstance.getWifiEncryptionType(scanResult);
    }
}
