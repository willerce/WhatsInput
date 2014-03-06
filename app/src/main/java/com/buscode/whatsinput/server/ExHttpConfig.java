package com.buscode.whatsinput.server;

import android.content.Context;
import android.text.TextUtils;
import com.buscode.whatsinput.common.Net;

/**
 * User: fanxu
 * Date: 12-10-31
 */
public class ExHttpConfig {
    //Single instance
    private ExHttpConfig(){}
    private static ExHttpConfig sInstance;
    public synchronized static ExHttpConfig getInstance() {
        if ( sInstance == null ) {
            sInstance = new ExHttpConfig();
        }
        return sInstance;
    }

    //Ip
    public String ip = "";

    //Port
    public int port = ExHttpServer.PORT;

    public String getLocalAddress() {
        if (TextUtils.isEmpty(ip)) {
            return "";
        }
        return String.format("http://%s:%d", ip, port);
    }
    //Password
    public String pwd = "";

    public static final int STATE_STOPPED = 0;
    public static final int STATE_LISTENING = 1;

    public int state = STATE_STOPPED;

    public void setStoppedState() {
        state = STATE_STOPPED;
    }
    public void setListeningState() {
        state = STATE_LISTENING;
    }

    public synchronized void init(Context context) throws Exception {
        if (!Net.WiFi.isWifiConnected(context)) {
            throw new IllegalArgumentException("Wifi is not connected...");
        }

        ip = Net.WiFi.getWifiIp(context);
        port = ExHttpServer.PORT;

        //Here should read password from Preference
        pwd = "1234";
    }
}
