package com.example.nustsocialcircle.FirebaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.nustsocialcircle.HomeShowing.HomeGeneralImagePost;
import com.example.nustsocialcircle.Interfaces.FirebaseDatabaseDownloadListener;
import com.example.nustsocialcircle.Interfaces.FirebaseDatabaseDualRetrieval;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.example.nustsocialcircle.PostModalClasses.ImagePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FirebaseCompletePostImageForHomeLoader implements FirebaseDatabaseDualRetrieval {

    public static final String TAG = "FirebaseDualTAG";
    private static final DatabaseReference IMAGE_POST_REFERENCE = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserPOSTs.GENERAL_USER_IMAGE_POSTS_REFERENCE);
    private static final DatabaseReference GENERAL_USER_REFERENCE = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE);
    private static final int MAX_TRIES_FOR_FETCHING = 2;

    private FirebaseDatabaseDownloadListener listener;


    private String imagePostID;
    private ImagePost downloadedImagePost;
    private GeneralUser downloadedGeneralUser;

    private boolean firstObjectFetched;


    private int currentTriesForFetchingImagePost;
    private int currentTriesForFetchingGeneralUser;

    public FirebaseCompletePostImageForHomeLoader(String imagePostID) {
        this.imagePostID = imagePostID;
    }

    public void setListener(FirebaseDatabaseDownloadListener listener) {
        this.listener = listener;
        this.execute();
    }

    @Override
    public void execute() {

        getFirstObjectFromDatabase();
        Log.d(TAG, imagePostID + ": Execution started");
    }

    @Override
    public void getFirstObjectFromDatabase() {

        Query query = IMAGE_POST_REFERENCE
                .orderByChild("postID")
                .equalTo(this.imagePostID);

        query.addListenerForSingleValueEvent(this);

        Log.d(TAG, imagePostID + ": Started getting first object");


    }

    @Override
    public void getSecondObjectFromDatabase() {

        Query query = GENERAL_USER_REFERENCE
                .orderByChild("mUid")
                .equalTo(this.downloadedImagePost.getUploadingUserUID());

        query.addListenerForSingleValueEvent(this);

        Log.d(TAG, imagePostID + ": Started getting second object");

    }

    @Override
    public void onFirstObjectFetchSucceeded() {

        Log.d(TAG, imagePostID + ": onFirstObjectFetchSucceeded called");

        firstObjectFetched = true;

        getSecondObjectFromDatabase();


    }

    @Override
    public void onSecondObjectFetchSucceeded() {
        Log.d(TAG, imagePostID + ": onSecondObjectFetchSucceeded called");

        onCompleteSuccess();

    }

    @Override
    public void onFirstObjectFetchFailure() {

        currentTriesForFetchingImagePost++;

        if (currentTriesForFetchingImagePost < MAX_TRIES_FOR_FETCHING) {

            getFirstObjectFromDatabase();
        } else {

            onCompleteFailure();
        }

    }

    @Override
    public void onSecondObjectFetchFailure() {

        currentTriesForFetchingGeneralUser++;

        if (currentTriesForFetchingGeneralUser < MAX_TRIES_FOR_FETCHING) {

            getSecondObjectFromDatabase();
        } else {

            onCompleteFailure();
        }

    }

    @Override
    public void onCompleteSuccess() {

        Log.d(TAG, imagePostID + ": Task completed");

        HomeGeneralImagePost post = new HomeGeneralImagePost(downloadedImagePost, downloadedGeneralUser);
        listener.onSuccess(post);

    }

    @Override
    public void onCompleteFailure() {

        Log.d(TAG, imagePostID + ": Task failed");

        listener.onFailure();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        // For ImagePost Object
        if (!firstObjectFetched) {

            Log.d(TAG, imagePostID + ": For onDataChange of first Object");


            if (dataSnapshot.exists()) {

                Log.d(TAG, imagePostID + ": dataSnapshot of first Object exists");


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    try {


                        ImagePost post = child.getValue(ImagePost.class);

                        if (post != null) {

                            downloadedImagePost = post;
                            onFirstObjectFetchSucceeded();
                            return;

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();

                        Log.d(TAG, imagePostID + ": dataSnapshot of first post getting error. stack traced " + ex);

                        onFirstObjectFetchFailure();
                    }

                }

            } else {

                Log.d(TAG, imagePostID + ": dataSnapshot of first Object doesn't exists");
                onCompleteFailure();
            }

        }

        // For General User Object
        else {

            Log.d(TAG, imagePostID + ": For onDataChange of second Object");


            if (dataSnapshot.exists()) {

                Log.d(TAG, imagePostID + ": dataSnapshot of second Object exists");


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    try {


                        GeneralUser user = child.getValue(GeneralUser.class);

                        if (user != null) {

                            downloadedGeneralUser = user;
                            onSecondObjectFetchSucceeded();
                            return;

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();

                        Log.d(TAG, imagePostID + ": dataSnapshot of second post getting error. stack traced");

                        onSecondObjectFetchFailure();
                    }

                }


            } else {

                Log.d(TAG, imagePostID + ": dataSnapshot of second Object doesn't exists");
                onCompleteFailure();
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

        if (!firstObjectFetched) {

            onFirstObjectFetchFailure();
        } else {

            onSecondObjectFetchFailure();
        }

    }
}
