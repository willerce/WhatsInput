package com.buscode.whatsinput.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * User: fanxu
 * Date: 12-10-31
 */
public class Net {
    public static final String NW_3G       = "3g";
    public static final String NW_WIFI     = "wifi";
    public static final String NW_NON      = "non";

    public static String getActiveNetwork(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info =  cm.getActiveNetworkInfo();
        if (info != null) {
            if (ConnectivityManager.TYPE_MOBILE == info.getType()) {
                return NW_3G;
            } else if (ConnectivityManager.TYPE_WIFI == info.getType()){
                return NW_WIFI;
            }
        }
        return NW_NON;
    }



    public static class WiFi {
        /**
         * Get Wifi Ip
         * @param context
         * @return
         */
        public static String getWifiIp(Context context) {
            String string_ip = "";
            WifiInfo wi = getWifiInfo(context);
            int code_ip = wi.getIpAddress();
            if (code_ip != 0) {
                string_ip = (code_ip & 0xFF) + "." +
                        ((code_ip >> 8) & 0xFF) + "." +
                        ((code_ip >> 16) & 0xFF) + "." +
                        ((code_ip >> 24) & 0xFF);
            }
            return string_ip;
        }

        /**
         * Whether wifi is connected
         * @param context
         * @return
         */
        public static boolean isWifiConnected(Context context) {
            ConnectivityManager cm = Net.getConnectivityManager(context);

            return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        }
        private static WifiInfo getWifiInfo(Context context) {
            WifiManager wm = getWifiManager(context);
            return wm.getConnectionInfo();
        }

        public static WifiManager getWifiManager(Context context) {
            return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
    }

    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
