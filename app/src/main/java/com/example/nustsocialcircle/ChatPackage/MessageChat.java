package com.example.nustsocialcircle.ChatPackage;

public class MessageChat extends Chat {

    private String message;

    public MessageChat() {
    }

    public MessageChat(String sendingUserID, String receivingUserID, String message) {
        super(sendingUserID, receivingUserID);
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
