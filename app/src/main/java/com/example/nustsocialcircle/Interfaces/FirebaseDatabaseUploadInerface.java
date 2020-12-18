package com.example.nustsocialcircle.Interfaces;

import com.google.firebase.database.ValueEventListener;

public interface FirebaseDatabaseUploadInerface extends ValueEventListener {

    //Start the operation sequence

    void execute();

    //StartUploading the firebase database object
    void startUploadingToDatabase();

    //If the object failed to reach database to some error
    void onAddToDatabaseFailed();

    //If object reached, checking it to unsure that the same object has been reached
    void checkIfObjectReachedIsSame();

    //If the operation has been done successfully
    void onCompleteSuccess();

    //If the operation has been completely failed
    void removeEverything();

}
