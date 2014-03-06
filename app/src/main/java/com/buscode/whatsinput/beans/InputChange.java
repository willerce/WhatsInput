package com.buscode.whatsinput.beans;

/**
 * User: fanxu
 * Date: 12-11-1
 */
public class InputChange extends AbstractMsg {
    public static final String TYPE = "InputChange";

    {
        type = TYPE;
    }
    public String text = "";
}
