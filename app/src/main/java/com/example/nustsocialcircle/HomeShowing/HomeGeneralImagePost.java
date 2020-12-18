package com.example.nustsocialcircle.HomeShowing;

import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.example.nustsocialcircle.PostModalClasses.ImagePost;

public class HomeGeneralImagePost {

    private ImagePost imagePost;
    private GeneralUser user;

    public HomeGeneralImagePost() {
    }

    public HomeGeneralImagePost(ImagePost imagePost, GeneralUser user) {
        this.imagePost = imagePost;
        this.user = user;
    }

    public ImagePost getImagePost() {
        return imagePost;
    }

    public void setImagePost(ImagePost imagePost) {
        this.imagePost = imagePost;
    }

    public GeneralUser getUser() {
        return user;
    }

    public void setUser(GeneralUser user) {
        this.user = user;
    }
}
