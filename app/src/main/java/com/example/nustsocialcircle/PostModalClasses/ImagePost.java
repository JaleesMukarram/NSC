package com.example.nustsocialcircle.PostModalClasses;

import androidx.annotation.Nullable;

public class ImagePost extends GeneralPost {

    public static final String DEFAULT_DESCRIPTION = "";

    private String imageURL;
    private String imageDescription;


    public ImagePost() {
    }

    //Used for creating new ImagePost
    public ImagePost(String uploadingUserUID, String description) {

        super(uploadingUserUID, GeneralPost.IMAGE_POST_CLASS);

        if (description != null && !"".equals(description)) {

            this.imageDescription = description;

        } else {

            this.imageDescription = DEFAULT_DESCRIPTION;
        }
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == null) {

            return false;

        } else if (!this.getClass().equals(obj.getClass())) {

            return false;

        } else {

            ImagePost o = (ImagePost) obj;

            return
                    this.getUploadingUserUID().equals(o.getUploadingUserUID()) &&
                            this.getPostID().equals(o.getPostID()) &&
                            this.getPostType() == o.getPostType() &&
                            this.getUploadMillis() == o.getUploadMillis() &&

                            this.imageDescription.equals(o.imageDescription) &&
                            this.imageURL.equals(o.imageURL);

        }
    }
}
