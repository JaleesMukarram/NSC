package com.example.nustsocialcircle.ModalClasses;

import java.util.Date;

public class Unfriended {

    String unFriendingUserID;
    String unFriendedUserID;

    Date unfriendedDate;

    public Unfriended() {
    }

    public Unfriended(String unFriendingUserID, String unFriendedUserID) {
        this.unFriendingUserID = unFriendingUserID;
        this.unFriendedUserID = unFriendedUserID;
        this.unfriendedDate = new Date();
    }

    public String getUnFriendingUserID() {
        return unFriendingUserID;
    }

    public void setUnFriendingUserID(String unFriendingUserID) {
        this.unFriendingUserID = unFriendingUserID;
    }

    public String getUnFriendedUserID() {
        return unFriendedUserID;
    }

    public void setUnFriendedUserID(String unFriendedUserID) {
        this.unFriendedUserID = unFriendedUserID;
    }

    public Date getUnfriendedDate() {
        return unfriendedDate;
    }

    public void setUnfriendedDate(Date unfriendedDate) {
        this.unfriendedDate = unfriendedDate;
    }
}
