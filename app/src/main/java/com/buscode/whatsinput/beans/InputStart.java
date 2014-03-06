package com.buscode.whatsinput.beans;

/**
 * User: fanxu
 * Date: 12-10-28
 */
public class InputStart extends AbstractMsg {
    public static final String TYPE = "InputStart";

    {
        //Init Type;
        type = TYPE;
    }
    public String text;
}
