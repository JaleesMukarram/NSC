package com.example.nustsocialcircle.ModalClasses;

import java.util.Date;
import java.util.Random;

public class FriendRequest {

    private String mSendingUserID;
    private String mSendToUserID;
    private Date mSendingDate;
    private Date mAcceptedDate;

    private String mRequestID;

    private boolean isRequestAccepted;
    private boolean isRequestRejected;

    public FriendRequest() {
    }

    public FriendRequest(String mSendingUserID, String mSendToUserID, Date mSendingDate) {
        this.mSendingUserID = mSendingUserID;
        this.mSendToUserID = mSendToUserID;
        this.mSendingDate = mSendingDate;
        this.mRequestID = new String(new Random().nextInt(9999999) + "");
    }

    public FriendRequest(String mSendingUserID, String mSendToUserID, Date mSendingDate, Date mAcceptedDate, boolean isRequestAccepted, boolean isRequestRejected) {
        this.mSendingUserID = mSendingUserID;
        this.mSendToUserID = mSendToUserID;
        this.mSendingDate = mSendingDate;
        this.mAcceptedDate = mAcceptedDate;
        this.isRequestAccepted = isRequestAccepted;
        this.isRequestRejected = isRequestRejected;
    }

    public String getmSendingUserID() {
        return mSendingUserID;
    }

    public void setmSendingUserID(String mSendingUserID) {
        this.mSendingUserID = mSendingUserID;
    }

    public String getmSendToUserID() {
        return mSendToUserID;
    }

    public void setmSendToUserID(String mSendToUserID) {
        this.mSendToUserID = mSendToUserID;
    }

    public Date getmSendingDate() {
        return mSendingDate;
    }

    public void setmSendingDate(Date mSendingDate) {
        this.mSendingDate = mSendingDate;
    }

    public Date getmAcceptedDate() {
        return mAcceptedDate;
    }

    public void setmAcceptedDate(Date mAcceptedDate) {
        this.mAcceptedDate = mAcceptedDate;
    }

    public String getmRequestID() {
        return mRequestID;
    }

    public boolean isRequestAccepted() {
        return isRequestAccepted;
    }

    public void setRequestAccepted(boolean requestAccepted) {
        isRequestAccepted = requestAccepted;
    }

    public boolean isRequestRejected() {
        return isRequestRejected;
    }

    public void setRequestRejected(boolean requestRejected) {
        isRequestRejected = requestRejected;
    }
}
