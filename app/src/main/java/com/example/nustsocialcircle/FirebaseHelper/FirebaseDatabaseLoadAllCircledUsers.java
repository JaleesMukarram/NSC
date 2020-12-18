package com.example.nustsocialcircle.FirebaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nustsocialcircle.Interfaces.FirebaseDatabaseDownloadListener;
import com.example.nustsocialcircle.Interfaces.FirebaseDatabaseDualRetrieval;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseLoadAllCircledUsers implements FirebaseDatabaseDualRetrieval {

    public static final String TAG = "AllCircleDownloadTAG";

    private static final DatabaseReference CIRCLE_USER_REFERENCE = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserCircle.CIRCLE_REFERENCE);
    private static final DatabaseReference GENERAL_USER_REFERENCE = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE);
    private static final int MAX_TRIES_FOR_FETCHING = 2;


    private int currentIDsFailedCounter = 0;
    private int currentGeneralUsersFailedCounter = 0;

    private String myID;
    private List<String> circledUsersIDsList;
    private List<GeneralUser> circledGeneralUsersList;


    private FirebaseDatabaseDownloadListener listener;

    public FirebaseDatabaseLoadAllCircledUsers(String myID) {
        this.myID = myID;
    }

    public void setListener(FirebaseDatabaseDownloadListener listener) {
        this.listener = listener;
        execute();
    }


    @Override
    public void execute() {

        circledUsersIDsList = new ArrayList<>();
        circledGeneralUsersList = new ArrayList<>();

        getFirstObjectFromDatabase();

    }

    @Override
    public void getFirstObjectFromDatabase() {

        Query query = CIRCLE_USER_REFERENCE
                .orderByChild("mAddingUserID")
                .equalTo(myID);

        Log.d(TAG, "First object getting started");

        query.addChildEventListener(new CircleListListener());

    }

    @Override
    public void getSecondObjectFromDatabase() {

        GENERAL_USER_REFERENCE.addChildEventListener(new GeneralListListener());

    }

    @Override
    public void onFirstObjectFetchSucceeded() {

        currentIDsFailedCounter = 0;
        getSecondObjectFromDatabase();

    }

    @Override
    public void onSecondObjectFetchSucceeded() {

        onCompleteSuccess();

    }

    @Override
    public void onFirstObjectFetchFailure() {

        currentIDsFailedCounter++;

        if (currentIDsFailedCounter < MAX_TRIES_FOR_FETCHING) {

            getFirstObjectFromDatabase();

        } else {

            onCompleteFailure();
        }

    }

    @Override
    public void onSecondObjectFetchFailure() {

        currentGeneralUsersFailedCounter++;

        if (currentGeneralUsersFailedCounter < MAX_TRIES_FOR_FETCHING) {

            getSecondObjectFromDatabase();

        } else {

            onCompleteFailure();
        }

    }

    @Override
    public void onCompleteSuccess() {

        currentIDsFailedCounter = 0;
        currentGeneralUsersFailedCounter = 0;
        listener.onSuccess(circledGeneralUsersList);

    }

    @Override
    public void onCompleteFailure() {

        currentIDsFailedCounter = 0;
        currentGeneralUsersFailedCounter = 0;
        listener.onFailure();

    }


    // Neglected
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }


    private class CircleListListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Log.d(TAG, "onChildAdded for object 1");

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attributes : child.getChildren()) {

                        if ("mAddedUserID".equals(attributes.getKey())) {

                            String id = attributes.getValue(String.class);

                            if (!circledUsersIDsList.contains(id)) {

                                circledUsersIDsList.add(id);
                                Log.d(TAG, "ID Added: " + id);
                            }

                            Log.d(TAG, "ID Already exists: " + id);

                        }
                    }
                }

                onFirstObjectFetchSucceeded();

            } else {

                onFirstObjectFetchFailure();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Log.d(TAG, "onChildChanged for object 1");

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attributes : child.getChildren()) {

                        if ("mAddedUserID".equals(attributes.getKey())) {

                            String id = attributes.getValue(String.class);

                            if (!circledUsersIDsList.contains(id)) {

                                circledUsersIDsList.add(id);
                                Log.d(TAG, "ID Added: " + id);
                            }

                            Log.d(TAG, "ID Already exists: " + id);
                        }
                    }
                }

                onFirstObjectFetchSucceeded();

            } else {

                onFirstObjectFetchFailure();
            }


        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            Log.d(TAG, "onChildRemoved for object 1");

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attributes : child.getChildren()) {

                        if ("mAddedUserID".equals(attributes.getKey())) {

                            String id = attributes.getValue(String.class);

                            if (circledUsersIDsList.contains(id)) {

                                circledUsersIDsList.remove(id);

                                Log.d(TAG, "ID Removed: " + id);
                            }

                            Log.d(TAG, "ID Already removed: " + id);

                        }
                    }
                }

                onFirstObjectFetchSucceeded();

            } else {

                onFirstObjectFetchFailure();
            }

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

            onFirstObjectFetchFailure();

        }
    }

    private class GeneralListListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Log.d(TAG, "onChildAdded for object 2");


            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attribute : child.getChildren()) {

                        if ("mUid".equals(attribute.getKey())) {

                            String id = attribute.getValue(String.class);

                            if (circledUsersIDsList.contains(id)) {

                                GeneralUser generalUser = child.getValue(GeneralUser.class);
                                circledGeneralUsersList.add(generalUser);

                                if (generalUser != null) {

                                    Log.d(TAG, "GeneralUser added: " + generalUser.getmName());
                                }
                            }
                        }
                    }
                }

                onSecondObjectFetchSucceeded();

            } else {

                onSecondObjectFetchFailure();
            }


        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Log.d(TAG, "onChildChanged for object 2");

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attribute : child.getChildren()) {

                        if ("mUid".equals(attribute.getKey())) {

                            String id = attribute.getValue(String.class);

                            if (circledUsersIDsList.contains(id)) {

                                GeneralUser generalUser = child.getValue(GeneralUser.class);
                                circledGeneralUsersList.add(generalUser);

                                if (generalUser != null) {
                                    Log.d(TAG, "Child added : " + generalUser.getmName());
                                }

                            }
                        }
                    }
                }

                onSecondObjectFetchSucceeded();

            } else {

                onSecondObjectFetchFailure();
            }

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            Log.d(TAG, "onChildRemoved for object 2");

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attribute : child.getChildren()) {

                        if ("mUid".equals(attribute.getKey())) {

                            String id = attribute.getValue(String.class);

                            if (circledUsersIDsList.contains(id)) {

                                GeneralUser generalUser = child.getValue(GeneralUser.class);
                                circledGeneralUsersList.remove(generalUser);
                                if (generalUser != null) {

                                    Log.d(TAG, "Child user removed: " + generalUser.getmName());
                                }
                            }
                        }
                    }
                }

                onSecondObjectFetchSucceeded();

            } else {

                onSecondObjectFetchFailure();
            }

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

            onSecondObjectFetchFailure();

        }
    }

}
