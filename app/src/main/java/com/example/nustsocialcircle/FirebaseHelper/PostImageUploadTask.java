package com.example.nustsocialcircle.FirebaseHelper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.nustsocialcircle.Interfaces.DatabaseUploadListener;
import com.example.nustsocialcircle.Interfaces.FirebaseStorageAndDatabaseUploadingInterface;
import com.example.nustsocialcircle.PostModalClasses.ImagePost;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.Serializable;

public class PostImageUploadTask implements FirebaseStorageAndDatabaseUploadingInterface, Serializable {


    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserPOSTs.GENERAL_USER_IMAGE_POSTS_REFERENCE);
    private static final StorageReference storageReference = FirebaseStorage.getInstance().getReference(FirebaseCustomReferences.GeneralUserPOSTs.GENERAL_USER_IMAGE_POSTS_STORAGE);
    private static final int STORAGE_MAX_TRIES = 3;
    private static final int STORAGE_URI_GETTING_MAX_TRIES = 5;
    private static final int DATABASE_MAX_TRIES = 3;

    private static final String TAG = "PostImageTaskTAG";

    private DatabaseUploadListener listener;
    private ImagePost imagePost;
    private File ImageFile;

    private transient StorageReference SpecificStorageReference;
    private transient DatabaseReference SpecificDatabaseReference;

    private int numStorageFails;
    private int numDatabaseFails;
    private int numUriFails;

    private boolean isUploadedToStorage;
    private boolean isUploadedToDatabase;
    private boolean isUriFetched;


    public PostImageUploadTask(ImagePost imagePost, File imageFile) {

        this.imagePost = imagePost;
        this.ImageFile = imageFile;

    }

    public void setListener(DatabaseUploadListener listener) {
        this.listener = listener;
    }

    public File getImageFile() {
        return ImageFile;
    }


    //Starts all the operations
    @Override
    public void startUploadingToStorage() {

        Log.d(TAG, imagePost.getPostID() + " : Started uploading to storage, fails for storage are: " + numStorageFails);

        if (SpecificStorageReference != null) {

            Log.d("BackUploadService", "Specific is available");

            SpecificStorageReference

                    .putFile(Uri.fromFile(ImageFile))

                    .addOnSuccessListener(new SuccessForUploadingToStorage())
                    .addOnFailureListener(new FailureForUploadingToStorage())
                    .addOnCanceledListener(new CanceledOperationListener(CanceledOperationListener.CANCELED_STORAGE));

        }
    }

    @Override
    public void startGettingUriFromStorage() {

        Log.d(TAG, imagePost.getPostID() + ": Started getting URI from storage, fails for getting URI are: " + numUriFails);


        SpecificStorageReference

                .getDownloadUrl()

                .addOnSuccessListener(new SuccessForGettingUploadURI())
                .addOnFailureListener(new FailureForGettingUploadURI())
                .addOnCanceledListener(new CanceledOperationListener(CanceledOperationListener.CANCELED_URI));

    }

    @Override
    public void startUploadingToDatabase() {

        Log.d(TAG, imagePost.getPostID() + ": Started uploading to database, fails for database are: " + numDatabaseFails);

        SpecificDatabaseReference

                .setValue(imagePost)


                .addOnSuccessListener(new SuccessForUpdatingDatabase())
                .addOnFailureListener(new FailureForUpdatingDatabase())
                .addOnCanceledListener(new CanceledOperationListener(CanceledOperationListener.CANCELED_DATABASE));

    }


    //Handles all the failures
    @Override
    public void onAddToStorageFailed() {

        numStorageFails++;
        Log.d(TAG, imagePost.getPostID() + ": Storage uploading failed, new fails for storage are: " + numStorageFails);

        if (numStorageFails < STORAGE_MAX_TRIES) {

            startUploadingToStorage();

        } else {

            removeEverything();
        }
    }

    @Override
    public void getDownloadUri() {

    }

    @Override
    public void onGettingUriFailed() {

        numUriFails++;
        Log.d(TAG, imagePost.getPostID() + ": Storage URI getting failed, new fails for URI are: " + numUriFails);

        if (numUriFails < STORAGE_URI_GETTING_MAX_TRIES) {

            startGettingUriFromStorage();

        } else {
            removeEverything();
        }
    }

    @Override
    public void onAddToDatabaseFailed() {

        numDatabaseFails++;

        Log.d(TAG, imagePost.getPostID() + ": Database uploading failed, new fails for database are: " + numDatabaseFails);


        if (numDatabaseFails < DATABASE_MAX_TRIES) {

            startUploadingToDatabase();

        } else {

            removeEverything();

        }
    }

    @Override
    public void checkIfObjectReachedIsSame() {

        SpecificDatabaseReference.addListenerForSingleValueEvent(this);
    }

    //Methods called on full complete or on full failure
    @Override
    public void removeFromStorage() {

        SpecificStorageReference
                .delete();

    }

    @Override
    public void onCompleteSuccess() {

        listener.listenToProgress(100);
        listener.onTaskCompleted();

    }

    @Override
    public void removeEverything() {

        if (isUploadedToStorage) {
            removeFromStorage();

        }

        listener.onCompleteFailure();
    }

    @Override
    public void execute() {


        this.SpecificDatabaseReference = databaseReference.child(imagePost.getPostID());
        this.SpecificStorageReference = storageReference.child(imagePost.getPostID());

        startUploadingToStorage();

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        if (dataSnapshot.exists()) {

            Log.d(TAG, "DataSnapshot: " + dataSnapshot);

            ImagePost serverImagePost = dataSnapshot.getValue(ImagePost.class);
            if (imagePost.equals(serverImagePost)) {

                Log.d(TAG, "ImagePost equal calling success completed");

                onCompleteSuccess();

            } else {

                Log.d(TAG, "TASK unequal");


                Log.d(TAG, "imageUrl: " + imagePost.getImageURL().equals(serverImagePost.getImageURL()));
                Log.d(TAG, "postID: " + imagePost.getPostID().equals(serverImagePost.getPostID()));
                Log.d(TAG, "postType: " + (imagePost.getPostType() == serverImagePost.getPostType()));
                Log.d(TAG, "uploadMillis: " + (imagePost.getUploadMillis() == serverImagePost.getUploadMillis()));
                Log.d(TAG, "UID: " + imagePost.getUploadingUserUID().equals(serverImagePost.getUploadingUserUID()));
                Log.d(TAG, "description: " + imagePost.getImageDescription().equals(serverImagePost.getImageDescription()));


                onAddToDatabaseFailed();
            }
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

        onAddToDatabaseFailed();

    }


    private class SuccessForUploadingToStorage implements OnSuccessListener<UploadTask.TaskSnapshot> {

        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            isUploadedToStorage = true;
            listener.listenToProgress(60);
            startGettingUriFromStorage();
        }
    }

    private class SuccessForGettingUploadURI implements OnSuccessListener<Uri> {

        @Override
        public void onSuccess(Uri uri) {

            isUriFetched = true;
            imagePost.setImageURL(uri.toString());
            listener.listenToProgress(70);
            startUploadingToDatabase();

        }
    }

    private class SuccessForUpdatingDatabase implements OnSuccessListener<Void> {


        @Override
        public void onSuccess(Void aVoid) {

            isUploadedToDatabase = true;
            listener.listenToProgress(90);
            checkIfObjectReachedIsSame();

        }
    }


    private class FailureForUploadingToStorage implements OnFailureListener {

        @Override
        public void onFailure(@NonNull Exception e) {
            Log.d(TAG, imagePost.getPostID() + ": Storage uploading exception " + e);
            onAddToStorageFailed();
        }
    }

    private class FailureForGettingUploadURI implements OnFailureListener {

        @Override
        public void onFailure(@NonNull Exception e) {

            Log.d(TAG, imagePost.getPostID() + ": Storage URI getting exception " + e);
            onGettingUriFailed();

        }
    }

    private class FailureForUpdatingDatabase implements OnFailureListener {


        @Override
        public void onFailure(@NonNull Exception e) {

            Log.d(TAG, imagePost.getPostID() + ": Database uploading  exception " + e);
            onAddToDatabaseFailed();

        }
    }

    private class CanceledOperationListener implements OnCanceledListener {

        private static final int CANCELED_STORAGE = 10;
        private static final int CANCELED_URI = 20;
        private static final int CANCELED_DATABASE = 30;
        private int cancelMode;

        private CanceledOperationListener(int cancelMode) {
            this.cancelMode = cancelMode;
        }

        @Override
        public void onCanceled() {

            if (this.cancelMode == CANCELED_STORAGE) {

                Log.d(TAG, imagePost.getPostID() + ": Storage uploading cenceled");
                onAddToStorageFailed();

            } else if (this.cancelMode == CANCELED_URI) {

                Log.d(TAG, imagePost.getPostID() + ": Storage URI getting cenceled");
                onGettingUriFailed();

            } else if (this.cancelMode == CANCELED_DATABASE) {

                Log.d(TAG, imagePost.getPostID() + ": database uploading cenceled");
                onAddToDatabaseFailed();

            }

        }
    }

}
