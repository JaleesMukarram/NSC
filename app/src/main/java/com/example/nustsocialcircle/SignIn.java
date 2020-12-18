package com.example.nustsocialcircle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nustsocialcircle.HelpingClasses.BackgroundServices;
import com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass;
import com.example.nustsocialcircle.HelpingClasses.CustomToast;
import com.example.nustsocialcircle.Interfaces.AuthStateUpdatingEnabled;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_AVAILABLE;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_ASYNC_REQUEST_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_ASYNC_RESPONSE_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_NOT_AVAILABLE;

public class SignIn extends AppCompatActivity implements AuthStateUpdatingEnabled {

    public static final String TAG = "SignINActivityTAG";

    private EditText EmailET, PasswordET;
    private TextView ErrorShowingTV;
    private Button SignINBTN;
    private TextView SignUPInsteadTV;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String emailTemp;
    private String passwordTemp;
    private boolean firstResponsePassed;
    private boolean internetAvailable;


    private BroadcastReceiver NetWorkAvailabilityReceiver;
    private BroadcastReceiver AuthStateChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeViews();
        initializeComponents();

    }

    private void initializeViews() {

        EmailET = (EditText) findViewById(R.id.ETSignINEmailAsking);
        ErrorShowingTV = (TextView) findViewById(R.id.TVSignInErrorShowing);
        PasswordET = (EditText) findViewById(R.id.ETSignINPasswordAsking);
        SignINBTN = (Button) findViewById(R.id.BTNSignINSignIN);
        SignUPInsteadTV = (TextView) findViewById(R.id.BTNSignINSignUPInstead);

    }

    private void initializeComponents() {

        mAuth = FirebaseAuth.getInstance();

        SignUPInsteadTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
                finish();
            }
        });

        SignINBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLogin();
            }
        });
    }

    private void startLogin() {

        emailTemp = EmailET.getText().toString().trim();
        passwordTemp = PasswordET.getText().toString();

        if (validatePassword(emailTemp, passwordTemp)) {

            mAuth.signInWithEmailAndPassword(emailTemp, passwordTemp)

                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            if (e instanceof FirebaseAuthInvalidCredentialsException) {

                                ErrorShowingTV.setText("Invalid Password, Try again");

//                            Log.d(TAG, "Invalid password exception");

                            } else if (e instanceof FirebaseAuthInvalidUserException) {

                                ErrorShowingTV.setText("Account doesn't exists ");

//                            Log.d(TAG, "Email not in use");

                            } else if (e instanceof FirebaseAuthException) {

                                ErrorShowingTV.setText("Authentication Error occurred");

//                            Log.d(TAG, "Authentication error occured");

                            } else {

                                ErrorShowingTV.setText("Error: " + e.toString());

                            }

                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

//                        Log.d(TAG, "Can't sign in. Please check your connection and try again");

                        }
                    });
        }
    }


    private boolean validatePassword(String email, String password) {

        boolean returner = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            returner = false;
            ErrorShowingTV.setText("Not a valid email address");

        }

        if (password.isEmpty()) {

            ErrorShowingTV.setText("Please enter a password");
            returner = false;

        } else if (password.length() < 8) {

            ErrorShowingTV.setText("Password Length too short. Min 8 Required");
            returner = false;


        } else if (password.length() > 20) {

            ErrorShowingTV.setText("Password Length too long. Max 20 Allowed");
            returner = false;


        } else if (password.contains(" ")) {

            ErrorShowingTV.setText("Password contains spaces. No spaces allowed");
            returner = false;


        }

        return returner;

    }


    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(SignIn.this, BackgroundServices.class));
        startListeningToInternetState();
        startListeningToUserStateChange();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListeningToInternetState();
        stopListeningUserToStateChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.internetAvailable = false;
        this.firstResponsePassed = false;
    }

    //User States
    @Override
    public void startListeningToUserStateChange() {


        IntentFilter filter = new IntentFilter(CustomIntentNamesClass.AUTHENTICATION_STATE_CHANGE_INTENT);
        AuthStateChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                respondToUserStateChange(intent);

            }
        };

        registerReceiver(AuthStateChangeReceiver, filter);


    }

    @Override
    public void stopListeningUserToStateChange() {

        unregisterReceiver(AuthStateChangeReceiver);

    }

    @Override
    public void respondToUserStateChange(Intent intent) {

        if (CustomIntentNamesClass.AUTHENTICATION_STATE_CHANGE_INTENT.equals(intent.getAction())) {

            mUser = mAuth.getCurrentUser();

            if (mUser == null) {

                Log.d(TAG, "Intent for AuthState recieved with NO USER");

            } else {

                startActivity(new Intent(SignIn.this, HomeScreenGeneral.class));

//                Log.d(TAG, "NEW USER FOUND::::::with email " + mUser.getEmail());

            }
        }
    }


    //Internet States

    @Override
    public void onInternetConnectionLost() {

        internetAvailable = false;
        CustomToast.make_toast_LIGHT(getApplicationContext(), "Internet connection lost", Gravity.BOTTOM);


    }

    @Override
    public void onInternetConnectionResume() {

        internetAvailable = true;
        CustomToast.make_toast_LIGHT(getApplicationContext(), "Internet connection available/resumed", Gravity.BOTTOM);


    }

    @Override
    public void startListeningToInternetState() {

        IntentFilter filter = new IntentFilter(NETWORK_CUSTOM_AVAILABILITY_INTENT);
        filter.addAction(NETWORK_CUSTOM_AVAILABILITY_ASYNC_RESPONSE_INTENT);
        NetWorkAvailabilityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                onInernetConnectionStateChanged(intent);

            }
        };

        registerReceiver(NetWorkAvailabilityReceiver, filter);


    }

    @Override
    public void onInernetConnectionStateChanged(Intent intent) {


        //If it is received due to change in the state at the start of the service
        if (NETWORK_CUSTOM_AVAILABILITY_INTENT.equals(intent.getAction())) {

            if (intent.getIntExtra("STATUS", NETWORK_NOT_AVAILABLE) == NETWORK_AVAILABLE) {

                if (firstResponsePassed) {
                    onInternetConnectionResume();
                } else {

                    firstResponsePassed = true;
                    internetAvailable = true;
                }


            } else if (intent.getIntExtra("STATUS", NETWORK_NOT_AVAILABLE) == NETWORK_NOT_AVAILABLE) {

                onInternetConnectionLost();
                Log.d(TAG, "Change Detected. Not Avaiable -1");

            }

            //Received on requesting only
        }

//        else if (NETWORK_CUSTOM_AVAILABILITY_ASYNC_RESPONSE_INTENT.equals(intent.getAction())) {
//
//            if (intent.getIntExtra("STATUS", NETWORK_NOT_AVAILABLE) == NETWORK_AVAILABLE) {
//
//                //Internet availabale
//
//                if (firstResponsePassed) {
//
//                    Log.d(TAG, "Response Detected. Available 1 but as first <negleced> ");
//                    onInternetConnectionResume();
//
//                } else {
//
//                    Log.d(TAG, "Response Detected. Available 1");
//
//                    firstResponsePassed = true;
//                    internetAvaiable = true;
//                }
//
//            } else if (intent.getIntExtra("STATUS", NETWORK_NOT_AVAILABLE) == NETWORK_NOT_AVAILABLE) {
//
//                //Interner not avaiable
//                Log.d(TAG, "Response Detected. unAvailable -1");
//                onInternetConnectionLost();
//
//            }
//        }
    }

    @Override
    public void askInternetConnectionAsync() {

        Intent intent = new Intent(NETWORK_CUSTOM_AVAILABILITY_ASYNC_REQUEST_INTENT);
        sendBroadcast(intent);
        Log.d(TAG, "Asking for asyc internet state via request with: " + internetAvailable);

    }

    @Override
    public void stopListeningToInternetState() {

        unregisterReceiver(NetWorkAvailabilityReceiver);

    }

}
