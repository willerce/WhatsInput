package com.buscode.whatsinput;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.buscode.whatsinput.server.*;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

/**
 * User: fanxu
 * Date: 12-10-31
 */
public class BackService extends Service {

    //logger
    private Logger logger = Logger.getLogger("BackService" + hashCode());

    private BackServiceListener mBackServiceListener = null;

    private IBinder mBinder = new BackServiceBinder.Stub() {
        @Override
        public boolean isBackServiceRunning() throws RemoteException {
            return BackService.this.isServersRunning();
        }

        @Override
        public void registerListener(BackServiceListener listener) throws RemoteException {
            logger.debug("registerListener:  listener == null" + (listener == null) );
            mBackServiceListener = listener;
        }

        @Override
        public void startBackService() throws RemoteException {
            logger.debug("HttpServiceBinder.startHttpServer: ");
            BackService.this.startBackServersInThread();
        }

        @Override
        public void stopBackService() throws RemoteException {
            logger.debug("HttpServiceBinder.stopHttpServer: ");
            BackService.this.stopBackServersInThread();
        }

        @Override
        public void sendMessage(String msg) throws RemoteException {
            ExWebSocketServer ws = ExWebSocketServer.getRunningServer();
            if (ws != null) {
                ws.sendMessageToAllWSClient(msg);
            }
        }
    };

    private boolean isServersRunning() {
        return ExHttpServer.isRunning();
    }

    public static final String ACTION_BIND_BY_ACTIVITY = "com.sand.airinput.action.bind_by_activity";
    public static final String ACTION_BIND_BY_INPUT_SERVICE = "com.sand.airinput.action.bind_by_input_service";

    @Override
    public IBinder onBind(Intent intent) {
        logger.debug("onBind: ");

//        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
//            return null;
//        }

//        if (ACTION_BIND_BY_ACTIVITY.equals(intent.getAction())) {
//            return mBinder;
//        } else if (ACTION_BIND_BY_INPUT_SERVICE.equals(intent.getAction())) {
//            return mBinder;
//        }

        if (!isServersRunning()) {
            startBackServersInThread();
        }

        ExWebSocketServer ws = ExWebSocketServer.getRunningServer();
        if (ws != null) {
            ws.setListener(mWSClientListener);
        }
        return mBinder;
    }
    private ExWebSocketServer.ClientListener mWSClientListener = new ExWebSocketServer.ClientListener() {
        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            BackService.this.onOpen();
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            BackService.this.onMessage(message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {

        }
    };

    private void onOpen() {
        try {
            if (mBackServiceListener != null) {
                mBackServiceListener.onOpen();
            }
        } catch (Exception e) {

        }
    }
    private void onMessage(String message) {

        try {
            if (mBackServiceListener != null) {
                mBackServiceListener.onMessage(message);
            }
        } catch (Exception e) {

        }
    }

    private void startBackServersInThread() {
        logger.debug("Start severs...");
        new Thread() {
            @Override
            public void run() {
                startHttpServer();
                startWebSocketServer();
                onBackServerStart();
            }
        }.start();
    }

    private void startWebSocketServer() {
        try {
            mServer = ExWebSocketServer.getRunningServer();
            if (mServer != null) {
                mServer.setListener(null);
                ExWebSocketServer.stopServer();
            }
            mServer = ExWebSocketServer.startNewServer(ExWebSocketServer.PORT, mWSClientListener);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
    }

    private ExWebSocketServer mServer = null;

    public void startHttpServer() {
        try {
            //Init configs
            ExHttpConfig config = ExHttpConfig.getInstance();
            config.init(this);

            ExHttpServer.startNewServer(this, config.port);
            config.setListeningState();
        } catch (Exception e) {
            e.printStackTrace();
            //Failed
        }
    }

    public void stopBackServersInThread() {
        new Thread() {
            @Override
            public void run() {
                stopHttpServer();
                stopWebSocketServer();
                onBackServerStop();
            }
        }.start();
    }

    private void stopWebSocketServer() {
        mServer = ExWebSocketServer.getRunningServer();
        if (mServer != null) {
            mServer.setListener(null);
            try {
                ExWebSocketServer.stopServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopHttpServer() {
        ExHttpConfig.getInstance().setStoppedState();
        ExHttpServer.stopServer();
    }
    private void onBackServerStart() {

        try{
            if (mBackServiceListener != null) {
                mBackServiceListener.onStart();
            }
            //ServerNotification.showServerNotification(this);
        } catch (Exception e) {

        }
    }
    private void onBackServerStop() {

        try{
            if (mBackServiceListener != null) {
                mBackServiceListener.onStop();
            }
            //ServerNotification.cancelAll(this);
        } catch (Exception e) {

        }

    }
}
