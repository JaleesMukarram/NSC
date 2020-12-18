package com.example.nustsocialcircle.PostModalClasses;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class GeneralPost implements Serializable {

    public static final int IMAGE_POST_CLASS = 10;

    private String uploadingUserUID;
    private String postID;

    private int likes;
    private int dislikes;
    private int shares;

    private int postType;
    private long uploadMillis;

    public GeneralPost() {
    }

    // Called when the post will be created
    public GeneralPost(String uploadingUserUID, int postType) {

        this.uploadingUserUID = uploadingUserUID;
        this.postType = postType;
        this.postID = computeRandomID();
        this.uploadMillis = System.currentTimeMillis();
    }

    public static int getImagePostClass() {
        return IMAGE_POST_CLASS;
    }

    public String getUploadingUserUID() {
        return uploadingUserUID;
    }

    public void setUploadingUserUID(String uploadingUserUID) {
        this.uploadingUserUID = uploadingUserUID;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public long getUploadMillis() {
        return uploadMillis;
    }

    public void setUploadMillis(long uploadMillis) {
        this.uploadMillis = uploadMillis;
    }


    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
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

    public Date getUploadDate() {

        if (uploadMillis > 0) {
            return new Date(uploadMillis);
        }

        return null;
    }

}
