package com.example.nustsocialcircle.FirebaseHelper;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.nustsocialcircle.Interfaces.FirebaseStorageUploadInterface;
import com.example.nustsocialcircle.Interfaces.StorageUploadListener;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class FirebaseStorageImageUploadTask implements FirebaseStorageUploadInterface, OnSuccessListener<Uri>, OnFailureListener, OnCanceledListener {

    public static final String TAG = "FirebaseStrImageTAG";
    public static final int STORAGE_MAX_TRIES = 2;


    private Uri imageFile;
    private String childPath;
    private StorageReference storageReference;

    private boolean imageUploaded;
    private int numOfFails;


    private StorageUploadListener listener;

    public FirebaseStorageImageUploadTask(Uri imageFile, String childPath, StorageReference storageReference) {
        this.imageFile = imageFile;
        this.childPath = childPath;
        this.storageReference = storageReference;
    }

    public void setListener(StorageUploadListener listener) {
        this.listener = listener;
    }

    @Override
    public void execute() {

        startUploadingToStorage();
    }

    @Override
    public void startUploadingToStorage() {

        storageReference.child(childPath)

                .putFile(imageFile)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imageUploaded = true;
                        listener.listenToProgress(40);
                        getDownloadUri();

                    }
                })
                .addOnFailureListener(this)
                .addOnCanceledListener(this)
        ;


    }

    @Override
    public void onAddToStorageFailed() {

        numOfFails++;

        //If the image has been uploaded and failure occurs while getting URI;
        if (imageUploaded) {

            if (numOfFails < STORAGE_MAX_TRIES) {

                getDownloadUri();

            } else {

                removeEverything();

            }
        }
        //If the image has not been uploaded yet
        else {

            if (numOfFails < STORAGE_MAX_TRIES) {

                startUploadingToStorage();

            } else {

                removeEverything();

            }
        }
    }

    @Override
    public void getDownloadUri() {

        storageReference.child(childPath)
                .getDownloadUrl()

                .addOnSuccessListener(this)
                .addOnFailureListener(this)
                .addOnCanceledListener(this);

    }

    @Override
    public void onCompleteSuccess() {

    }

    @Override
    public void removeEverything() {

        if (imageUploaded) {
            storageReference.child(childPath)
                    .delete();
        }
    }

    @Override
    public void onSuccess(Uri uri) {

        listener.listenToProgress(100);
        listener.onTaskCompleted(uri);
        onCompleteSuccess();

    }

    @Override
    public void onFailure(@NonNull Exception e) {

        onAddToStorageFailed();

    }

    @Override
    public void onCanceled() {

        onAddToStorageFailed();

    }

}
