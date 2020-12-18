package com.example.nustsocialcircle.FragmentsHomeGeneral;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences;
import com.example.nustsocialcircle.GeneralOthersProfileShowing;
import com.example.nustsocialcircle.HelpingClasses.CustomSharedPreferencesPaths;
import com.example.nustsocialcircle.HelpingClasses.RecyclerViewDecorator;
import com.example.nustsocialcircle.ModalClasses.FriendRequest;
import com.example.nustsocialcircle.ModalClasses.FriendRequestShowing;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.example.nustsocialcircle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences.GeneralUserFriend.GENERAL_USER_FRIEND_REQUESTS_REFERENCE;


public class FragmentFavoriteGeneral extends Fragment {

    private static final String TAG = "FragmentFavoriteTAG";

    public List<Object> friendShowingList;

    private List<String> uIDS;
    private List<GeneralUser> tempList;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;

    private FriendsShowingRecyclerAdapter recyclerAdapter;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private DataSnapshot mSnapShot;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private GeneralUser currentUser;

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferenceEditor;

    private boolean allUsersLoaded;
    private boolean allRequestsLoaded;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite_general, null);

        initializeViews(view);
        initializeComponents(view);

        return view;

    }

    private void initializeViews(View view) {

        recyclerView = view.findViewById(R.id.RVFragmentFavoriteFriendsShowing);
        friendShowingList = new ArrayList<Object>();
        uIDS = new ArrayList<String>();
        tempList = new ArrayList<GeneralUser>();
        recyclerAdapter = new FriendsShowingRecyclerAdapter(friendShowingList);


        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.addItemDecoration(new RecyclerViewDecorator(15, 20));

    }

    private void initializeComponents(View view) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        preferences = getActivity().getSharedPreferences(CustomSharedPreferencesPaths.FragmentFavoriteGeneralPreferences.DEFAULT_PATH, Context.MODE_PRIVATE);
        preferenceEditor = preferences.edit();

        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE);

        addAllSuggestions();
        addAllFriendRequestsToMe();

    }

    private void addAllSuggestions() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mSnapShot = dataSnapshot;
                Log.d(TAG, "Loade all the users, passed to function for list adding. total: " + dataSnapshot.getChildrenCount());
                createAndAddUsersFromDataSnapShot(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addUserToOrigionalList() {

        Log.d(TAG, "Starting adding all the suggestions to the origional list");

        if (tempList.size() > 0) {

            if (uIDS.size() > 0) {

                for (GeneralUser user : tempList) {

                    String currentUserID = user.getmUid();

                    boolean requestFound = false;

                    for (String uID : uIDS) {

                        if (currentUserID.equals(uID)) {

                            Log.d(TAG, "User:: " + currentUserID + ":: has sent the request to me");
                            FriendRequestShowing showing = new FriendRequestShowing(user);
                            friendShowingList.add(showing);
                            requestFound = true;
                            break;

                        }
                    }

                    if (!requestFound) {

                        friendShowingList.add(user);

                    }
                }
            } else {
                Log.d(TAG, "No request found for me");
                for (GeneralUser user : tempList) {

                    friendShowingList.add(user);
                }

            }
        }

        recyclerAdapter.notifyDataSetChanged();

    }

    private void addAllFriendRequestsToMe() {

        Query query = FirebaseDatabase.getInstance().getReference(GENERAL_USER_FRIEND_REQUESTS_REFERENCE);
        query.orderByChild("mSendToUserID")
                .equalTo(mUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "Loaded all the friend requests to me. total: " + dataSnapshot.getChildrenCount());

                if (dataSnapshot.exists()) {


                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        FriendRequest request = child.getValue(FriendRequest.class);
                        uIDS.add(request.getmSendingUserID());
                        //Get all the users who sent me the friend requests

                    }
                }

                allRequestsLoaded = true;

                if (allUsersLoaded) {

                    addUserToOrigionalList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createAndAddUsersFromDataSnapShot(DataSnapshot dataSnapshot) {

        for (DataSnapshot child : dataSnapshot.getChildren()) {

            GeneralUser user = child.getValue(GeneralUser.class);

            if (this.currentUser == null && user.getmUid().equals(mUser.getUid())) {

                FragmentFavoriteGeneral.this.currentUser = user;
//                Log.d(TAG, "Current User ADDED");

            } else {

                tempList.add(user);

            }
        }

        allUsersLoaded = true;

        if (allRequestsLoaded) {

            addUserToOrigionalList();
        }
    }

    private void addSameSchoolSuggestions() {

        if (mSnapShot != null && currentUser != null) {

            friendShowingList.clear();

            Iterable<DataSnapshot> iterable = mSnapShot.getChildren();

            for (DataSnapshot child : iterable) {

//                FriendShowing user = child.getValue(FriendShowing.class);
//                if (user.getmSchool().equals(currentUser.getmSchool())) {
//                    friendShowingList.add(user);
//                }

            }
        }
    }

    private void addSameSectionSuggestions() {

    }

    private void addSameBatchSuggestions() {

    }

    private class FriendsShowingRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int SUGGESTION = 10;
        private static final int REQUEST = 20;

        List<Object> list;

        public FriendsShowingRecyclerAdapter(List<Object> friendShowingList) {
            this.list = friendShowingList;
        }

        @Override
        public int getItemViewType(int position) {

            if (friendShowingList.get(position) instanceof GeneralUser) {

                return SUGGESTION;

            } else if (friendShowingList.get(position) instanceof FriendRequestShowing) {

                return REQUEST;
            }

            return SUGGESTION;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;

            if (viewType == SUGGESTION) {

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_suggestion_view, null);
                return new SuggestionViewHolder(view);


            } else if (viewType == REQUEST) {

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_view, null);
                return new RequestViewHolder(view);


            } else {

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_suggestion_view, null);
                return new SuggestionViewHolder(view);

            }


        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof SuggestionViewHolder) {

                ((SuggestionViewHolder) holder).setData(position);

            } else if (holder instanceof RequestViewHolder) {

                ((RequestViewHolder) holder).setData(position);
            }
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        private class SuggestionViewHolder extends RecyclerView.ViewHolder {

            private ImageView ImageIV;
            private TextView NameTV, SchoolTV;
            private RelativeLayout ParentLayout;


            public SuggestionViewHolder(@NonNull View itemView) {
                super(itemView);

                ImageIV = itemView.findViewById(R.id.IVFriendSuggestionProfileImage);
                NameTV = itemView.findViewById(R.id.TVFriendSuggestionNameShowing);
                SchoolTV = itemView.findViewById(R.id.TVFriendSuggestionSchoolShowing);
                ParentLayout = itemView.findViewById(R.id.RLFriendSuggestionParentLayout);

            }

            private void setData(final int position) {

                final GeneralUser friend;
                try {
                    friend = (GeneralUser) friendShowingList.get(position);

                } catch (Exception ex) {
                    return;
                }

                if (friend.getmProfileUri() != null) {

                    Picasso.get().load(friend.getmProfileUri()).resize(90, 90).into(ImageIV);

                }

                NameTV.setText(friend.getmName());
                SchoolTV.setText(friend.getmSchool());
                ParentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), GeneralOthersProfileShowing.class);

                        intent.putExtra("USER_ID", friend.getmUid());

                        Objects.requireNonNull(getContext()).startActivity(intent);
                    }
                });
            }

        }

        private class RequestViewHolder extends RecyclerView.ViewHolder {

            private ImageView ImageIV;
            private TextView NameTV, SchoolTV;
            private RelativeLayout ParentLayout;

            private Button AcceptRequest, RejectRequest;


            public RequestViewHolder(@NonNull View itemView) {
                super(itemView);

                ImageIV = itemView.findViewById(R.id.IVFriendRequestProfileImage);
                NameTV = itemView.findViewById(R.id.TVFriendRequestNameShowing);
                SchoolTV = itemView.findViewById(R.id.TVFriendRequestSchoolShowing);
                ParentLayout = itemView.findViewById(R.id.RLFriendRequestParentLayout);

                AcceptRequest = itemView.findViewById(R.id.BTNFriendRequestAcceptRequest);
                RejectRequest = itemView.findViewById(R.id.BTNFriendRequestRejectRequest);

            }

            private void setData(final int position) {

//                Log.d(TAG, "Setted data");

                FriendRequestShowing showing = (FriendRequestShowing) list.get(position);

                if (showing.getmGeneralUser().getmProfileUri() != null) {

                    Picasso.get().load(showing.getmGeneralUser().getmProfileUri()).resize(90, 90).into(ImageIV);
                }

                NameTV.setText(showing.getmGeneralUser().getmName());
                SchoolTV.setText(showing.getmGeneralUser().getmSchool());
                ParentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), GeneralOthersProfileShowing.class);

                        intent.putExtra("USER_ID", (String) ((FriendRequestShowing) list.get(position)).getmGeneralUser().getmUid());


                        getContext().startActivity(intent);

                    }
                });
            }
        }

    }


}


