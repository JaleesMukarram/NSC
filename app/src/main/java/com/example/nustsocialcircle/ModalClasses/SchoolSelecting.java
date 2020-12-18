package com.example.nustsocialcircle.ModalClasses;

public class SchoolSelecting {

    private String mSchoolName;
    private String mImageUri;
    private boolean mSelected;


    public SchoolSelecting() {
    }


    public SchoolSelecting(String mSchoolName, String mImageUri) {
        this.mSchoolName = mSchoolName;
        this.mImageUri = mImageUri;

    }

    public String getmSchoolName() {
        return mSchoolName;
    }

    public void setmSchoolName(String mSchoolName) {
        this.mSchoolName = mSchoolName;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public boolean ismSelected() {
        return mSelected;
    }

    public void setmSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}
