package com.example.victor.swipeviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SexyCalendarForGuys extends Activity {
    protected Spinner listOfPartnersSpinner;
    protected ParseRelation<ParseUser> mPartnersRelation;
    protected ParseUser mCurrentUser;
    protected ArrayList<String> mListOfPartnerUsernames;
    protected ArrayList<String> mObjectIDsOfPartners;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_sexy_calendar_for_guys);
        listOfPartnersSpinner = (Spinner) findViewById(R.id.spinnerListOfPartners);

        //Inicializirame dvata masiva, za da moze da dobaviame stoinosti kam tiah
        mListOfPartnerUsernames = new ArrayList<String>();
        mObjectIDsOfPartners = new ArrayList<String>();

        //tarsim partniorite i zapalvame spinnera
        findPartners();

        //calendar

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view_sexy_calendar_for_guys);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today);

    }

    public void findPartners() {
        mCurrentUser = ParseUser.getCurrentUser();
        mPartnersRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDSRELATION);

        setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseUser> query = mPartnersRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        setProgressBarIndeterminateVisibility(true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    //Partners found
                    //sazdava masiv ot usernames
                    for (ParseUser user : parseUsers) {
                        mListOfPartnerUsernames.add(user.getUsername());
                        mObjectIDsOfPartners.add(user.getObjectId());

                    }

                    //sled kato imame masiva s key - value parairs zapalvame spinnera s imenata
                    //na parniorite ni
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SexyCalendarForGuys.this,
                            android.R.layout.simple_spinner_item,mListOfPartnerUsernames);
                    listOfPartnersSpinner.setAdapter(adapter);

                } else {
                    //failure
                    Log.e("Vic", e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(SexyCalendarForGuys.this);
                    builder.setTitle(R.string.error_title)
                            .setMessage(R.string.general_error_message)
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sexy_calendar_for_guys, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.choose_partner) {
            Intent intent = new Intent(this, EditFriendsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
