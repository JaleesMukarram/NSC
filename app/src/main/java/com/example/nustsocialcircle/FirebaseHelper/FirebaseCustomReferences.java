package com.example.nustsocialcircle.FirebaseHelper;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FirebaseCustomReferences {

    public static final String SEPERATOR = "/";

    //Reference for account ID of general user
    public static final String GENERAL_USER_ACCOUNT_REFERENCE = "GENERAL_USERS";

    //Reference for pofile Image reference of user
    public static final String GENERAL_USER_PROFILE_STORAGE = "GENERAL_USERS_PROFILE";


    //All modes for updating general users account info
    public static final int UPDATE_GENERAL_USER_NAME = 10;
    public static final int UPDATE_GENERAL_USER_SCHOOL = 20;
    public static final int UPDATE_GENERAL_USER_SECTION = 30;

    public static class GeneralUserCircle {

        public static final String CIRCLE_REFERENCE = "CIRCLE_REFERENCE";

    }

    public static class GeneralUserPOSTs {

        //Image post Database reference
        public static final String GENERAL_USER_IMAGE_POSTS_REFERENCE = "GENERAL_USER_IMAGE_POSTS";

        //Image post Storage reference
        public static final String GENERAL_USER_IMAGE_POSTS_STORAGE = "GENERAL_USER_IMAGE_POSTS";


    }

    public static class GeneralUserFriend {

        //Friend Requests will be sent to this reference
        public static final String GENERAL_USER_FRIEND_REQUESTS_REFERENCE = "GENERAL_USERS_FRIEND_REQUESTS";

        //Accepted friend request will be sent to this reference
        public static final String GENERAL_USER_ACCEPTED_FRIEND_REQUESTS_REFERENCE = "GENERAL_USERS_ACCEPTED_FRIEND_REQUESTS";

        //Rejected friend requests will be sent to this reference
        public static final String GENERAL_USER_REJECTED_FRIEND_REQUESTS_REFERENCE = "GENERAL_USERS_REJECTED_FRIEND_REQUESTS";

        //People later Un-friend will be sent to this refenrece
        public static final String GENERAL_USER_UNFRIEND_REFERENCE = "GENERAL_USERS_UNFRIEND_REFERENCE";

    }

    public static class GeneralUserMessages {

        public static final String GENERAL_USER_CHAT_REFERENCE = "GENERAL_USER_CHATS";
        public static final String GENERAL_USER_CHAT_SEPERATOR = "__";

    }


}
