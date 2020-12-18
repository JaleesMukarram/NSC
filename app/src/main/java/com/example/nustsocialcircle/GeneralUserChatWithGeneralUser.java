package com.example.nustsocialcircle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nustsocialcircle.ChatPackage.Chat;
import com.example.nustsocialcircle.ChatPackage.MessageChat;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseDatabaseObjectUploadTask;
import com.example.nustsocialcircle.HelpingClasses.CustomToast;
import com.example.nustsocialcircle.Interfaces.DatabaseUploadListener;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.example.nustsocialcircle.PostModalClasses.ImagePost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GeneralUserChatWithGeneralUser extends AppCompatActivity {

    private static final String TAG = "GUTOGUChatTAG";

    private ImageView BackButtonIV, ThreeDotsMenuIV, VisitingUserIV, SendButtonIV, ShowSendOptionIV;
    private TextView VisitingUserNameTV;
    private EditText TypedMessageET;
    private RelativeLayout BottomSendContainerRL;

    private RecyclerView AllMessagesRecycler;
    private LinearLayoutManager linearLayoutManager;
    private ChatRecyclerAdapter AdapterForChat;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private AlertDialog SendOptionSelectAD;

    private String ChatNode;

    private GeneralUser VisitingUser;
    private String VisitingUserID;

    private List<Chat> chatList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user_chat_with_general_user);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {

        BackButtonIV = this.findViewById(R.id.IVGeneralUserChatWithGeneralUserBackButton);
        ThreeDotsMenuIV = this.findViewById(R.id.IVGeneralUserChatWithGeneralUserThreeDotMenuOptions);
        VisitingUserIV = this.findViewById(R.id.IVGeneralUserChatWithGeneralUserVisitingUserProfileImageShowing);
        SendButtonIV = this.findViewById(R.id.IVGeneralUserChatWithGeneralUserSendButton);
        VisitingUserNameTV = this.findViewById(R.id.TVGeneralUserChatWithGeneralUserVisitingUserName);
        TypedMessageET = this.findViewById(R.id.ETGeneralUserChatWithGeneralUserTypedMessage);
        AllMessagesRecycler = this.findViewById(R.id.RVGeneralUserChatWithGeneralUserAllMessagesShowing);
        ShowSendOptionIV = this.findViewById(R.id.IVGeneralUserChatWithGeneralUserShowSendOptions);
        BottomSendContainerRL = this.findViewById(R.id.RLGeneralUserChatWithGeneralUserBottomChatSendContainer);


        chatList = new ArrayList<Chat>();

    }

    private void initializeComponents() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent intent = getIntent();


        if (intent.getBooleanExtra("REGULAR", false)) {

            try {

                VisitingUserID = intent.getStringExtra("USER_ID");
                getUserDataForThisUser();

            } catch (Exception ex) {

                ex.printStackTrace();

            }

        } else {

            Log.d(TAG, "");

            //Intent has been through notification
        }


        if (VisitingUserID == null) {

            CustomToast.make_toast_DARK(this, "Error", Gravity.BOTTOM);

        }


        AdapterForChat = new ChatRecyclerAdapter();

        linearLayoutManager = new LinearLayoutManager(this);

        linearLayoutManager.setStackFromEnd(true);

        AllMessagesRecycler.setLayoutManager(linearLayoutManager);
        AllMessagesRecycler.setAdapter(AdapterForChat);

    }

    private void getUserDataForThisUser() {

        Query query = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE)
                .orderByChild("mUid")
                .equalTo(VisitingUserID);

        query.addListenerForSingleValueEvent(new LoadThisUserFromDatabase());

    }

    private void onUserLoadedFromDatabase() {

        int index = 0;

        while (true) {

            if (mUser.getUid().charAt(index) > VisitingUser.getmUid().charAt(index)) {

                ChatNode = mUser.getUid() + FirebaseCustomReferences.GeneralUserMessages.GENERAL_USER_CHAT_SEPERATOR + VisitingUser.getmUid();
                break;

            } else if (mUser.getUid().charAt(index) < VisitingUser.getmUid().charAt(index)) {

                ChatNode = VisitingUser.getmUid() + FirebaseCustomReferences.GeneralUserMessages.GENERAL_USER_CHAT_SEPERATOR + mUser.getUid();
                break;

            } else {

                index++;

            }
        }


        Log.d(TAG, "Chat node is: " + ChatNode);

        initializeListeners();
        listenToChatWithThisUser();

    }

    private void initializeListeners() {

        SendButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewSendingChatFromMe();

            }
        });

        ShowSendOptionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                showSendingOptionDialogue();

            }
        });


    }

    private void addNewSendingChatFromMe() {

        Log.d(TAG, "Sending new chat : please wait");


        final MessageChat chat = new MessageChat(mUser.getUid(), VisitingUser.getmUid(), TypedMessageET.getText().toString().trim());
        TypedMessageET.setText("");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserMessages.GENERAL_USER_CHAT_REFERENCE + "/" + ChatNode);
        FirebaseDatabaseObjectUploadTask task = new FirebaseDatabaseObjectUploadTask(chat, databaseReference, chat.getChatID());

        task.setListener(new DatabaseUploadListener() {
            @Override
            public void listenToProgress(int progress) {

            }

            @Override
            public void onCompleteFailure() {

                CustomToast.make_toast_DARK(GeneralUserChatWithGeneralUser.this, "Failed to sent message", Gravity.BOTTOM);
            }

            @Override
            public void onTaskCompleted() {

            }
        });

    }

    private void listenToChatWithThisUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserMessages.GENERAL_USER_CHAT_REFERENCE + "/" + ChatNode);
        reference.addChildEventListener(new ListenToMessageSentAndReceived());

    }


    private class LoadThisUserFromDatabase implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    GeneralUser user = child.getValue(GeneralUser.class);

                    if (user != null) {

                        VisitingUser = user;
                        onUserLoadedFromDatabase();
                    }

                }

            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    private class ListenToMessageSentAndReceived implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            if (dataSnapshot.exists()) {

                Log.d(TAG, dataSnapshot.toString());

                //Adding for the first time or adding foe the first chat
                if (chatList.size() == 0) {

                    updateListFromThisSnapshot(dataSnapshot, true);

                    //If adding foe the repeating chat message
                } else {

                    updateListFromThisSnapshot(dataSnapshot, false);

                }
            }

            AdapterForChat.notifyDataSetChanged();


        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            Chat chat = dataSnapshot.getValue(Chat.class);

            if (chat != null) {

                deleteFollowingChatFromLocalList(chat.getChatID());
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

        private void addChatToList(DataSnapshot dataSnapshot, boolean firstTime) {


        }


        private void updateListFromThisSnapshot(DataSnapshot child, boolean firstTime) {

            Class cl = MessageChat.class;

            try {

                convertToSpecificClassAndAddToList(child, cl, firstTime);

            } catch (ClassCastException ex) {

                ex.printStackTrace();

            }
        }

        private void convertToSpecificClassAndAddToList(DataSnapshot child, Class c, boolean firstTime) {

            try {

                if (c == MessageChat.class) {

                    MessageChat chat = child.getValue(MessageChat.class);

                    if (firstTime) {

                        chatList.add(chat);

                    } else {

                        boolean exists = false;

                        if (chat != null) {

                            for (Chat alreadyAddedChats : chatList) {

                                if (alreadyAddedChats.getChatID().equals(chat.getChatID())) {
                                    exists = true;
                                    break;
                                }
                            }

                            if (!exists) {

                                chatList.add(chat);
                            }

                        }


                    }

                } else if (c == ImagePost.class) {

                    ImagePost post = child.getValue(ImagePost.class);

                }

            } catch (ClassCastException ex) {

                if (c == MessageChat.class) {

                    convertToSpecificClassAndAddToList(child, ImagePost.class, firstTime);

                } else {

                    throw new ClassCastException();
                }

            }

        }

        private void deleteFollowingChatFromLocalList(String ID) {

            for (int position = 0; position < chatList.size(); position++) {

                if (chatList.get(position).getChatID().equals(ID)) {

                    chatList.remove(position);
                    AdapterForChat.notifyDataSetChanged();
                    break;
                }

            }


        }

    }

    private class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int SENT_MODE = 10;
        private static final int RECEIVED_MODE = 20;

        @Override
        public int getItemViewType(int position) {

            if (chatList.get(position).getSendingUserID().equals(mUser.getUid())) {

                return SENT_MODE;

            } else if (chatList.get(position).getReceivingUserID().equals(mUser.getUid())) {

                return RECEIVED_MODE;

            }
            return SENT_MODE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;

            if (viewType == SENT_MODE) {

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_sent_view, parent, false);
                return new SentMessageChatViewHolder(view);


            } else {

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_received_view, parent, true);
                return new ReceivedMessageChatViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof SentMessageChatViewHolder) {

                SentMessageChatViewHolder viewHolder = (SentMessageChatViewHolder) holder;

                viewHolder.setMessageData(position);


            } else if (holder instanceof ReceivedMessageChatViewHolder) {

                ReceivedMessageChatViewHolder viewHolder = (ReceivedMessageChatViewHolder) holder;

                viewHolder.setMessageData(position);

            }
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }

        private void deleteFollowingChatMessage(String ID) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserMessages.GENERAL_USER_CHAT_REFERENCE + "/" + ChatNode + "/" + ID);
            reference.removeValue();

        }

        private class ReceivedMessageChatViewHolder extends RecyclerView.ViewHolder {

            private ImageView ReceivedFromUserImageIV;
            private TextView ReceivedFromUserMessageTV;


            private ReceivedMessageChatViewHolder(@NonNull View itemView) {
                super(itemView);
                ReceivedFromUserImageIV = itemView.findViewById(R.id.IVChatMessageReceivedViewVisitingUserImage);
                ReceivedFromUserMessageTV = itemView.findViewById(R.id.TVChatMessageReceivedViewReceivedMessage);

            }

            private void setMessageData(int position) {

                if (chatList.get(position) instanceof MessageChat) {

                    MessageChat chat = (MessageChat) chatList.get(position);
                    if (VisitingUser != null) {

                        Picasso.get().load(VisitingUser.getmProfileUri())
                                .resize(70, 70)
                                .into(ReceivedFromUserImageIV);

                    }
                    ReceivedFromUserMessageTV.setText(chat.getMessage());

                }
            }
        }

        private class SentMessageChatViewHolder extends RecyclerView.ViewHolder {

            private ImageView SentFromUserImageIV;
            private TextView SentFromUserMessageTV;


            private SentMessageChatViewHolder(@NonNull View itemView) {
                super(itemView);

                SentFromUserImageIV = itemView.findViewById(R.id.IVChatMessageSentViewVisitingUserImage);
                SentFromUserMessageTV = itemView.findViewById(R.id.TVChatMessageSentViewReceivedMessage);


            }

            private void setMessageData(final int position) {

                if (chatList.get(position) instanceof MessageChat) {

                    final MessageChat chat = (MessageChat) chatList.get(position);
                    SentFromUserMessageTV.setText(chat.getMessage());
                    ;

                    SentFromUserMessageTV.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            deleteFollowingChatMessage(chat.getChatID());
                            return true;
                        }
                    });


                }
            }
        }

    }


}


//    private void showSendingOptionDialogue() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        View view = getLayoutInflater().inflate(R.layout.send_to_chat_options, null);
//
//        ImageView ChoseImage = view.findViewById(R.id.IVSendToChatOptionChoseImage);
//
//
//        builder.setView(view);
//
//        SendOptionSelectAD = builder.create();
//
//
//        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
//
//
////        Log.d(TAG, "Keyboard not using");
//
//        Window window = SendOptionSelectAD.getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.gravity = Gravity.BOTTOM;
//        params.y = BottomSendContainerRL.getHeight();
//        window.setAttributes(params);
//
//        SendOptionSelectAD.show();
//
//
//    }
