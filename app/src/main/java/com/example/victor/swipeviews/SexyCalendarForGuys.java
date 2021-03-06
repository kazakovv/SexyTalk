package com.example.victor.swipeviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class SexyCalendarForGuys extends Activity {
    protected Spinner listOfPartnersSpinner;
    protected ParseRelation<ParseUser> mPartnersRelation;
    protected ParseUser mCurrentUser;
    protected ArrayList<String> mListOfPartnerUsernames;
    protected ArrayList<String> mObjectIDsOfPartners;

    protected Date mFirstDayOfCycle;
    protected Date mLastDayOfCycle;
    CalendarPickerView calendar;



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
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view_sexy_calendar_for_guys);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today);


        //spinner listener
        listOfPartnersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String senderId = mObjectIDsOfPartners.get(position);
                findSexyCalendarUpdates(senderId);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void findSexyCalendarUpdates(String senderID) {

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.CLASS_CALENDAR_UPDATES);
        query.whereEqualTo(ParseConstants.KEY_RECEPIENT_IDS, ParseUser.getCurrentUser()
                .getObjectId());
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID,senderID); //tarsim samo calendar updates ot opredlen potrebitel
        query.addAscendingOrder(ParseConstants.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                setProgressBarIndeterminateVisibility(false);

                ArrayList<Date> firstDayOfCycle = new ArrayList<Date>();
                ArrayList<Date> lastDayOfCycle = new ArrayList<Date>();
                ArrayList<Date> createdAt = new ArrayList<Date>();
                if (e == null) {
                    //sucessful!
                    //vrashta vsichki calendar updates, ot opredelen potrebitel koito e poluchil user

                  //sazdava masiv first day of cycle, last day of cylce i koga e izprateno saobshtenieto
                    for (ParseObject message : messages) {
                        firstDayOfCycle.add((Date) message.get(ParseConstants.KEY_FIRST_DAY_OF_CYCLE));
                        lastDayOfCycle.add( (Date) message.get(ParseConstants.KEY_LAST_DAY_OF_CYCLE));
                        createdAt.add(message.getCreatedAt());
                    }


                    //namirame poslednoto saobshtenie
                    if(createdAt.size() > 0) {
                       Collections.sort(createdAt); //sortirame po data

                       Date maxDate = createdAt.get(createdAt.size() - 1); //posliednata data e nai-goliamata
                       int positionInArray = createdAt.indexOf(maxDate);
                       mFirstDayOfCycle = firstDayOfCycle.get(positionInArray);
                       mLastDayOfCycle = lastDayOfCycle.get(positionInArray);

                    }
                    //refreshvame calendara
                    refreshCalendarWithSexDaysOfSelectedPartner();

                } else {
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
    public void refreshCalendarWithSexDaysOfSelectedPartner() {

        if(mFirstDayOfCycle != null && mLastDayOfCycle != null) {
            Calendar firstDayToHaveSex = Calendar.getInstance();
            Calendar lastDayToHaveSex = Calendar.getInstance();

            firstDayToHaveSex.setTime(mFirstDayOfCycle);
            firstDayToHaveSex.add(Calendar.DAY_OF_MONTH,Statics.LENGHT_OF_MENSTRUATION);
            lastDayToHaveSex.setTime(mLastDayOfCycle);

            //zadavame range, koito da se pokazva v kalendara
            Calendar temp = Calendar.getInstance();
            temp.setTime(mFirstDayOfCycle);
            temp.add(Calendar.MONTH, - Statics.MONTHS_TO_DISPLAY_IN_CALENDAR); //izvazhdam zashtoto ima minus
            Date firstDateToDisplay = temp.getTime();

            temp.setTime(mLastDayOfCycle);
            temp.add(Calendar.MONTH, Statics.MONTHS_TO_DISPLAY_IN_CALENDAR);
            Date lastDateToDisplay = temp.getTime();

            //inicializiram nanovo calendara
            ArrayList<Date> datesForSex = new ArrayList<Date>();
            datesForSex.add(firstDayToHaveSex.getTime());
            datesForSex.add(lastDayToHaveSex.getTime());

            calendar.init(firstDateToDisplay,lastDateToDisplay)
                    .displayOnly()
                    .inMode(CalendarPickerView.SelectionMode.RANGE)
                    .withSelectedDates(datesForSex);
        }

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
