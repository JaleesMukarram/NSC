package com.example.nustsocialcircle.Interfaces;

public interface DatabaseUploadListener {

    void listenToProgress(int progress);

    void onCompleteFailure();

    void onTaskCompleted();

}
