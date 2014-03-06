package com.buscode.whatsinput.server;

interface BackServiceListener {

    void onStart();
    void onStop();

    void onMessage(String msg);
    void onOpen();
}