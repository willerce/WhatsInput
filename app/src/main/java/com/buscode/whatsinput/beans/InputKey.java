package com.buscode.whatsinput.beans;

import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;

import com.buscode.whatsinput.WifiInputMethod;

/**
 * User: fanxu
 * Date: 12-10-30
 */
public class InputKey extends AbstractMsg {
    public static final String TYPE = "InputKey";
    {
        type = TYPE;
    }
    public int code = -1;

    public void onMessage(WifiInputMethod service) {
        if (code < 0) {
            return;
        }

        InputConnection ic = service.getCurrentInputConnection();
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
        sleep(100);
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, code));

        sleep(500);

        InputChange change = new InputChange();
        change.text = service.getText();

        service.sendMessage(change.toJson());
    }

    private void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
