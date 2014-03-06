package com.buscode.whatsinput.server;

import com.buscode.whatsinput.server.BackServiceListener;

interface BackServiceBinder {

    void registerListener(BackServiceListener listener);
    void startBackService();
    void stopBackService();

    boolean isBackServiceRunning();

    void sendMessage(String msg);
}