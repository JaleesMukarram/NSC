package com.example.nustsocialcircle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences;
import com.example.nustsocialcircle.FragmentsHomeGeneral.ViewPagerCustomAdapter;
import com.example.nustsocialcircle.HelpingClasses.BackgroundServices;
import com.example.nustsocialcircle.Interfaces.AuthStateUpdatingEnabled;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.AUTHENTICATION_STATE_CHANGE_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_AVAILABLE;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_ASYNC_REQUEST_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_ASYNC_RESPONSE_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_CUSTOM_AVAILABILITY_INTENT;
import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.NETWORK_NOT_AVAILABLE;


public class HomeScreenGeneral extends AppCompatActivity implements AuthStateUpdatingEnabled {

    private static final String TAG = "HomeScreenGeneralTAG";
    public static FirebaseAuth mAuth;

    private TabLayout TabLayoutMain;
    private ViewPager2 ViewPager2;
    private ViewPagerCustomAdapter ViewPageAdapter2;


    private FirebaseUser mUser;
    private DatabaseReference databaseReference;

    private BroadcastReceiver AuthStateReceiver;
    private BroadcastReceiver NetWorkAvailabilityReceiver;


    private boolean internetAvailable;
    private boolean firstResponsePassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_general);

        Log.d(TAG, "onCreate called");

        initializeViews();
        initializeComponents();
//        initializeListners();


        Intent intent = new Intent(HomeScreenGeneral.this, BackgroundServices.class);
        startService(intent);


    }

    private void initializeViews() {

        TabLayoutMain = findViewById(R.id.TLHomeScreenGeneralTabItems);
        ViewPager2 = findViewById(R.id.VP2HomeScreenGeneralAllFragmentsContainer);
        ViewPageAdapter2 = new ViewPagerCustomAdapter(this);
        ViewPager2.setAdapter(ViewPageAdapter2);
        ViewPager2.setOffscreenPageLimit(6);

        TabLayoutMain.setTabTextColors(getResources().getColor(R.color.white, getTheme()), getResources().getColor(R.color.yellow, getTheme()));
        TabLayoutMediator mediator = new TabLayoutMediator(TabLayoutMain, ViewPager2, new CustomTabConfiguration());
        mediator.attach();

        TabLayoutMain.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary, getTheme()));

        TabLayoutMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                tab.getIcon().setColorFilter(getResources().getColor(R.color.yellow, getTheme()), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                tab.getIcon().setColorFilter(getResources().getColor(R.color.white, getTheme()), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


//        ViewPager1 = (ViewPager) findViewById(R.id.viewPager);
//        ViewPageAdapter1 = new ViewPagerFragmentPageAdapter(getSupportFragmentManager(), 1);


//        ViewPager1.setAdapter(ViewPageAdapter1);
//        ViewPager1.setOffscreenPageLimit(6);

    }

    private void initializeComponents() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {

            Log.d(TAG, "User not found");
            startActivity(new Intent(HomeScreenGeneral.this, SignUp.class));
        }

        checkIfUserDataExistsInDatabaseAsWell();

    }

//    private void initializeListners() {
//
//
//        ViewPager1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//                simulatePageChanges(position);
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        HomeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewPager1.setCurrentItem(HOME_VIEW);
//            }
//        });
//        FavoriteView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewPager1.setCurrentItem(FAVORITE_VIEW);
//            }
//        });
//
//        MessageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewPager1.setCurrentItem(MESSAGE_VIEW);
//            }
//        });
//
//        NotificationView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewPager1.setCurrentItem(NOTIFICATION_VIEW);
//            }
//        });
//
//        SettingView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewPager1.setCurrentItem(SETTINGS_VIEW);
//            }
//        });
//
//        AccountIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewPager1.setCurrentItem(ACCOUNT_VIEW);
//            }
//        });
//    }

    private void checkIfUserDataExistsInDatabaseAsWell() {

        if (mUser != null) {

            Log.d(TAG, "Current User ID while checking in data for mUser is: " + mUser.getUid());

            databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE)
                    .child(mUser.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        Log.d(TAG, "User data available in the database");

                    } else {

                        if (dataSnapshot.getValue() == null) {

                            startActivity(new Intent(HomeScreenGeneral.this, GeneralUserCompleteRegistration.class));
                        }

                        Log.d(TAG, "Snapshot doesnt exisit");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        startListeningToInternetState();
        startListeningToUserStateChange();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListeningToInternetState();
        stopListeningUserToStateChange();
        Log.d(TAG, "onStop called");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    //User States
    @Override
    public void respondToUserStateChange(Intent intent) {

        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            Log.d(TAG, "In response to state changed, the user is null");

            startActivity(new Intent(HomeScreenGeneral.this, Splash.class));
            finish();

        } else {
            Log.d(TAG, "In response to state changed, the user is available");
        }
    }

    @Override
    public void startListeningToUserStateChange() {

        IntentFilter intentFilter = new IntentFilter(AUTHENTICATION_STATE_CHANGE_INTENT);
        AuthStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "Broadcast related to user state received");

                if (AUTHENTICATION_STATE_CHANGE_INTENT.equals(intent.getAction())) {
                    respondToUserStateChange(intent);
                }
            }
        };

        registerReceiver(AuthStateReceiver, intentFilter);
        Log.d(TAG, "Auth Broadcast registered");
    }

    @Override
    public void stopListeningUserToStateChange() {

        if (AuthStateReceiver != null) {
            unregisterReceiver(AuthStateReceiver);

        }
        Log.d(TAG, "Auth Broadcast unRegistered");

    }


    //Internet States
    @Override
    public void onInternetConnectionLost() {

        internetAvailable = false;


    }

    @Override
    public void onInternetConnectionResume() {

        internetAvailable = true;

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

                    Log.d(TAG, "Internet available first intent recieved");

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
        Log.d(TAG, "Asking for asyc internet state via request");
    }

    @Override
    public void stopListeningToInternetState() {

        unregisterReceiver(NetWorkAvailabilityReceiver);

    }


    class CustomTabConfiguration implements TabLayoutMediator.TabConfigurationStrategy {


        @Override
        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            switch (position) {

                case 0:

                    tab.setText("Home");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_home_off_30, getTheme()));
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.yellow, getTheme()), PorterDuff.Mode.SRC_IN);

                    break;

                case 1:
                    tab.setText("Explore");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_favorite_off_30, getTheme()));
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.white, getTheme()), PorterDuff.Mode.SRC_IN);

                    break;

                case 2:
                    tab.setText("Chats");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_message_off_30, getTheme()));
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.white, getTheme()), PorterDuff.Mode.SRC_IN);
                    break;

                case 3:
                    tab.setText("Notes");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_notifications_off_30, getTheme()));
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.white, getTheme()), PorterDuff.Mode.SRC_IN);

                    break;

                case 4:
                    tab.setText("Profile");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_account_circle_off_30, getTheme()));
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.white, getTheme()), PorterDuff.Mode.SRC_IN);

                    break;

            }


        }
    }

}
