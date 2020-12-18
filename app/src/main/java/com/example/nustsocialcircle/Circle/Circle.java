package com.example.nustsocialcircle.Circle;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.Random;

public class Circle {

    private String mAddingUserID;
    private String mAddedUserID;

    private String mCircleID;
    private Date mAddedDate;


    public Circle() {
    }

    public Circle(String mAddingUserID, String mAddedUserID) {
        this.mAddingUserID = mAddingUserID;
        this.mAddedUserID = mAddedUserID;
        this.mAddedDate = new Date();
        this.mCircleID = computeRandomID();
    }

    @Override
    public boolean equals(@Nullable Object obj) {


        if (obj == null) {

            return false;

        } else {

            if (obj.getClass() != this.getClass()) {
                return false;
            }

            Circle o = (Circle) obj;

            return this.getmAddingUserID().equals(o.getmAddingUserID()) &&
                    this.getmAddedUserID().equals(o.getmAddedUserID()) &&
                    this.getmCircleID().equals(o.getmCircleID()) &&
                    this.getmAddedDate().equals(o.getmAddedDate());
        }
    }

    private String computeRandomID() {

        Random random = new Random();
        char[] c = new char[10];

        int start = 65;

        for (int i = 0; i < c.length; i++) {

            start = i % 2 == 0 ? 65 : 97;

            c[i] = (char) (start + random.nextInt(25));
        }

        return System.currentTimeMillis() + "_" + new String(c);

    }

    public String getmAddingUserID() {
        return mAddingUserID;
    }

    public void setmAddingUserID(String mAddingUserID) {
        this.mAddingUserID = mAddingUserID;
    }

    public String getmAddedUserID() {
        return mAddedUserID;
    }

    public void setmAddedUserID(String mAddedUserID) {
        this.mAddedUserID = mAddedUserID;
    }

    public String getmCircleID() {
        return mCircleID;
    }

    public void setmCircleID(String mCircleID) {
        this.mCircleID = mCircleID;
    }

    public Date getmAddedDate() {
        return mAddedDate;
    }

    public void setmAddedDate(Date mAddedDate) {
        this.mAddedDate = mAddedDate;
    }
}
