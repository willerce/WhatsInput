package com.buscode.whatsinput.server;

import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * User: fanxu
 * Date: 12-10-28
 */
public class ExWebSocketServer extends WebSocketServer{

    public static interface ClientListener {
        public void onOpen(WebSocket conn, ClientHandshake handshake);
        public void onClose(WebSocket conn, int code, String reason, boolean remote);
        public void onMessage(WebSocket conn, String message);
        public void onError(WebSocket conn, Exception ex);
    }

    private static ExWebSocketServer sInstance = null;

    public static final int PORT = 6677;

    /**
     * Start a new Server
     * @param port
     * @return
     */
    public synchronized static ExWebSocketServer startNewServer(int port, ClientListener listener) throws Exception{

        if (sInstance == null) {

            //Don't have a WebSocket Server running...
            sInstance = new ExWebSocketServer(new InetSocketAddress(port));
            sInstance.setListener(listener);
            sInstance.start();
        }
        return sInstance;
    }
    public void sendMessageToAllWSClient(final String message) {

        final Set<WebSocket> conns = connections();
        if (conns == null || conns.size() == 0) {
            logger.debug("sendMessageToAllWSClient: Empty Clients");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                for(final WebSocket ws : conns) {
                    ws.send(message);
                }
            }
        }.start();
    }
    /**
     * Stop WebSocket Server
     * @throws Exception
     */
    public synchronized static void stopServer() throws Exception {
        if (sInstance == null) {
            return;
        }
        sInstance.setListener(null);
        sInstance.stop();
        sInstance = null;
    }


    /**
     * May be null...
     * @return
     */
    public static ExWebSocketServer  getRunningServer() {
        return sInstance;
    }
    //logger
    private Logger logger = Logger.getLogger("ExWebSocketServer@" + hashCode());


    private ExWebSocketServer(InetSocketAddress address) throws UnknownHostException {
        super(address);
    }

    private ClientListener mClientLister = null;

    public void setListener(ClientListener listener) {
        mClientLister = listener;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        logger.debug("onOpen: " /*+ conn.getRemoteSocketAddress().getHostName().toString()*/);

        if (mClientLister != null) {
            mClientLister.onOpen(conn, handshake);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.debug("onClose: ");
        if (mClientLister != null) {
            mClientLister.onClose(conn, code, reason, remote);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.debug("onMessage: " + message);
        if (mClientLister != null) {
            mClientLister.onMessage(conn, message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.debug("onError: ");
        if (mClientLister != null) {
            mClientLister.onError(conn, ex);
        }
    }
}

