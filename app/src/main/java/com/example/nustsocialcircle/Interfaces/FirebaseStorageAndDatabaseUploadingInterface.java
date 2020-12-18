package com.example.nustsocialcircle.Interfaces;

public interface FirebaseStorageAndDatabaseUploadingInterface extends FirebaseDatabaseUploadInerface, FirebaseStorageUploadInterface {

    //Start the operation sequence

    void execute();

    //Start getting Uri from database storage
    void startGettingUriFromStorage();


    void onGettingUriFailed();


    //Calls if max Limit for anything reached before uploading;
    void removeFromStorage();

    void onCompleteSuccess();

    void removeEverything();

}
