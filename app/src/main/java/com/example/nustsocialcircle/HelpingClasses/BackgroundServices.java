package com.example.nustsocialcircle.HelpingClasses;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.AUTHENTICATION_STATE_CHANGE_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_AVAILABLE;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_ASYNC_REQUEST_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_ASYNC_RESPONSE_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_NOT_AVAILABLE;

public class BackgroundServices extends Service {

    private static final String TAG = "BGServiceTAG";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private int networkStatus;

    private BroadcastReceiver NetworkStateRequestReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startFirebaseAuthCompleteService();
        registerNetworkCheckingCallback();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startFirebaseAuthCompleteService() {

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                Intent intent = new Intent();
                intent.setAction(AUTHENTICATION_STATE_CHANGE_INTENT);
                if (user != null) {

                    Log.d(TAG, "Firebase Auth Service state changed. Intent sent" + firebaseAuth.getCurrentUser().getEmail());

                }

                sendBroadcast(intent);

                Log.d(TAG, "Firebase Auth Service state changed. Intent sent:::::::;NO EMAIL ATTACHED");

            }
        };

        mAuth.addAuthStateListener(mAuthStateListener);

    }

    public void registerNetworkCheckingCallback() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Intent intent = new Intent(NETWORK_CUSTOM_AVAILABILITY_INTENT);
                intent.putExtra("STATUS", NETWORK_AVAILABLE);
                networkStatus = NETWORK_AVAILABLE;
                sendBroadcast(intent);
                Log.d(TAG, "Intent of callback sent with TRUE value");

            }

            @Override
            public void onLost(Network network) {

                super.onLost(network);
                Intent intent = new Intent(NETWORK_CUSTOM_AVAILABILITY_INTENT);
                intent.putExtra("STATUS", NETWORK_NOT_AVAILABLE);
                networkStatus = NETWORK_NOT_AVAILABLE;
                sendBroadcast(intent);
                Log.d(TAG, "Intent of callback sent with FALSE value");

            }

            @Override
            public void onUnavailable() {

                super.onUnavailable();
                Intent intent = new Intent(NETWORK_CUSTOM_AVAILABILITY_INTENT);
                intent.putExtra("STATUS", NETWORK_NOT_AVAILABLE);
                sendBroadcast(intent);
                Log.d(TAG, "Intent of callback sent with FALSE value at unavaiable");

            }

        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            manager.registerDefaultNetworkCallback(callback);

        }
    }

    private void respondToNetworkStateAskingRequest() {

        Intent intent = new Intent(NETWORK_CUSTOM_AVAILABILITY_ASYNC_RESPONSE_INTENT);
        intent.putExtra("STATUS", networkStatus);
        Log.d(TAG, "Intent sent with value of " + networkStatus);
        sendBroadcast(intent);
    }

    public void startListeningToInternetStateRequest() {

        IntentFilter filter = new IntentFilter(NETWORK_CUSTOM_AVAILABILITY_ASYNC_REQUEST_INTENT);
        networkStatus = NETWORK_NOT_AVAILABLE;

        NetworkStateRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                respondToNetworkStateAskingRequest();

            }
        };

        registerReceiver(NetworkStateRequestReceiver, filter);
        Log.d(TAG, "Broadcast registered");
    }

    public void stopListeningToInternetStateRequest() {

        unregisterReceiver(NetworkStateRequestReceiver);
        Log.d(TAG, "Broadcast unRegistered");

    }

    private void sheduledTimer() {

        //        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//                Log.d(TAG,"Method called for :"+ ++counter);
//
//
//            }
//        },0,1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuth != null && mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);

        }
        stopListeningToInternetStateRequest();
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        startListeningToInternetStateRequest();
    }

}
