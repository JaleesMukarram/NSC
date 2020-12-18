package com.example.nustsocialcircle.Interfaces;

import android.net.Uri;

public interface StorageUploadListener {

    void listenToProgress(int progress);

    void onCompleteFailure();

    void onTaskCompleted(Uri uri);

}
