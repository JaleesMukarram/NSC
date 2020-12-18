package com.example.nustsocialcircle.ModalClasses;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

public class GeneralUser implements Serializable {

    private String mName;
    private String mSchool;
    private String mSection;
    private Date mCreationDate;
    private String mProfileUri;
    private String mBadge;
    private String mProgram;
    private String mUid;


    public GeneralUser() {
    }

    public GeneralUser(String mName, String mSchool, String mSection) {
        this.mName = mName;
        this.mSchool = mSchool;
        this.mSection = mSection;
        this.mCreationDate = new Date();
    }

    public GeneralUser(String mName, String mSchool, String mSection, String mBadge, String mUid) {
        this.mName = mName;
        this.mSchool = mSchool;
        this.mSection = mSection;
        this.mBadge = mBadge;
        this.mCreationDate = new Date();
        this.mUid = mUid;
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSchool() {
        return mSchool;
    }

    public void setmSchool(String mSchool) {
        this.mSchool = mSchool;
    }

    public String getmSection() {
        return mSection;
    }

    public void setmSection(String mSection) {
        this.mSection = mSection;
    }

    public Date getmCreationDate() {
        return mCreationDate;
    }

    public void setmCreationDate(Date mCreationDate) {
        this.mCreationDate = mCreationDate;
    }

    public String getmProfileUri() {
        return mProfileUri;
    }

    public void setmProfileUri(String mProfileUri) {
        this.mProfileUri = mProfileUri;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public boolean isUserValid() {

        if (this.mName != null && this.mSchool != null && this.mSection != null) {
            return true;
        } else {
            return false;
        }
    }

    public String getmBadge() {
        return mBadge;
    }

    public void setmBadge(String mBadge) {
        this.mBadge = mBadge;
    }

    public String getmProgram() {
        return mProgram;
    }

    public void setmProgram(String mProgram) {
        this.mProgram = mProgram;
    }

    public HashMap<String, Object> getHashMap() throws IllegalAccessException {

        HashMap<String, Object> map = new HashMap<>();

        Field[] fields = this.getClass().getDeclaredFields();

        for (Field f : fields) {

            map.put(f.getName(), f.get(this));
        }

        return map;
    }

    @NonNull
    @Override
    public String toString() {

        return "Name: " + this.mName + " School: " + mSchool + " Section: " + mSection;
    }
}
