package com.example.nustsocialcircle.FirebaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.nustsocialcircle.ChatPackage.MessageChat;
import com.example.nustsocialcircle.Circle.Circle;
import com.example.nustsocialcircle.Interfaces.DatabaseUploadListener;
import com.example.nustsocialcircle.Interfaces.FirebaseDatabaseUploadInerface;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class FirebaseDatabaseObjectUploadTask implements FirebaseDatabaseUploadInerface, OnSuccessListener<Void>, OnFailureListener, OnCanceledListener {

    public static final String TAG = "DatabaseObjUploadTAG";
    public static final int DATABASE_MAX_TRIES = 3;

    private static final Class GENERAL_USER_CLASS = GeneralUser.class;
    private static final Class GENERAL_USER_CHAT_MESSAGE_CLASS = MessageChat.class;
    private static final Class CIRCLE_CLASS = Circle.class;


    private Object uploadObject;
    private HashMap<String, Object> map;
    private DatabaseReference databaseReference;
    private String key;
    private Class<?> objectClass;

    private DatabaseUploadListener listener;

    private int numOfFails;
    private boolean objectSent;
    private boolean verificationFetchingError;

    public FirebaseDatabaseObjectUploadTask(Object uploadObject, DatabaseReference databaseReference, String key) {

        this.uploadObject = uploadObject;
        this.databaseReference = databaseReference;
        this.key = key;
        this.objectClass = uploadObject.getClass();

    }

    public void setListener(DatabaseUploadListener listener) {
        this.listener = listener;
        this.execute();
    }

    @Override
    public void execute() {

        Log.d(TAG, "called method execute");
        startUploadingToDatabase();

    }

    @Override
    public void startUploadingToDatabase() {

        Log.d(TAG, "called method startUploadingToDatabase");

        if (objectClass == GENERAL_USER_CLASS) {

            Log.d(TAG, "it is an instance of GeneralUser");

            GeneralUser user = (GeneralUser) uploadObject;

            try {

                map = user.getHashMap();

                databaseReference.child(key)
                        .updateChildren(map)

                        .addOnSuccessListener(this)
                        .addOnFailureListener(this)
                        .addOnCanceledListener(this);

            } catch (IllegalAccessException e) {

                removeEverything();
            }


        } else if (objectClass == GENERAL_USER_CHAT_MESSAGE_CLASS) {

            MessageChat messageChat = (MessageChat) uploadObject;

            databaseReference.child(key)
                    .setValue(messageChat)

                    .addOnSuccessListener(this)
                    .addOnFailureListener(this)
                    .addOnCanceledListener(this);
        } else if (objectClass == CIRCLE_CLASS) {

            Circle circle = (Circle) uploadObject;

            databaseReference.child(key)
                    .setValue(circle)
                    .addOnSuccessListener(this)
                    .addOnFailureListener(this)
                    .addOnCanceledListener(this);


        }

    }

    @Override
    public void onAddToDatabaseFailed() {

        Log.d(TAG, "called method onAddToDatabaseFailed");

        numOfFails++;

        Log.d(TAG, "onAddToFailure::: total fails: " + numOfFails);


        //If the error has occurred at the time of verification
        if (objectSent) {

            //If the limit for verification exists
            if (numOfFails < DATABASE_MAX_TRIES) {


                //If the error is caused due to object fetching failed for verification
                if (verificationFetchingError) {

                    Log.d(TAG, "The object fetching for verification failed failed:)");
                    Log.d(TAG, "As tries remaining are: " + numOfFails + " again fetching object from database to verify");

                    verificationFetchingError = false;
                    checkIfObjectReachedIsSame();

                }

                //If the objects are not same then reset everything and send the object again
                else {

                    Log.d(TAG, "The object are not same. resetting and uploading object again:)");
                    Log.d(TAG, "As tries remaining are: " + numOfFails + " again starting uploading to database");
                    objectSent = false;
                    startUploadingToDatabase();
                }
            }
            //If the limit for verification reached
            else {

                Log.d(TAG, "Number of failure exceeded for verifying object");
                removeEverything();
            }

        }

        //If the error has occurred at the time of sending object
        else {

            //If I can still send the object
            if (numOfFails < DATABASE_MAX_TRIES) {

                Log.d(TAG, "As tries remaining are: " + numOfFails + " again starting uploading to database");
                startUploadingToDatabase();

            }
            //If even the limit for sending object has been failed
            else {
                Log.d(TAG, "Number of failure exceeded for sending first object");
                removeEverything();
            }
        }

    }

    @Override
    public void checkIfObjectReachedIsSame() {

        Log.d(TAG, "called method checkIfObjectReachedIsSame");
        databaseReference.child(key)

                .addListenerForSingleValueEvent(this);

    }

    @Override
    public void onCompleteSuccess() {

        Log.d(TAG, "called method onCompleteSuccess");
        listener.onTaskCompleted();
    }

    @Override
    public void removeEverything() {

        Log.d(TAG, "called method removeEverything");
        listener.onCompleteFailure();
//        databaseReference.child(key)
//                .removeValue();

    }


    //Att those method for listener the upload object
    @Override
    public void onSuccess(Void aVoid) {

        Log.d(TAG, "called method onSuccess");
        objectSent = true;
        checkIfObjectReachedIsSame();
    }

    @Override
    public void onFailure(@NonNull Exception e) {

        Log.d(TAG, "called method onFailure");
        onAddToDatabaseFailed();

    }

    @Override
    public void onCanceled() {

        Log.d(TAG, "called method onCanceled");
        onAddToDatabaseFailed();
    }


    //All the listeners for verification for uploading object
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        Log.d(TAG, "called method onDataChange");

        if (dataSnapshot.exists()) {

            if (objectClass == GENERAL_USER_CLASS) {

                Log.d(TAG, "Receiving object is instance of GeneralUser");

                GeneralUser clientUser = (GeneralUser) uploadObject;
                GeneralUser serverUser = dataSnapshot.getValue(GeneralUser.class);

                try {

                    if (clientUser.getmName().equals(serverUser.getmName())) {

                        onCompleteSuccess();

                    } else {

                        onAddToDatabaseFailed();

                    }

                } catch (Exception ex) {

                }
            } else if (objectClass == GENERAL_USER_CHAT_MESSAGE_CLASS) {


                Log.d(TAG, "Receiving object is instance of Message chat");

                MessageChat clientChat = (MessageChat) uploadObject;
                MessageChat serverChat = dataSnapshot.getValue(MessageChat.class);

                try {

                    if (clientChat.getMessage().equals(serverChat.getMessage())) {

                        onCompleteSuccess();

                    } else {

                        onAddToDatabaseFailed();

                    }

                } catch (Exception ex) {

                }


            } else if (objectClass == CIRCLE_CLASS) {

                Circle clientCircle = (Circle) uploadObject;
                Circle serverCircle = dataSnapshot.getValue(Circle.class);

                if (clientCircle.equals(serverCircle)) {

                    onCompleteSuccess();

                } else {

                    onAddToDatabaseFailed();
                }
            }

        } else {

//            onAddToDatabaseFailed();

        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

        Log.d(TAG, "called method onCancelled");


        verificationFetchingError = true;
        onAddToDatabaseFailed();

    }

}
