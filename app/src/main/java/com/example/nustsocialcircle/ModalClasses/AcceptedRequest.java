package com.example.nustsocialcircle.ModalClasses;

import java.util.Date;
import java.util.Random;

public class AcceptedRequest {

    private Date acceptedDate;
    private Date sentDate;
    private String sendingUserID;
    private String acceptingUserID;

    private String acceptedRequestID;

    public AcceptedRequest() {
    }


    public AcceptedRequest(Date sentDate, String sendingUserID, String acceptingUserID) {

        this.sentDate = sentDate;
        this.sendingUserID = sendingUserID;
        this.acceptingUserID = acceptingUserID;
        this.acceptedDate = new Date();
        this.acceptedRequestID = (new Random()).nextInt(999999) + "";

    }


    public Date getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(Date acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getSendingUserID() {
        return sendingUserID;
    }

    public void setSendingUserID(String sendingUserID) {
        this.sendingUserID = sendingUserID;
    }

    public String getAcceptingUserID() {
        return acceptingUserID;
    }

    public void setAcceptingUserID(String acceptingUserID) {
        this.acceptingUserID = acceptingUserID;
    }

    public String getAcceptedRequestID() {
        return acceptedRequestID;
    }

    public void setAcceptedRequestID(String acceptedRequestID) {
        this.acceptedRequestID = acceptedRequestID;
    }
}
