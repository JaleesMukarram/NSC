package com.example.nustsocialcircle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nustsocialcircle.Circle.Circle;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseDatabaseObjectUploadTask;
import com.example.nustsocialcircle.HelpingClasses.CustomToast;
import com.example.nustsocialcircle.Interfaces.DatabaseUploadListener;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class GeneralOthersProfileShowing extends AppCompatActivity {

    private static final String TAG = "GeneralOthersTAG";

    private ProgressBar ProfileLoadingPB;
    private RelativeLayout AddToCircleContainerRL;

    private Button AddToCircleBTN, RemoveFromCircleBTN;

    private TextView CircleDateShowingTV;

    private Circle mCircle;

    private boolean oneChecked;

    private TextView NameShowingTV, SchoolShowingTV, SectionShowingTV, BadgeShowingTV;
    private ImageView ProfileImageIV;
    private String VisitingUserID;
    private GeneralUser VisitingUser;


    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_others_profile_showing);

        initializeViews();
        initializeComponents();
        getDataForThisUser();

    }

    private void initializeViews() {

        NameShowingTV = (TextView) findViewById(R.id.TVGeneralOthersProfileShowingNameShowing);
        SchoolShowingTV = (TextView) findViewById(R.id.TVGeneralOthersProfileShowingSchoolShowing);
        SectionShowingTV = (TextView) findViewById(R.id.TVGeneralOthersProfileShowingSectionShowing);
        BadgeShowingTV = (TextView) findViewById(R.id.TVGeneralOthersProfileShowingBadgeShowing);
        ProfileLoadingPB = (ProgressBar) findViewById(R.id.PBGeneralOthersProfileShowingUserProfileLoading);
        AddToCircleContainerRL = (RelativeLayout) findViewById(R.id.RLGeneralOthersProfileShowingFriendStatus);

        ProfileImageIV = (ImageView) findViewById(R.id.IVGeneralOthersProfileShowingProfilePic);

    }

    private void initializeComponents() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        VisitingUserID = intent.getStringExtra("USER_ID");

        if (VisitingUserID != null) {

            getDataForThisUser();
            checkInThreadIfTheUserIsInCircle();
            View logout = findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    startActivity(new Intent(GeneralOthersProfileShowing.this, SignIn.class));
                }
            });

        } else {

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }

    // Load data for this user with the intent ID
    private void getDataForThisUser() {

        Thread userDataGettingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE)
                        .orderByChild("mUid")
                        .equalTo(VisitingUserID);


                query.addValueEventListener(new UserCompleteInfoValueEventListener());

                Log.d(TAG, "Listener attached");

            }
        });

        userDataGettingThread.start();
    }

    // Get all the circles crated by me and then check for this user in that list
    private void checkInThreadIfTheUserIsInCircle() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                Query query = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserCircle.CIRCLE_REFERENCE)
                        .orderByChild("mAddingUserID")
                        .equalTo(mUser.getUid());

                query.addListenerForSingleValueEvent(new GetAllCircleCreatedByMe());

            }
        });
        thread.start();

    }

    // Load the data to the UI components after loading from database
    private void setUserDataToUI() {

        if (VisitingUser != null) {

            NameShowingTV.setText(VisitingUser.getmName());
            SchoolShowingTV.setText(VisitingUser.getmSchool());
            SectionShowingTV.setText(VisitingUser.getmSection());
            BadgeShowingTV.setText(VisitingUser.getmBadge());

            if (VisitingUser.getmProfileUri() != null) {
                Picasso.get()
                        .load(VisitingUser.getmProfileUri())
                        .into(ProfileImageIV);
            }

        }

        ProfileLoadingPB.setVisibility(View.INVISIBLE);

    }

    // Show add to circle view if the user is not already in circle
    private void addToCircleView() {

        View view = LayoutInflater.from(this).inflate(R.layout.add_to_circle_add_option_view, AddToCircleContainerRL, false);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

        AddToCircleContainerRL.removeAllViews();

        AddToCircleBTN = view.findViewById(R.id.BTNAddToCircleViewAdd);
        AddToCircleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAddToCircle();

            }
        });

        AddToCircleContainerRL.addView(view);

    }

    // Show remove from circle view if the user is already in circle
    private void removeFromCircleView() {

        View view = LayoutInflater.from(this).inflate(R.layout.add_to_circle_remove_option_view, AddToCircleContainerRL, false);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

        AddToCircleContainerRL.removeAllViews();

        RemoveFromCircleBTN = view.findViewById(R.id.BTNRemoveFromCircleViewRemove);
        CircleDateShowingTV = view.findViewById(R.id.TVRemoveFromCircleViewAddDate);

        CircleDateShowingTV.setText(mCircle.getmAddedDate().toString());

        RemoveFromCircleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCircle != null) {

                    performDeleteFromCircle();

                } else {

                    CustomToast.make_toast_LIGHT(getApplicationContext(), "Error while adding", Gravity.BOTTOM);

                }
            }
        });

        AddToCircleContainerRL.addView(view);

    }

    // if the user wants to add this user to the circle
    private void performAddToCircle() {

        Circle circle = new Circle(mUser.getUid(), VisitingUserID);
        mCircle = circle;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserCircle.CIRCLE_REFERENCE);
        FirebaseDatabaseObjectUploadTask task = new FirebaseDatabaseObjectUploadTask(circle, reference, circle.getmCircleID());

        task.setListener(new DatabaseUploadListener() {
            @Override
            public void listenToProgress(int progress) {

                Log.d(TAG, "Adding circle progress: " + progress);
            }

            @Override
            public void onCompleteFailure() {

                Log.d(TAG, "Adding circle completely failed");

            }

            @Override
            public void onTaskCompleted() {

                Log.d(TAG, "Adding circle completed");
                removeFromCircleView();
            }
        });
    }

    // if the user wants to remove this user from the circle
    private void performDeleteFromCircle() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserCircle.CIRCLE_REFERENCE);

        reference.child(mCircle.getmCircleID()).removeValue()

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(GeneralOthersProfileShowing.this, "ID: " + mCircle.getmCircleID() + " deleted", Toast.LENGTH_SHORT).show();
                        addToCircleView();

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    // Load all the information from the database for this user
    private class UserCompleteInfoValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                Log.d(TAG, "DataSnapshot received");


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    VisitingUser = child.getValue(GeneralUser.class);
                    setUserDataToUI();
                    return;
                }

            } else {

                Log.d(TAG, "DataSnapshot empty");
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

            Log.d(TAG, "DataSnapshot error: " + databaseError.getMessage());

        }
    }

    // Check if this user is in my circle
    private class GetAllCircleCreatedByMe implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            boolean found = false;

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attributes : child.getChildren()) {

                        if ("mAddedUserID".equals(attributes.getKey())) {

                            if (VisitingUserID.equals(attributes.getValue())) {

                                Circle circle = child.getValue(Circle.class);

                                if (circle != null) {

                                    mCircle = circle;
                                    removeFromCircleView();

                                    return;
                                } else {

                                    Log.d(TAG, "Error while checking circle");
                                }

                            }
                        }

                    }

                }

                addToCircleView();

            } else {

                addToCircleView();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }


}


//    private void initializeAllFirebaseReferences() {
//
//        queryifThisPersonHasSentMeRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE)
//                .orderByChild("mSendingUserID")
//                .equalTo(VisitingUser.getmUid());
//
//        queryAgainstMyRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE)
//                .orderByChild("mSendingUserID")
//                .equalTo(mUser.getUid());
//
//        queryIfUserHasAcceptedMyRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_ACCEPTED_FRIEND_REQUESTS_REFERENCE)
//                .orderByChild("acceptingUserID")
//                .equalTo(VisitingUser.getmUid());
//
//        queryIfUserHasRejectedMyRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_REJECTED_FRIEND_REQUESTS_REFERENCE)
//                .orderByChild("rejectingUserID")
//                .equalTo(VisitingUser.getmUid());
//
//        queryIfUserHasUnFrienededMe = FirebaseDatabase.getInstance().getReference(GENERAL_USER_ACCEPTED_FRIEND_REQUESTS_REFERENCE);
//
//    }
//
//
//    //Listener 1
//    private void startListeningIfThisUserHasSentMeRequest() {
//
//        queryifThisPersonHasSentMeRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE)
//                .orderByChild("mSendingUserID")
//                .equalTo(VisitingUser.getmUid());
//
//        //Setting a listener to check if that visiting person has sent me a request
//        listenIfVisitingPersonHasSentMeFriendRequest = new ListenIfVisitingPersonHasSentMeFriendRequest();
//        queryifThisPersonHasSentMeRequest.addValueEventListener(listenIfVisitingPersonHasSentMeFriendRequest);
//
//
//    }
//
//    private void stopListeningIfThisUserHasSentMeRequest() {
//
//        //Stopping a listener to check if that visiting person has sent me a request
//        if (listenIfVisitingPersonHasSentMeFriendRequest != null) {
//            queryifThisPersonHasSentMeRequest.removeEventListener(listenIfVisitingPersonHasSentMeFriendRequest);
//            listenIfVisitingPersonHasSentMeFriendRequest = null;
//        }
//    }
//
//    //Listener 2
//    private void startListeningIfIHaveSentRequestToThatUser() {
//
//        queryAgainstMyRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE)
//                .orderByChild("mSendingUserID")
//                .equalTo(mUser.getUid());
//
//        //Setting a lisnter to check if I have sent a request to that visiting person
//        listenAgainstMySendedRequest = new ListenAgainstMySendedRequest();
//        queryAgainstMyRequest.addValueEventListener(listenAgainstMySendedRequest);
//
//    }
//
//    private void stopListeningIfIHaveSentRequestToThatUser() {
//
//        //Stopping a lisnter to check if I have sent a request to that visiting person
//        if (listenAgainstMySendedRequest != null) {
//            queryAgainstMyRequest.removeEventListener(listenAgainstMySendedRequest);
//            listenAgainstMySendedRequest = null;
//        }
//    }
//
//    //Listener3
//    private void startListeningIfTheUserHasAcceptedMyRequest() {
//
//        //Setting a listener if user accepts my request
//        queryIfUserHasAcceptedMyRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_ACCEPTED_FRIEND_REQUESTS_REFERENCE)
//                .orderByChild("acceptingUserID")
//                .equalTo(VisitingUser.getmUid());
//
//        listenIfUserHasAcceptedMyRequest = new ListenIfUserHasAcceptedMyRequest();
//        queryIfUserHasAcceptedMyRequest.addValueEventListener(listenIfUserHasAcceptedMyRequest);
//
//
//    }
//
//    private void stopListeningIfTheUserHasAcceptedMyRequest() {
//
//        //Stopping a listener if user accepts my request
//        if (listenIfUserHasAcceptedMyRequest != null) {
//
//            queryIfUserHasAcceptedMyRequest.removeEventListener(listenIfUserHasAcceptedMyRequest);
//            listenIfUserHasAcceptedMyRequest = null;
//        }
//    }
//
//    //Listener4
//    private void startListeningIfTheUserHasRejectedMyRequest() {
//
//        queryIfUserHasRejectedMyRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_REJECTED_FRIEND_REQUESTS_REFERENCE)
//                .orderByChild("rejectingUserID")
//                .equalTo(VisitingUser.getmUid());
//
//        listenIfUserHasRejectedMyRequest = new ListenIfUserHasRejectedMyRequest();
//        queryIfUserHasRejectedMyRequest.addValueEventListener(listenIfUserHasRejectedMyRequest);
//
//
//    }
//
//    private void stopListeningIfTheUserHasRejectedMyRequest() {
//
//        if (listenIfUserHasRejectedMyRequest != null) {
//            queryIfUserHasRejectedMyRequest.removeEventListener(listenIfUserHasRejectedMyRequest);
//            listenIfUserHasRejectedMyRequest = null;
//
//        }
//
//    }
//
//
//    //Listeenr 5
//    private void startListeningIfUserHasUnfriendedMe() {
//
//        queryIfUserHasUnFrienededMe = FirebaseDatabase.getInstance().getReference(GENERAL_USER_ACCEPTED_FRIEND_REQUESTS_REFERENCE);
//        listenIfUserHasUnFriendedMe = new ListenIfUserHasUnFriendedMe();
//        queryIfUserHasUnFrienededMe.addValueEventListener(listenIfUserHasUnFriendedMe);
//
//
//    }
//
//    private void stopListeningIfUserHasUnfriendedMe() {
//
//        if (listenIfUserHasUnFriendedMe != null) {
//            queryIfUserHasUnFrienededMe.removeEventListener(listenIfUserHasUnFriendedMe);
//            listenIfUserHasUnFriendedMe = null;
//        }
//    }
//
//
//    private void setVisitingUserFields(GeneralUser user) {
//
//        if (VisitingUser != null) {
//
//            NameShowingTV.setText(VisitingUser.getmName());
//            SchoolShowingTV.setText(VisitingUser.getmSchool());
//            SectionShowingTV.setText(VisitingUser.getmSection());
//
//            Picasso.get().load(VisitingUser.getmProfileUri())
//                    .into(ProfileImageIV);
//
//        } else {
//
//        }
//
//    }
//
//
//    //Used to send the request to the visiting user
//    private void sendRequestoSpecificUserInDatabase() {
//
//        FriendRequest request = new FriendRequest(mUser.getUid(), VisitingUser.getmUid(), new Date());
//
//        databaseReference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
//
//
//        databaseReference.push().setValue(request)
//
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//                        activateCancelFriendRequestMode();
////                        Log.d(TAG, "Friend request sent successfully");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        activateSendFriendRequestMode();
////                        Log.d(TAG, "Friend Request sent failed with exception: " + e.toString());
//
//                    }
//                })
//
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//
//                        activateSendFriendRequestMode();
//
//                    }
//                });
//
//    }
//
//
//    //Find and delete the request I sent to the vising user
//    private void cancelRequestForSpecificUserInDatabase() {
//
//        databaseReference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
//
//        //Get all request sent by me
//        Query query = databaseReference.orderByChild("mSendingUserID")
//                .equalTo(mUser.getUid());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()) {
//
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                        FriendRequest request = child.getValue(FriendRequest.class);
//
//                        //If the request has been sent by me to the visiting user
//                        if (VisitingUser.getmUid().equals(request.getmSendToUserID())) {
//
//                            cancelFollowingFriendRequestInDatabase(child.getKey());
//                            return;
//
//                        }
//                    }
//
//
////                    Log.d(TAG, "No such request sent by the user");
//                    activateCancelFriendRequestMode();
//
//                } else {
//
////                    Log.d(TAG, "No request exists in database");
//                    activateCancelFriendRequestMode();
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                activateCancelFriendRequestMode();
//
//            }
//        });
//    }
//
//
//    //Delete the key of request that is sent by me to the visiting user
//    private void cancelFollowingFriendRequestInDatabase(String key) {
//
//        databaseReference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
//
//        databaseReference.child(key).removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//                        activateSendFriendRequestMode();
////                        Log.d(TAG, "Request deleted successfully");
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        activateCancelFriendRequestMode();
////                        Log.d(TAG, "could not delete specific request");
//
//                    }
//                })
//
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//
//                        activateCancelFriendRequestMode();
////                        Log.d(TAG, "specific request deletion failed");
//
//                    }
//                });
//
//    }
//
//
//    //Accept the request sent to me by the visiting user
//    private void acceptRequestForSpecificUserToDatabase() {
//
//
//    }
//
//    //This will find if this person has sent me the request
//    private void findIfThisPersonHasSentMeFriendRequest(final boolean reject) {
//
//
//        queryifThisPersonHasSentMeRequest = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
//
//        //Get all request sent by the visiting user
//        Query query = queryifThisPersonHasSentMeRequest.orderByChild("mSendingUserID")
//                .equalTo(VisitingUser.getmUid());
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()) {
//
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                        FriendRequest request = child.getValue(FriendRequest.class);
//
//                        //If the request has been sent to me by the visiting user
//                        if (mUser.getUid().equals(request.getmSendToUserID())) {
//
//                            Log.d(TAG, "Friend request by the visiting person to me detected");
//
//                            if (reject) {
//
//                                rejectFollowingFriendRequestInDatabase(child.getKey(), request);
//                                return;
//                            } else {
//
//                                Log.d(TAG, "Now activating cancel friend request mode to accept or reject this request sent by the visiting user");
//                                activateAcceptRequestMode();
//
//                                return;
//                            }
//                        }
//                    }
//
//                    Log.d(TAG, "No such request sent by the user");
//                    activateSendFriendRequestMode();
//
//                } else {
//
////                    Log.d(TAG, "No request exists in database");
//                    activateSendFriendRequestMode();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    //Reject this request by deleting sent by the visiting user to me
//    private void rejectFollowingFriendRequestInDatabase(String key, final FriendRequest request) {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
//
//        reference.child(key).removeValue()
//
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//
//                        activateSendFriendRequestMode();
//
//                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(GENERAL_USER_REJECTED_FRIEND_REQUESTS_REFERENCE);
//
//                        reference1.push().setValue(new RejectedRequest(request.getmSendingDate(), VisitingUser.getmUid(), mUser.getUid()));
//
//
//                    }
//                })
//
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        activateAcceptRequestMode();
//
//                    }
//                })
//
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//
//                        activateAcceptRequestMode();
//
//                    }
//                })
//        ;
//    }
//
//    //Unfriend this visiting person by me
//    private void findIfThisPersonIsMyFriend(final boolean unfriend) {
//
//        databaseReference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_ACCEPTED_FRIEND_REQUESTS_REFERENCE);
//
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()) {
//
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                        AcceptedRequest request = child.getValue(AcceptedRequest.class);
//
//                        if (
//                                (request.getAcceptingUserID().equals(mUser.getUid()) &&
//                                        request.getSendingUserID().equals(VisitingUser.getmUid()))
//                                        ||
//                                        (request.getAcceptingUserID().equals(VisitingUser.getmUid()) &&
//                                                request.getSendingUserID().equals(mUser.getUid()))
//                        ) {
//
//                            if (unfriend) {
//
//                                unFriendFollowingFriends(child.getKey(), request);
//
//                            } else {
//
//                                activateRemoveFriendMode();
//
//                            }
//
//
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//    }
//
//
//    private void unFriendFollowingFriends(String key, AcceptedRequest request) {
//
//        databaseReference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_ACCEPTED_FRIEND_REQUESTS_REFERENCE);
//
//        databaseReference.child(key)
//                .removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//                        activateSendFriendRequestMode();
//                        Unfriended unfriended = new Unfriended(mUser.getUid(), VisitingUser.getmUid());
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                })
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//
//                    }
//                });
//
//
//    }
//
//
//    //All the modes used for various request positions
//
//
//    private void activateSendFriendRequestMode() {
//
//        RequestSendBTN.setVisibility(View.VISIBLE);
//        RequestSendBTN.setText("Send Request");
//        RequestSendBTN.setOnClickListener(new SendFriendRequest());
//
//
//        RequestStatusUpdaterTV.setVisibility(View.INVISIBLE);
//
//        AcceptRequestBTN.setVisibility(View.INVISIBLE);
//        RejectRequestBTN.setVisibility(View.INVISIBLE);
//
//
//        startListeningIfThisUserHasSentMeRequest();
//
//
//        stopListeningIfIHaveSentRequestToThatUser();
//
//        stopListeningIfTheUserHasRejectedMyRequest();
//        stopListeningIfTheUserHasAcceptedMyRequest();
//
//        stopListeningIfUserHasUnfriendedMe();
//
//    }
//
//    private void activateSendingFriendRequestMode() {
//
//        RequestStatusUpdaterTV.setVisibility(View.VISIBLE);
//        RequestStatusUpdaterTV.setText("Sending Request");
//
//        RequestSendBTN.setVisibility(View.INVISIBLE);
//
//        AcceptRequestBTN.setVisibility(View.INVISIBLE);
//        RejectRequestBTN.setVisibility(View.INVISIBLE);
//    }
//
//    private void activateCancelFriendRequestMode() {
//
//        RequestSendBTN.setVisibility(View.VISIBLE);
//        RequestSendBTN.setText("Cancel Request");
//        RequestSendBTN.setOnClickListener(new CancelFriendRequest());
//
//        RequestStatusUpdaterTV.setVisibility(View.INVISIBLE);
//
//        AcceptRequestBTN.setVisibility(View.INVISIBLE);
//        RejectRequestBTN.setVisibility(View.INVISIBLE);
//
//        startListeningIfTheUserHasAcceptedMyRequest();
//        startListeningIfTheUserHasRejectedMyRequest();
//
//        stopListeningIfThisUserHasSentMeRequest();
//
//        stopListeningIfUserHasUnfriendedMe();
//
//
//    }
//
//    private void activateCancelingFriendRequestMode() {
//
//        RequestStatusUpdaterTV.setVisibility(View.VISIBLE);
//        RequestStatusUpdaterTV.setText("Canceling Request");
//
//        RequestSendBTN.setVisibility(View.INVISIBLE);
//
//        AcceptRequestBTN.setVisibility(View.INVISIBLE);
//        RejectRequestBTN.setVisibility(View.INVISIBLE);
//
//    }
//
//    private void activateAcceptRequestMode() {
//
//
//        RequestStatusUpdaterTV.setVisibility(View.VISIBLE);
//        RequestStatusUpdaterTV.setText(VisitingUser.getmName() + " has sent you a friend request");
//
//
//        AcceptRequestBTN.setVisibility(View.VISIBLE);
//        RejectRequestBTN.setVisibility(View.VISIBLE);
//
//        AcceptRequestBTN.setOnClickListener(new AcceptFriendRequest());
//        RejectRequestBTN.setOnClickListener(new RejectFriendRequest());
//
//        RequestSendBTN.setVisibility(View.INVISIBLE);
//        RequestStatusUpdaterTV.setVisibility(View.VISIBLE);
//
//
//        stopListeningIfTheUserHasAcceptedMyRequest();
//        stopListeningIfTheUserHasRejectedMyRequest();
//
//        stopListeningIfIHaveSentRequestToThatUser();
//
//        stopListeningIfUserHasUnfriendedMe();
//
//
//    }
//
//    private void activateRemoveFriendMode() {
//
//        RequestSendBTN.setVisibility(View.VISIBLE);
//        RequestSendBTN.setText("Remove Friend");
//        RequestSendBTN.setOnClickListener(new RemoveFriend());
//
//
//        RequestStatusUpdaterTV.setVisibility(View.INVISIBLE);
//
//        AcceptRequestBTN.setVisibility(View.INVISIBLE);
//        RejectRequestBTN.setVisibility(View.INVISIBLE);
//
//        startListeningIfUserHasUnfriendedMe();
//
//        stopListeningIfTheUserHasAcceptedMyRequest();
//        stopListeningIfTheUserHasRejectedMyRequest();
//        stopListeningIfIHaveSentRequestToThatUser();
//        stopListeningIfThisUserHasSentMeRequest();
//
//
//    }
//
//
//    @Override
//    public void onBackPressed() {
//
//        Intent intent = new Intent(this, HomeScreenGeneral.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//        startActivity(intent);
//
//    }
//
//private class SendFriendRequest implements View.OnClickListener {
//
//    @Override
//    public void onClick(View v) {
//
//        activateSendingFriendRequestMode();
//        sendRequestoSpecificUserInDatabase();
//
//    }
//}
//
//private class CancelFriendRequest implements View.OnClickListener {
//
//    @Override
//    public void onClick(View v) {
//
//        if (listenAgainstMySendedRequest != null) {
//
//            listenAgainstMySendedRequest.cancelFollowgingFriendRequest();
//        }
//
//    }
//}
//
//private class RemoveFriend implements View.OnClickListener {
//
//    @Override
//    public void onClick(View v) {
//
//        findIfThisPersonIsMyFriend(true);
//
//    }
//}
//
//private class AcceptFriendRequest implements View.OnClickListener {
//    @Override
//    public void onClick(View v) {
//        if (listenIfVisitingPersonHasSentMeFriendRequest != null) {
//
//            listenIfVisitingPersonHasSentMeFriendRequest.acceptTheFollowingRequestID();
//
//        }
//    }
//}
//
//private class RejectFriendRequest implements View.OnClickListener {
//
//    @Override
//    public void onClick(View v) {
//
//        if (listenIfVisitingPersonHasSentMeFriendRequest != null) {
//
//            listenIfVisitingPersonHasSentMeFriendRequest.rejectFriendRequestByVisitingUser();
//
//        }
//    }
//}
//
//private class ListenIfVisitingPersonHasSentMeFriendRequest implements ValueEventListener {
//
//    private String requestKey;
//    private FriendRequest request;
//
//    private boolean ifRequestDataExisits() {
//
//        if (requestKey != null && request != null) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private void acceptTheFollowingRequestID() {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_ACCEPTED_FRIEND_REQUESTS_REFERENCE);
//
//        final String key = reference.push().getKey();
//
//        AcceptedRequest acceptFriendRequest = new AcceptedRequest(request.getmSendingDate(), VisitingUser.getmUid(), mUser.getUid());
//
//        reference.child(key)
//
//                .setValue(acceptFriendRequest)
//
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//                        activateRemoveFriendMode();
//                        deleteFollowingFriendRequestID(key);
//
//                    }
//                })
//
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        Toast.makeText(GeneralOthersProfileShowing.this, "Error while performing operation", Toast.LENGTH_SHORT).show();
//
//                    }
//                })
//
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//
//                        Toast.makeText(GeneralOthersProfileShowing.this, "operation cancelled due to unkwnon error", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//    }
//
//    private void rejectFriendRequestByVisitingUser() {
//
//        if (ifRequestDataExisits()) {
//
//            final Date sentDate = this.request.getmSendingDate();
//
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
//
//            reference.child(this.requestKey).
//
//                    removeValue()
//
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            addToRejectedRequests(new RejectedRequest(sentDate, VisitingUser.getmUid(), mUser.getUid()));
//
//                        }
//                    })
//
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//
//                            Toast.makeText(GeneralOthersProfileShowing.this, "Unble to perfrom operation. Pkease check internet connection", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//
//                    .addOnCanceledListener(new OnCanceledListener() {
//                        @Override
//                        public void onCanceled() {
//
//                            Toast.makeText(GeneralOthersProfileShowing.this, "Unble to perfrom operation. Pkease check internet connection", Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//        } else {
//
//            Toast.makeText(GeneralOthersProfileShowing.this, "Unble to perfrom operation. Pkease check internet connection", Toast.LENGTH_SHORT).show();
//
//
//        }
//    }
//
//    private void addToRejectedRequests(RejectedRequest rejectedRequest) {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_REJECTED_FRIEND_REQUESTS_REFERENCE);
//
//        reference.push().setValue(rejectedRequest)
//
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//                        activateSendFriendRequestMode();
//
//                    }
//                })
//
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                })
//
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//
//                    }
//                });
//    }
//
//
//    private void deleteFollowingFriendRequestID(String acceptedKey) {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
//
//        reference.child(requestKey)
//                .removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//
//                    }
//                })
//
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//
//                    }
//                })
//
//
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//
//                    }
//                })
//        ;
//
//    }
//
//
//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        if (dataSnapshot.exists()) {
//
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                FriendRequest request = child.getValue(FriendRequest.class);
//
//                //If the request has been sent to me by the visiting user
//                if (mUser.getUid().equals(request.getmSendToUserID())) {
//
//                    Log.d(TAG, "Friend request by the visiting person to me detected");
//
//                    this.requestKey = child.getKey();
//                    this.request = request;
//                    activateAcceptRequestMode();
//                    break;
//                }
//
//            }
//
//            Log.d(TAG, "No such request sent by the user");
//
//        }
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//}
//
//private class ListenAgainstMySendedRequest implements ValueEventListener {
//
//    private String requestKey;
//    private FriendRequest request;
//
//    private boolean ifRequestDataExist() {
//
//        if (requestKey != null && request != null) {
//
//            return true;
//
//        } else {
//
//            return false;
//
//        }
//    }
//
//    private void cancelFollowgingFriendRequest() {
//
//        if (ifRequestDataExist()) {
//
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
//
//            reference.child(requestKey)
//                    .removeValue()
//
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            activateSendFriendRequestMode();
//                            ListenAgainstMySendedRequest.this.request = null;
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    })
//
//                    .addOnCanceledListener(new OnCanceledListener() {
//                        @Override
//                        public void onCanceled() {
//
//                        }
//                    });
//        }
//    }
//
//
//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        if (dataSnapshot.exists()) {
//
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                FriendRequest request = child.getValue(FriendRequest.class);
//
//                //If the request has been sent by me to the visiting user
//                if (VisitingUser.getmUid().equals(request.getmSendToUserID())) {
//
//                    this.requestKey = child.getKey();
//                    this.request = request;
//                    activateCancelFriendRequestMode();
//                    Toast.makeText(GeneralOthersProfileShowing.this, "Request already sent to the user", Toast.LENGTH_SHORT).show();
//                    break;
//
//                }
//            }
//
//        } else {
//
//        }
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//}
//
//private class ListenIfUserHasAcceptedMyRequest implements ValueEventListener {
//
//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        if (dataSnapshot.exists()) {
//
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                AcceptedRequest request = child.getValue(AcceptedRequest.class);
//
//                if (mUser.getUid().equals(request.getSendingUserID())) {
//
//                    Toast.makeText(GeneralOthersProfileShowing.this, "request accepted", Toast.LENGTH_SHORT).show();
//
//                    activateRemoveFriendMode();
//
//                }
//            }
//        }
//
//
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//}
//
//private class ListenIfUserHasRejectedMyRequest implements ValueEventListener {
//
//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        if (dataSnapshot.exists()) {
//
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                RejectedRequest request = child.getValue(RejectedRequest.class);
//
//                if (mUser.getUid().equals(request.getSendingUserID())) {
//
//                    Toast.makeText(GeneralOthersProfileShowing.this, "Request Rejected", Toast.LENGTH_SHORT).show();
//
//                    activateSendFriendRequestMode();
//
//                }
//            }
//        }
//
//
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//}
//
//private class ListenIfUserHasUnFriendedMe implements ValueEventListener {
//
//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        if (dataSnapshot.exists()) {
//
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                AcceptedRequest request = child.getValue(AcceptedRequest.class);
//
//                if (
//                        (request.getSendingUserID().equals(mUser.getUid())
//                                && request.getAcceptingUserID().equals(VisitingUser.getmUid()))
//                                ||
//                                (request.getSendingUserID().equals(VisitingUser.getmUid())
//                                        && request.getAcceptingUserID().equals(mUser.getUid()))
//
//                ) {
//
//                    Toast.makeText(GeneralOthersProfileShowing.this, "User unfriended me", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
//
//            }
//
//            activateSendFriendRequestMode();
//
//        }
//
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//}
