package com.custom.provision.entity;

/**
 * Author: created by huhuaijun on 2025/8/5 18:02
 * Function:
 */
public class WifiNetwork {
    public String ssid;
    public String bssid;
    public int level;
    public boolean isConnected;
    public String capabilities;

    public WifiNetwork(String ssid, String bssid, int level, boolean isConnected, String capabilities) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.level = level;
        this.isConnected = isConnected;
        this.capabilities = capabilities;
    }
}
