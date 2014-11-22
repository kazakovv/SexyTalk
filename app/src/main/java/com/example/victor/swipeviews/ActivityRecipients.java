package com.example.victor.swipeviews;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ActivityRecipients extends ListActivity {
    public static final String TAG = ActivityRecipients.class.getSimpleName();

    protected MenuItem mMenuItemSendButton;
    protected Uri mMediaUri;
    protected String mFileType;

    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected List<ParseUser> mFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //vzimame mediaUri adres, koito dobavihme kam Intent kato ia izvikahem
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        Log.d("Vic","The file type is" + mFileType);
    }
    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();

        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDSRELATION);

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query =  mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                setProgressBarIndeterminateVisibility(false);

                mFriends = parseUsers;

                if( e == null ) {
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    //sazdava masiv ot usernames
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            ActivityRecipients.this,
                            android.R.layout.simple_list_item_checked,
                            usernames
                    );
                    setListAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecipients.this);
                    builder.setTitle(R.string.error_title)
                            .setMessage(e.getMessage())
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
            if(l.getCheckedItemCount() > 0) {
                mMenuItemSendButton.setVisible(true);
            } else {
                mMenuItemSendButton.setVisible(false);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_recipients, menu);
        mMenuItemSendButton = menu.findItem(R.id.action_send);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_send:
                ParseObject message = createMessage();
                if(message == null) {
                //error
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.error_sending_file_selected)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                //send message
                    send(message);
                    finish(); //zatvaria taya activity i se vrashtame kam main activity
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            if (e == null) {
            //success
                Toast.makeText(ActivityRecipients.this,R.string.message_successfully_sent,Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecipients.this);
                builder.setMessage(R.string.error_sending_file)
                        .setTitle(R.string.error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
            }
        });
    }
}
