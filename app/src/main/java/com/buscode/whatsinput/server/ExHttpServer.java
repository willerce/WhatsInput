package com.buscode.whatsinput.server;

import android.content.Context;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * User: fanxu
 * Date: 12-10-28
 */
public class ExHttpServer {

    public static final int PORT = 6688;

    //logger
    private Logger logger = Logger.getLogger("ExHttpServer@" + hashCode());

    public synchronized static ExHttpServer startNewServer(Context context, int port) throws IOException {

        if (sInstance == null) {
            sInstance = new ExHttpServer();
            sInstance.start(context, port);
        }
        return sInstance;
    }

    public synchronized static void stopServer() {
        if (sInstance != null) {
            sInstance.stop();
            sInstance = null;
        }
    }

    public static ExHttpServer getRunningServer() {
        return sInstance;
    }

    public static boolean isRunning() {
        return sInstance != null;
    }

    //Single instance
    private ExHttpServer(){}
    private static ExHttpServer sInstance;


    private NanoHTTPD mHttpServer;

    private void start(Context context, int port) throws IOException {
        mHttpServer = new NanoHTTPD(port, context.getAssets());
    }
    private void stop() {
        if (mHttpServer != null) {
            mHttpServer.stop();
            mHttpServer = null;
        }
    }
}