package com.example.nustsocialcircle.Interfaces;

import com.google.firebase.database.ValueEventListener;

public interface FirebaseDatabaseDualRetrieval extends ValueEventListener {

    // Used to start the execution
    void execute();


    // Used to get the first and second object from database
    void getFirstObjectFromDatabase();

    void getSecondObjectFromDatabase();


    // If first or second object succeeded in fetching
    void onFirstObjectFetchSucceeded();

    void onSecondObjectFetchSucceeded();


    // If first or second object failed in fetching
    void onFirstObjectFetchFailure();

    void onSecondObjectFetchFailure();


    // if all succeeded
    void onCompleteSuccess();


    // If all failed
    void onCompleteFailure();


}
