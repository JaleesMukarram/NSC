package com.example.nustsocialcircle.Interfaces;


import android.content.Intent;

public interface AuthStateUpdatingEnabled {


    //Auth state methods
    void respondToUserStateChange(Intent intent);

    void startListeningToUserStateChange();

    void stopListeningUserToStateChange();


    //Internet connection methods
    void onInternetConnectionLost();

    void onInternetConnectionResume();

    void startListeningToInternetState();

    void onInernetConnectionStateChanged(Intent intent);

    void stopListeningToInternetState();

    void askInternetConnectionAsync();


}
