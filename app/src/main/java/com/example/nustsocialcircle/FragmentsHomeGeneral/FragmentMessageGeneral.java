package com.example.nustsocialcircle.FragmentsHomeGeneral;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences;
import com.example.nustsocialcircle.GeneralUserChatWithGeneralUser;
import com.example.nustsocialcircle.HelpingClasses.RecyclerViewDecorator;
import com.example.nustsocialcircle.HomeScreenGeneral;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.example.nustsocialcircle.R;
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

public class FragmentMessageGeneral extends Fragment {

    public static final String TAG = "FragMessageTAG";
    private StartNewChatAdapter AdapterForNewChat;
    private TextView NoChatStartNewTV, NoChatSmallTV;
    private ImageView StartNewConversationIV;
    private RecyclerView AllChatsRecyclerRV;
    private LinearLayoutManager linearLayoutManager;

    private List<String> circledUsersIDsList;
    private List<GeneralUser> circleUsersList;

    private FirebaseUser mUser;


    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private boolean friendsLoaded;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_general, null);

        initializeViews(view);
        initializeComponents(view);

        return view;
    }


    private void initializeViews(View view) {

        StartNewConversationIV = (ImageView) view.findViewById(R.id.IVFragmentChatStartChat);

        NoChatStartNewTV = (TextView) view.findViewById(R.id.TVFragmentChatPromptingToStartNewChat);
        NoChatSmallTV = (TextView) view.findViewById(R.id.TVFragmentChatNoChatsShowing);

        AllChatsRecyclerRV = (RecyclerView) view.findViewById(R.id.RVFragmentChatShowAllFriendsToChat);

        circledUsersIDsList = new ArrayList<String>();
        circleUsersList = new ArrayList<GeneralUser>();

        linearLayoutManager = new LinearLayoutManager(getContext());
        AllChatsRecyclerRV.setLayoutManager(linearLayoutManager);
        AllChatsRecyclerRV.addItemDecoration(new RecyclerViewDecorator(10, 10));


    }

    private void initializeComponents(View view) {

        mUser = HomeScreenGeneral.mAuth.getCurrentUser();
        getAllCircleUsersIDs();
    }

    private void enableChatsLoadedMode() {

        StartNewConversationIV.setVisibility(View.INVISIBLE);
        NoChatStartNewTV.setVisibility(View.INVISIBLE);
        NoChatSmallTV.setVisibility(View.INVISIBLE);

        AllChatsRecyclerRV.setVisibility(View.VISIBLE);

    }

    private void enableNoChatAvailableMode() {

        StartNewConversationIV.setVisibility(View.VISIBLE);
        NoChatStartNewTV.setVisibility(View.VISIBLE);
        NoChatSmallTV.setVisibility(View.VISIBLE);

        AllChatsRecyclerRV.setVisibility(View.INVISIBLE);
    }

    private void getAllCircleUsersIDs() {

        Query query = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserCircle.CIRCLE_REFERENCE)
                .orderByChild("mAddingUserID")
                .equalTo(mUser.getUid());

        query.addValueEventListener(new CheckAllPeopleInMyCircle());

    }

    private void onAllIDsAdded() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE);
        reference.addListenerForSingleValueEvent(new GetAllUsersFromDatabase());
    }

    private void onAllUsersAdded() {

        friendsLoaded = true;
        StartNewConversationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableChatsLoadedMode();
            }
        });

        AdapterForNewChat = new StartNewChatAdapter(StartNewChatAdapter.MODE_NEW_CHAT_STARTING);

        AllChatsRecyclerRV.setAdapter(AdapterForNewChat);

        AdapterForNewChat.notifyDataSetChanged();

    }

    private class CheckAllPeopleInMyCircle implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attribute : child.getChildren()) {

                        if ("mAddedUserID".equals(attribute.getKey())) {

                            circledUsersIDsList.add(attribute.getValue(String.class));

                        }
                    }
                }
            }

            onAllIDsAdded();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    private class GetAllUsersFromDatabase implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attributes : child.getChildren()) {

                        if ("mUid".equals(attributes.getKey())) {

                            String uID = attributes.getValue(String.class);

                            if (circledUsersIDsList.contains(uID)) {

                                GeneralUser user = child.getValue(GeneralUser.class);

                                if (user != null) {

                                    circleUsersList.add(user);
                                }
                            }
                        }
                    }

                }

                onAllUsersAdded();
            } else {

                Log.d(TAG, "");

            }

            onAllUsersAdded();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    private class StartNewChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int MODE_NEW_CHAT_STARTING = 1;
        private static final int MODE_NEW_OLD_CHAT_SHOWING = 2;

        private int currentMode;


        private StartNewChatAdapter(int currentMode) {
            this.currentMode = currentMode;
        }

        @Override
        public int getItemViewType(int position) {

            return currentMode;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == MODE_NEW_CHAT_STARTING) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.start_new_chat_view, parent, false);
                return new SingleFriendView(view);


            } else {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.start_new_chat_view, parent, false);
                return new SingleFriendView(view);

            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof SingleFriendView) {

                SingleFriendView viewHolder = (SingleFriendView) holder;

                viewHolder.setData(position);

            } else {

                Log.d(TAG, "");

            }

        }

        @Override
        public int getItemCount() {

            return currentMode == MODE_NEW_CHAT_STARTING ? circleUsersList.size() : circledUsersIDsList.size();
        }


        private class SingleFriendView extends RecyclerView.ViewHolder {

            ImageView ProfileImageIV;
            TextView NameTV, SchoolTV;
            RelativeLayout MainContainerRL;

            private SingleFriendView(@NonNull View itemView) {

                super(itemView);

                ProfileImageIV = (ImageView) itemView.findViewById(R.id.IVStartNewChatProfileImage);
                NameTV = (TextView) itemView.findViewById(R.id.TVStartNewChatNameShowing);
                SchoolTV = (TextView) itemView.findViewById(R.id.TVStartNewChatSchoolShowing);
                MainContainerRL = (RelativeLayout) itemView.findViewById(R.id.RLStartNewChatParentLayout);

            }

            void setData(int position) {

                final GeneralUser current = circleUsersList.get(position);

                Picasso.get().load(current.getmProfileUri())
                        .resize(70, 70)
                        .into(ProfileImageIV);


                NameTV.setText(current.getmName());
                SchoolTV.setText(current.getmSchool());

                MainContainerRL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), GeneralUserChatWithGeneralUser.class);
                        intent.putExtra("REGULAR", true);
                        intent.putExtra("USER_ID", current.getmUid());
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(intent);
                    }
                });
            }
        }

    }

}
