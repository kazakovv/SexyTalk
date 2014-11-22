package com.example.victor.swipeviews;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

/**
 * Created by Victor on 23/10/2014.
 */
public final class ParseConstants {
    //Class name
    public static final String CLASS_MESSAGES = "Messages";

    //Keys

    public static final String KEY_USERNAME = "username";
    public static final String KEY_MALEORFEMALE = "MaleOrFemale";
    public static final String KEY_FRIENDSRELATION = "friendsRelation";
    public static final String KEY_RECEPIENT_IDS = "recepientIDs";
    public static final String KEY_SENDER_ID = "senderID";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_FILE = "file";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_LOVE_MESSAGE = "loveMessage";

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_TEXTMESSAGE = "textMessage";

    public static final String SEX_MALE = "Male";
    public static final String SEX_FEMALE = "Female";



    //Keys prase pusheshes

    public static final String KEY_USER = "user";
    public static final String KEY_ALERT = "alert";
    public static final String KEY_ACTION = "action";
    public static final String KEY_PUSH_MESSAGE = "messageFromSender";

    public static final String TYPE_PUSH_KISS = "kiss";
    public static final String TYPE_PUSH_CALENDAR= "calendar";
    public static final String TYPE_PUSH_MESSAGE="message";



}
