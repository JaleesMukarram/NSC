package com.example.nustsocialcircle.HelpingClasses;

import com.example.nustsocialcircle.FirebaseHelper.FirebaseDatabaseLoadAllCircledUsers;
import com.example.nustsocialcircle.Interfaces.FirebaseDatabaseDownloadListener;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;

import java.util.List;

public class TemporaryHolders {

    private List<GeneralUser> circleGeneralUsers;
    private String myID;

    public TemporaryHolders(String myID) {
        this.myID = myID;
    }

    public List<GeneralUser> getCircleGeneralUsers() {
        return circleGeneralUsers;
    }

    public void setCircleGeneralUsers(List<GeneralUser> circleGeneralUsers) {
        this.circleGeneralUsers = circleGeneralUsers;
    }

    public void downloadCircledGeneralUsers() {

        FirebaseDatabaseLoadAllCircledUsers users = new FirebaseDatabaseLoadAllCircledUsers(myID);
        users.setListener(new FirebaseDatabaseDownloadListener() {
            @Override
            public void onSuccess(Object object) {

                List<GeneralUser> post = (List<GeneralUser>) object;
                circleGeneralUsers = post;


            }

            @Override
            public void onFailure() {


            }
        });
    }

    public boolean isCircleListLoaded() {

        return circleGeneralUsers != null;
    }
}
