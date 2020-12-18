package com.example.nustsocialcircle.ModalClasses;

import java.util.Date;

public class RejectedRequest {

    private Date rejectedDate;
    private Date sentDate;
    private String sendingUserID;
    private String rejectingUserID;


    public RejectedRequest() {
    }

    public RejectedRequest(Date sentDate, String sendingUserID, String rejectingUserID) {

        this.rejectedDate = new Date();
        this.sentDate = sentDate;
        this.sendingUserID = sendingUserID;
        this.rejectingUserID = rejectingUserID;
    }

    public Date getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
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

    public String getRejectingUserID() {
        return rejectingUserID;
    }

    public void setRejectingUserID(String rejectingUserID) {
        this.rejectingUserID = rejectingUserID;
    }
}
