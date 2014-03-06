package com.buscode.whatsinput.beans;

import com.buscode.whatsinput.WifiInputMethod;

/**
 * User: fanxu
 * Date: 12-10-28
 */
public class InputEdit extends AbstractMsg {
    public static final String TYPE = "InputEdit";

    public String text = "";

    public void onMessage(WifiInputMethod service) {
       service.setText(text);
    }
}
