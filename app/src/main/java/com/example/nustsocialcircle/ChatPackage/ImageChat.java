package com.example.nustsocialcircle.ChatPackage;

import android.net.Uri;

public class ImageChat extends Chat {

    private Uri imageUri;


    public ImageChat() {
    }

    public ImageChat(String sendingUserID, String receivingUserID, Uri imageUri) {
        super(sendingUserID, receivingUserID);
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
