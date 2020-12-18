package com.example.nustsocialcircle.ChatPackage;

import java.util.Date;
import java.util.Random;

public class Chat {


    private Date sentDate;
    private Date readDate;

    private String sendingUserID;
    private String receivingUserID;

    private String chatID;

    public Chat() {
    }

    public Chat(String sendingUserID, String receivingUserID) {
        this.sendingUserID = sendingUserID;
        this.receivingUserID = receivingUserID;
        this.sentDate = new Date();
        this.chatID = computeRandomID();
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getSendingUserID() {
        return sendingUserID;
    }

    public void setSendingUserID(String sendingUserID) {
        this.sendingUserID = sendingUserID;
    }

    public String getReceivingUserID() {
        return receivingUserID;
    }

    public void setReceivingUserID(String receivingUserID) {
        this.receivingUserID = receivingUserID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String computeRandomID() {

        Random random = new Random();
        char[] c = new char[10];

        int start = 65;

        for (int i = 0; i < c.length; i++) {

            start = i % 2 == 0 ? 65 : 97;

            c[i] = (char) (start + random.nextInt(25));
        }

        return System.currentTimeMillis() + "_" + new String(c);

    }
}
