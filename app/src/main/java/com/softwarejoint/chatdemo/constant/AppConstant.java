package com.softwarejoint.chatdemo.constant;

import com.softwarejoint.chatdemo.R;


public class AppConstant {

    public static final String[] AVAILABLE_STATUS_MSGS = {
            "Available",
            "Busy",
            "At work",
            "In a meeting",
            "At the gym",
            "At the movies",
            "Sleeping",
            "Battery is dying",
            "Can't talk, msg me",
            "Urgent calls only"
    };

    public static final int MINIMUM_USERNAME_LENGTH = 3;
    public static final int MAXIMUM_USERNAME_LENGTH = 20;
    public static final String USERNAME_PATTERN = "^[a-z0-9_-]{" + MINIMUM_USERNAME_LENGTH + ","
            + MAXIMUM_USERNAME_LENGTH + "}$";
    public static final int MINIMUM_PASSWORD_LENGTH = 6;
    public static final int MAXIMUM_PASSWORD_LENGTH = 16;
    public static final String PASSWORD_PATTERN = "^[A-Za-z0-9_-]{" + MINIMUM_PASSWORD_LENGTH + ","
            + MAXIMUM_PASSWORD_LENGTH + "}$";

    public static final String ALPHABET_INDEXER_KEYS = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // for xmpp notification broadcasts

    public static final String UNREAD_RECEIVER_EVENT = "unread-msg-event";
    public static final String RECEIVER_EVENT = "msg-event";
    public static final String CHAT_EVENT = "chat-event";

    public static final String USERID = "user_id";
    public static final String MSG_TYPE = "msgType";
    public static final String MSG_DATA = "msgData";

    public static final String DEFAULT_STATUS = "New to app";

    public static final int NOT_DELETED = 0;
    public static final int NOT_NEW = 0;
    public static final int IS_NEW = 1;
    public static final int REQUEST_FRESH = 4;
    public static final String DATA_DIR_LOCATION = "/softwarejoint/";
    public static final int MAX_IMAGE_SEND = 5;

    public static final String SERVER_IP = "59.176.81.98";
    public static final String XMPP_DOMAIN = "delhi.softwarejoint.com";
    public static final String XMPP_USER_SUFFIX = "@" + XMPP_DOMAIN;

	public static final String XMPP_MUC_DOMAIN = "muc." + XMPP_DOMAIN;
	public static final String XMPP_MUC_DOMAIN_SUFFIX = "@" + XMPP_MUC_DOMAIN;

    public static final int XMPP_PORT = 5222;
    public static final int MAX_CHAT_VIEWS = 30;


    public static final int ACCOUNT_STATE_PROFILE_SET = 2;

    public static final long LAST_SEEN_HIDE = 0;
    public static final int DEFAULT_TIME_BOMB_DURATION = 0;
    public static final String EXPIRED_MESSAGE_TEXT_EMPTY = "";

    public static final String IS_NOTIFICATION = "IS_NOTIFICATION";

    public static final String TYPING = "Typing...";
	public static final String DEFAULT_USER_RESOURCE = "Smack";
}
