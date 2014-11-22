package com.example.victor.swipeviews;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class SendTo extends ListActivity {
    public static final String TAG = SendTo.class.getSimpleName();


    protected List<ParseUser> mPartners;
    protected ArrayList<Integer> mSendTo; //izpolzva se samo kato broiach v onListItemClick
    protected ArrayList<String> mRecepientIDs;
    protected ArrayList<String> mRecepientUserNames;

    protected ParseRelation<ParseUser> mPartnersRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_send_to);


        //trasi koi e zadaden kato partnior i go izkarva v spisak
        findPartners();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //inicializirame arraylists, za da mozem da dobaviame info kam tiah
        mSendTo = new ArrayList<Integer>();
        mRecepientIDs = new ArrayList<String>();
        mRecepientUserNames = new ArrayList<String>();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_to, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(l.isItemChecked(position)) {
            mSendTo.add(position);
            mRecepientIDs.add(mPartners.get(position).getObjectId());
            mRecepientUserNames.add(mPartners.get(position).getUsername());
        } else {
            int positionToRemove = mSendTo.indexOf(position);
            mSendTo.remove(positionToRemove);
            mRecepientIDs.remove(positionToRemove);
            mRecepientUserNames.remove(positionToRemove);
            //mSendTo.remove(new Integer(position));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()) {
        case R.id.action_ok:
            Intent intent = new Intent(SendTo.this, SendMessage.class);

            intent.putStringArrayListExtra(ParseConstants.KEY_USERNAME,mRecepientUserNames);
            intent.putStringArrayListExtra(ParseConstants.KEY_RECEPIENT_IDS,mRecepientIDs);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        case R.id.action_settings:
            Intent intentSendMessage = new Intent(this, EditFriendsActivity.class);
            startActivity(intentSendMessage);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void findPartners() {
        mCurrentUser = ParseUser.getCurrentUser();
        mPartnersRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDSRELATION);

        setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseUser> query =  mPartnersRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
            setProgressBarIndeterminateVisibility(false);

                if(e == null) {
               //Partners found
                mPartners = parseUsers;
                String[] usernames = new String[mPartners.size()];
                int i = 0;
                //sazdava masiv ot usernames
                for (ParseUser user : mPartners) {
                    usernames[i] = user.getUsername();
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        SendTo.this,
                        android.R.layout.simple_list_item_checked,
                        usernames
                );
                setListAdapter(adapter);

            } else {
            //failure
                Log.e(TAG, e.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(SendTo.this);
                builder.setTitle(R.string.error_title)
                        .setMessage(e.getMessage())
                        .setPositiveButton(R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            }
        });
    }




}
