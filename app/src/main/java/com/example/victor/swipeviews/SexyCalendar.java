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

    public static final int MONTHS_TO_DISPLAY_IN_CALENDAR = 1;

    Calendar firstDayToHaveSex;
    Calendar lasttDayToHaveSex;
    ArrayList<Date> datesForSex;
    Date firstDateToDisplay;
    Date lastDateToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sexy_calendar);


        //Vzimame parvia i poslednia den, ako sa zadadeni

        Bundle b = getIntent().getExtras();
        if(b.get(Statics.CALENDAR_FIRST_DAY_AFTER_MENSTRUATION) !=null ||
                b.get(Statics.CALENDAR_LAST_DAY_BEFORE_NEXT_CYCLE) != null ) {
            firstDayToHaveSex = (Calendar) b.get(Statics.CALENDAR_FIRST_DAY_AFTER_MENSTRUATION);
            lasttDayToHaveSex = (Calendar) b.get(Statics.CALENDAR_LAST_DAY_BEFORE_NEXT_CYCLE);
            datesForSex = new ArrayList<Date>();
            datesForSex.add(firstDayToHaveSex.getTime());
            datesForSex.add(lasttDayToHaveSex.getTime());


            //zadavame range, koito da se pokazva v kalendara
            Calendar temp = firstDayToHaveSex;
            temp.add(Calendar.MONTH, - MONTHS_TO_DISPLAY_IN_CALENDAR); //izvazhdam zashtoto ima minus
            firstDateToDisplay = temp.getTime();

            temp = lasttDayToHaveSex;
            temp.add(Calendar.MONTH, MONTHS_TO_DISPLAY_IN_CALENDAR);
            lastDateToDisplay = temp.getTime();

        }



        //ako e prazen zadavame dneshtanata data
        if(datesForSex == null) {
            datesForSex = new ArrayList<Date>();
            datesForSex.add(new Date());

            firstDateToDisplay = new Date(); //pokazva dneshnata data
            Calendar nextMonth = Calendar.getInstance();
            nextMonth.add(Calendar.MONTH, MONTHS_TO_DISPLAY_IN_CALENDAR);
            lastDateToDisplay = nextMonth.getTime();
        }



        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.sexyCalendar);
        calendar.init(firstDateToDisplay, lastDateToDisplay)
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(datesForSex);

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
