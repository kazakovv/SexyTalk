package com.example.victor.swipeviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {
    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }




    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDSRELATION);

        setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                setProgressBarIndeterminateVisibility(false);


                if (e == null) {
               //success
                   mUsers = parseUsers;
                   String[] usernames = new String[mUsers.size()];
                   int i = 0;
                   for(ParseUser user : mUsers) {
                       usernames[i] = user.getUsername();
                       i++;
                   }
                   ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                           EditFriendsActivity.this,
                           android.R.layout.simple_list_item_checked,
                           usernames
                           );
                   setListAdapter(adapter);
                   addFriendsCheckMarks();
               }
                else {
                   //failed
                   Log.e(TAG, e.getMessage());
                   AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
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
        //markira kato priatel
        super.onListItemClick(l, v, position, id);


        if(getListView().isItemChecked(position)) {
         mFriendsRelation.add(mUsers.get(position)); //add locally

        } else {  //ako e unchecked go iztrivam
        mFriendsRelation.remove(mUsers.get(position)); //remove locally

        }
        //save changes to Parse.com
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, e.getMessage());
                }
            }
        });
    }

    private void addFriendsCheckMarks() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
           if(e == null)      {
           //success
               for (int i = 0; i< mUsers.size(); i++) {
               ParseUser user = mUsers.get(i);
                   //vtori loop da proveri dali user i e priatel s niakoi
                   for(ParseUser friend : parseUsers) { //sravniava s parseUsers, koito se vrashtat ot metod done
                       if(friend.getObjectId().equals(user.getObjectId())) {
                       getListView().setItemChecked(i, true);
                       }

                   }
               }
           } else {
               Log.i(TAG, e.getMessage());
           }
            }
        });
    }
}
