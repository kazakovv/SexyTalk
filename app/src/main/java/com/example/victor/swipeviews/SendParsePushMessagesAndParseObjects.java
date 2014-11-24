package com.example.victor.swipeviews;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sadarzha edin metod za izprashtane na push notifications, koito moze da se izpolzva navsiakade
 * v programata.
 */
public class SendParsePushMessagesAndParseObjects {

    protected void registerForPush(ParseUser currentParseUser) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER, currentParseUser.getObjectId());
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e!=null) {
                    Log.d("Vic", "push error " + e.toString());
                } else {

                    Log.d("Vic", "push registered");
                }
            }
        });
    }

    protected void sendPush( ArrayList<String> recepients, ArrayList<String> usernames,
                             String senderMessageText, String typeOfMessage,
                             String message, Context context) {
        int i = 0;

        for(String recepient: recepients) {

            // Create our Installation query
            ParseQuery pushQuery = ParseInstallation.getQuery();
            pushQuery.whereEqualTo(ParseConstants.KEY_USER,
                    recepient);

            //dobaviame dopalinetelnata info kam JSON object
            JSONObject obj;
            try {
                obj = new JSONObject();

                obj.put(ParseConstants.KEY_PUSH_MESSAGE, message); //tova e osnovnoto saobshtenie

                //zadava tipa push
                if (typeOfMessage.equals(ParseConstants.TYPE_PUSH_KISS)) {
                    obj.put(ParseConstants.KEY_ACTION, ParseConstants.TYPE_PUSH_KISS);

                    //Print a toast message
                    String user = usernames.get(i).toString();
                    Toast.makeText(context, "You sent a kiss to " + user, Toast.LENGTH_LONG).show();
                    i++;
                } else if (typeOfMessage.equals(ParseConstants.TYPE_PUSH_MESSAGE)) {
                    obj.put(ParseConstants.KEY_ACTION, ParseConstants.TYPE_PUSH_MESSAGE);
                    obj.put(ParseConstants.KEY_PUSH_MESSAGE, message);

                } else if (typeOfMessage.equals(ParseConstants.TYPE_PUSH_CALENDAR)) {
                    obj.put(ParseConstants.KEY_ACTION, ParseConstants.TYPE_PUSH_CALENDAR);
                }


                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setData(obj); //dobaviame dopalnitelnite neshta kam saobshtenieto
                push.sendInBackground();


            } catch (Exception e) {
                //there was an error

            }
        }
    }



//Tova se metodite, za izprashtane na saobshtenia, koito se izpolzvat v tutoriala
/*
    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECEPIENT_IDS,getRecepientIDs());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        //razbiva fila na array ot bitove, za da go pratim prez parse.com
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this,mMediaUri);

        if (fileBytes == null ) {
            return null;
        } else {
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this,mMediaUri,mFileType);
            ParseFile file = new ParseFile(fileName,fileBytes);//***** sazdavame ParseFile********
            message.put(ParseConstants.KEY_FILE,file);

            return message;
        }

    }

    protected ArrayList<String> getRecepientIDs() {
        ArrayList<String> recepientIDs = new ArrayList<String>();
        for (int i=0; i < getListView().getCount(); i++) {
            if(getListView().isItemChecked(i)) {
                recepientIDs.add(mFriends.get(i).getObjectId());
            }
        }
        return recepientIDs;
    }
 */

    protected void sendCalendarUpdate(ParseUser currentParseUser,  final ArrayList<String> recepientIDs,
                                      Date firstDayOfCycle, Date lastDateOfCycle,final Context context ) {
        ParseObject calendarUpdate = new ParseObject(ParseConstants.CLASS_CALENDAR_UPDATES);
        calendarUpdate.put(ParseConstants.KEY_SENDER_ID,currentParseUser.getObjectId());
        calendarUpdate.put(ParseConstants.KEY_SENDER_NAME, currentParseUser.getUsername());
        calendarUpdate.put(ParseConstants.KEY_RECEPIENT_IDS,recepientIDs);
        calendarUpdate.put(ParseConstants.KEY_FIRST_DAY_OF_CYCLE, firstDayOfCycle);
        calendarUpdate.put(ParseConstants.KEY_LAST_DAY_OF_CYCLE, lastDateOfCycle);

        calendarUpdate.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //proveriavame kolko pratnira ima, za da izpratim Toast v edinistveno ili mnozhestveno chislo
                    if (recepientIDs.size() > 1) {
                        //ako imash poveche ot 1 partnior
                        Toast.makeText(context, R.string.calendar_update_sent_plural, Toast.LENGTH_LONG).show();
                    } else {
                        //ako imash 1 partnior
                        Toast.makeText(context, R.string.calendar_update_sent_singular, Toast.LENGTH_LONG).show();

                    }
                } else {
                //faillure
                    Log.d("Vic","faulure sending calendar update " + e.toString());
                }
            }
        });
    }
    protected void send(ParseUser currentParseUser, ArrayList<String> recepientIDs,
                        String messageType, String loveMessage,
                        Uri mMediaUri, final Context context) {


        ParseObject messageTosend = new ParseObject(ParseConstants.CLASS_MESSAGES);
        messageTosend.put(ParseConstants.KEY_SENDER_ID,currentParseUser.getObjectId());
        messageTosend.put(ParseConstants.KEY_SENDER_NAME,currentParseUser.getUsername());
        messageTosend.put(ParseConstants.KEY_RECEPIENT_IDS,recepientIDs);
        messageTosend.put(ParseConstants.KEY_LOVE_MESSAGE,loveMessage);
        messageTosend.put(ParseConstants.KEY_FILE_TYPE, messageType);

        if(mMediaUri != null) {
            //razbiva fila na array ot bitove, za da go pratim prez parse.com
            byte[] fileBytes = FileHelper.getByteArrayFromFile(context, mMediaUri);

            if (fileBytes != null) {


                if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                }


                String fileName = FileHelper.getFileName(context, mMediaUri, messageType);
                ParseFile file = new ParseFile(fileName, fileBytes);//***** sazdavame ParseFile********
                messageTosend.put(ParseConstants.KEY_FILE, file);


            }
        }
        messageTosend.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //success
                    Toast.makeText(context,R.string.message_successfully_sent,Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    //builder.setMessage(R.string.error_sending_file)
                    builder.setMessage(e.toString())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }




}

