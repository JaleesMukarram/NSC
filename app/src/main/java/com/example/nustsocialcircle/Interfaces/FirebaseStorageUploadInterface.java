package com.example.nustsocialcircle.Interfaces;

public interface FirebaseStorageUploadInterface {

    //Start the operation sequence

    void execute();

    //StartUploading the firebase storage file
    void startUploadingToStorage();


    //If the object failed to reach due to some error
    void onAddToStorageFailed();

    //If the object reached, then get the Image download link
    void getDownloadUri();


    //If the operation has been done successfully
    void onCompleteSuccess();

    //If the operation has been completely failed
    void removeEverything();

}
