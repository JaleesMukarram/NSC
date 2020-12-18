package com.example.nustsocialcircle.ModalClasses;


public class GeneralSettings {

    private String mSettingName;
    private String mSettingDescription;
    private int mSettingIcon;

    public GeneralSettings() {
    }

    public GeneralSettings(String mSettingName, String mSettingDescription, int mSettingIcon) {
        this.mSettingName = mSettingName;
        this.mSettingDescription = mSettingDescription;
        this.mSettingIcon = mSettingIcon;
    }

    public String getmSettingName() {
        return mSettingName;
    }

    public void setmSettingName(String mSettingName) {
        this.mSettingName = mSettingName;
    }

    public String getmSettingDescription() {
        return mSettingDescription;
    }

    public void setmSettingDescription(String mSettingDescription) {
        this.mSettingDescription = mSettingDescription;
    }

    public int getmSettingIcon() {
        return mSettingIcon;
    }

    public void setmSettingIcon(int mSettingIcon) {
        this.mSettingIcon = mSettingIcon;
    }
}
