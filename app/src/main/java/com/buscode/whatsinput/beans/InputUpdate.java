package com.buscode.whatsinput.beans;

import com.buscode.whatsinput.WifiInputMethod;

/**
 * User: fanxu
 * Date: 12-10-28
 */
public class InputUpdate extends AbstractMsg {

    public static final String TYPE = "InputUpdate";

    public void onMessage(WifiInputMethod service) {

        InputStart msg = new InputStart();
        if (service.isEditing()) {
            msg.text = service.getText();
        }
        service.sendMessage(msg.toJson());
    }
}
