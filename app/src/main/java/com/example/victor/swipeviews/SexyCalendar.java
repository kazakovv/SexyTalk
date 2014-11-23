package com.example.victor.swipeviews;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SexyCalendar extends Activity {

    public static final int MONTHS_TO_DISPLAY_IN_CALENDAR =2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sexy_calendar);
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.MONTH, MONTHS_TO_DISPLAY_IN_CALENDAR);

        Calendar today2 = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        today2.add(Calendar.DATE, 3);
        dates.add(today2.getTime());
        today2.add(Calendar.DATE, 5);
        dates.add(today2.getTime());



        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.sexyCalendar);
        Date today = new Date();
        calendar.init(today, nextMonth.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(dates);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sexy_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
