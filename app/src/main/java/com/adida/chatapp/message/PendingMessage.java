package com.adida.chatapp.message;

public class PendingMessage {
    public String sender;
    public String message;
    public int type;
    public static int TEXT = 0;
    public static int URL = 1;

    public PendingMessage() {
        sender = "";
        message = "";
        type = -1;
    }
}
