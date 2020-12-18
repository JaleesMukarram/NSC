package com.example.nustsocialcircle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.nustsocialcircle.CompleteRegistrationFragments.ViewPagerCompleteRegistrationAdapter;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseDatabaseObjectUploadTask;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseStorageImageUploadTask;
import com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass;
import com.example.nustsocialcircle.HelpingClasses.CustomToast;
import com.example.nustsocialcircle.Interfaces.AuthStateUpdatingEnabled;
import com.example.nustsocialcircle.Interfaces.DatabaseUploadListener;
import com.example.nustsocialcircle.Interfaces.StorageUploadListener;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class GeneralUserCompleteRegistration extends AppCompatActivity implements AuthStateUpdatingEnabled {

    private static final String TAG = "CompleteRegTAG";

    private TextView EmailShowingTV;

    private ViewPager viewPager;
    private ViewPagerCompleteRegistrationAdapter viewPagerAdapter;
    private LinearLayout moveNext, moveBack;
    private TextView nextShowing;
    private String[] nameFragmentInfoArray;
    private String selectedSchool;

    private NextListener nextListener;
    private FinishListener finishListener;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String profileImageURI;

    private boolean isProfileUploading;

    private BroadcastReceiver AuthStateChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);

        initializeViews();
        initializeComponents();

    }


    private void initializeViews() {

        viewPager = this.findViewById(R.id.VPGeneralUserCompleteRegistrationAllProfileOptions);
        viewPagerAdapter = new ViewPagerCompleteRegistrationAdapter(getSupportFragmentManager(), 1, this);
        viewPager.setAdapter(viewPagerAdapter);

        moveNext = this.findViewById(R.id.LLGeneralUserCompleteRegistrationNext);
        moveBack = this.findViewById(R.id.LLGeneralUserCompleteRegistrationBack);

        EmailShowingTV = this.findViewById(R.id.TVGeneralUserCompleteRegistrationUserEmail);

        nextShowing = this.findViewById(R.id.TVGeneralUserCompleteRegistrationNextShowing);

        nextListener = new NextListener();
        finishListener = new FinishListener();

        moveNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        moveBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                simulatePageChanged(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOffscreenPageLimit(3);


    }

    private void setNextListener() {

        moveNext.setOnClickListener(null);
        moveNext.setOnClickListener(nextListener);


    }

    private void setFinishListener() {

        moveNext.setOnClickListener(null);
        moveNext.setOnClickListener(finishListener);


    }

    private void simulatePageChanged(int position) {

        switch (position) {

            case 0:

                moveBack.setVisibility(View.INVISIBLE);
                moveNext.setVisibility(View.VISIBLE);
                nextShowing.setText(getResources().getString(R.string.next));
                setNextListener();

                break;

            case 1:

                moveBack.setVisibility(View.VISIBLE);
                moveNext.setVisibility(View.VISIBLE);
                nextShowing.setText(getResources().getString(R.string.next));
                setNextListener();

                break;

            case 2:

                moveBack.setVisibility(View.VISIBLE);
                moveNext.setVisibility(View.VISIBLE);
                nextShowing.setText(getResources().getString(R.string.finish));
                setFinishListener();

                break;


            default:

                moveBack.setVisibility(View.INVISIBLE);
                moveNext.setVisibility(View.VISIBLE);
                nextShowing.setText(getResources().getString(R.string.next));
                setNextListener();

        }

        setProfileData();

    }

    private void setProfileData() {

        nameFragmentInfoArray = viewPagerAdapter.nameFragment.getNameFragmentInfo();
        selectedSchool = viewPagerAdapter.schoolFragment.getSelectedSchool();

        viewPagerAdapter.profileImageFragment.setData(nameFragmentInfoArray, selectedSchool);

    }

    private void initializeComponents() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        EmailShowingTV.setText(mUser.getEmail());

    }

    private void initializeListeners() {

    }

    private void saveUserProfileInformation() {

        if (!isProfileUploading) {

            nameFragmentInfoArray = viewPagerAdapter.nameFragment.getNameFragmentInfo();
            selectedSchool = viewPagerAdapter.schoolFragment.getSelectedSchool();

            if (isNameInfoValid() & isSchoolSelected()) {

                GeneralUser user = new GeneralUser(nameFragmentInfoArray[0], selectedSchool, nameFragmentInfoArray[1], nameFragmentInfoArray[2], mUser.getUid());

                if (profileImageURI != null) {

                    user.setmProfileUri(profileImageURI);
                }

                new UploadUserToDatabase(user).execute();
            }


        } else {

            CustomToast.make_toast_LIGHT(this, "Please wait for image uploading", Gravity.BOTTOM);
        }

    }

    private boolean isSchoolSelected() {

        if (selectedSchool == null || "".equals(selectedSchool)) {

            viewPagerAdapter.schoolFragment.highlightSelectSchool();
            return false;
        }

        return true;

    }

    private boolean isNameInfoValid() {

        boolean returner = true;

        String name = nameFragmentInfoArray[0];
        String section = nameFragmentInfoArray[1];
        String badge = nameFragmentInfoArray[2];

        if (name.length() < 2) {

            viewPager.setCurrentItem(0);
            viewPagerAdapter.nameFragment.highlightName();
            returner = false;

        }

        if (section.length() < 5) {

            viewPager.setCurrentItem(0);
            viewPagerAdapter.nameFragment.highlightSection();
            returner = false;
        }

        if (badge == null) {

            viewPager.setCurrentItem(0);
            returner = false;

        }

        return returner;
    }

    public void uploadUserProfileImageToStorage(File file) {

        if (file != null && file.exists()) {

            isProfileUploading = true;

            String childReference = mUser.getUid() + "/" + file.getName();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_PROFILE_STORAGE);


            FirebaseStorageImageUploadTask task = new FirebaseStorageImageUploadTask(Uri.fromFile(file), childReference, storageReference);

            task.setListener(new StorageUploadListener() {
                @Override
                public void listenToProgress(int progress) {
                }

                @Override
                public void onCompleteFailure() {

                    isProfileUploading = false;
                    CustomToast.make_toast_DARK(GeneralUserCompleteRegistration.this, "Image uploading failed", Gravity.BOTTOM);
                }

                @Override
                public void onTaskCompleted(Uri uri) {

                    isProfileUploading = false;
                    profileImageURI = uri.toString();
                    viewPagerAdapter.profileImageFragment.imageUploaded();

                }

            });

            task.execute();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        startListeningToInternetState();
        startListeningToUserStateChange();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListeningToInternetState();
        stopListeningUserToStateChange();
    }

    //User states
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

        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(GeneralUserCompleteRegistration.this, SignUp.class));
            finish();
        }

    }

    //Internet states
    @Override
    public void onInternetConnectionLost() {

    }

    @Override
    public void onInternetConnectionResume() {

    }

    @Override
    public void startListeningToInternetState() {

    }

    @Override
    public void onInernetConnectionStateChanged(Intent intent) {

    }

    @Override
    public void stopListeningToInternetState() {

    }

    @Override
    public void askInternetConnectionAsync() {

    }

    private class NextListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);


        }
    }

    private class FinishListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            saveUserProfileInformation();

        }
    }

    private class UploadUserToDatabase implements DatabaseUploadListener {

        private final DatabaseReference referene = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE);

        private GeneralUser user;
        private String key;

        private UploadUserToDatabase(GeneralUser user) {
            this.user = user;
            this.key = user.getmUid();
        }

        public void execute() {

            FirebaseDatabaseObjectUploadTask upload = new FirebaseDatabaseObjectUploadTask(user, referene, key);
            upload.setListener(this);

        }

        @Override
        public void listenToProgress(int progress) {

        }

        @Override
        public void onCompleteFailure() {

            CustomToast.make_toast_LIGHT(GeneralUserCompleteRegistration.this, "Profile updating failed", Gravity.BOTTOM);

        }

        @Override
        public void onTaskCompleted() {

            CustomToast.make_toast_LIGHT(GeneralUserCompleteRegistration.this, "Profile updating successful", Gravity.BOTTOM);

            Intent intent = new Intent(GeneralUserCompleteRegistration.this, HomeScreenGeneral.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);

        }

    }
}

