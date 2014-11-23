package com.example.victor.swipeviews;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentDays extends Fragment {

    private TextView mainMessage;
    private TextView sexyMessage;
    private Button showSexyCalendarButton;
    private Button showPrivateDaysCalendarButton;

    private static final int MENSTRUAL_CALENDAR_DIALOG = 11;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mAverageLengthOfMenstrualCycle;

    Calendar firstDayToHaveSex;
    Calendar lastDayToHaveSex;
    
    protected static int LENGHT_OF_MENSTRUATION = 5;



    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.frament_love_days, container, false);

        //vrazvam poletata, koito shte zapametiavam
        mainMessage = (TextView) inflatedView.findViewById(R.id.mainMessage);
        sexyMessage = (TextView) inflatedView.findViewById(R.id.textViewSexyMessage);
        showSexyCalendarButton = (Button) inflatedView.findViewById(R.id.showSexyCalendarButton);
        showPrivateDaysCalendarButton = (Button) inflatedView.findViewById(R.id.showPrivateDaysDialog);

        //Zarezda mainMessage ot savedSettings. Ako niama nishto zapazeno mu dava prazen text
        SharedPreferences savedSettings = getActivity().getSharedPreferences("MYPREFS",0);
        sexyMessage.setText(savedSettings.getString("FertileMessage", ""));
        mainMessage.setText(savedSettings.getString("MainMessage","Welcome! Enter your settings to start using BabyTalk!"));


        //vazstanoviava saved instance state variables

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mYear = savedInstanceState.getInt(Statics.CALENDAR_YEAR);
            mMonth = savedInstanceState.getInt(Statics.CALENDAR_MONTH);
            mDay = savedInstanceState.getInt(Statics.CALENDAR_DAY);
            mAverageLengthOfMenstrualCycle =
                    savedInstanceState.getInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
            setSexyMessage();
        }


        showSexyCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SexyCalendar.class);
                intent.putExtra(Statics.CALENDAR_FIRST_DAY_AFTER_MENSTRUATION, firstDayToHaveSex);
                intent.putExtra(Statics.CALENDAR_LAST_DAY_BEFORE_NEXT_CYCLE, lastDayToHaveSex);
                startActivity(intent);
            }
        });



        showPrivateDaysCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MenstrualCalendarDialog newDialog = new MenstrualCalendarDialog();
                newDialog.setTargetFragment(FragmentDays.this,MENSTRUAL_CALENDAR_DIALOG);
                newDialog.show(getFragmentManager(),"Welcome");


            }
        });



        return inflatedView;

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == MENSTRUAL_CALENDAR_DIALOG) {


                if (resultCode == Activity.RESULT_OK) {
                    // After Ok code.
                    Bundle bundle =data.getExtras();


                    mYear   =      bundle.getInt(Statics.CALENDAR_YEAR);
                    mMonth  =      bundle.getInt(Statics.CALENDAR_MONTH);
                    mDay    =      bundle.getInt(Statics.CALENDAR_DAY);
                    mAverageLengthOfMenstrualCycle =
                            bundle.getInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);

                    setSexyMessage();

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // After Cancel code.
                }

            }


    }

    protected void setSexyMessage() {
        //pravia si dva kalendara. I dvata sega sochat kam parvia den ot predishnia menstrualen cikal
        firstDayToHaveSex = new GregorianCalendar(mYear,mMonth,mDay);
        lastDayToHaveSex = new GregorianCalendar(mYear,mMonth,mDay);

        //izchisliavane na dnite
        firstDayToHaveSex.add(Calendar.DAY_OF_MONTH, LENGHT_OF_MENSTRUATION);
        lastDayToHaveSex.add(Calendar.DAY_OF_MONTH, mAverageLengthOfMenstrualCycle);

        //sastaviane na message
        String first = firstDayToHaveSex.get(Calendar.DAY_OF_MONTH) + " " +
                new DateFormatSymbols().getMonths()[firstDayToHaveSex.get(Calendar.MONTH)] + " " +
                firstDayToHaveSex.get(Calendar.YEAR);

        String last = lastDayToHaveSex.get(Calendar.DAY_OF_MONTH) + " " +
                new DateFormatSymbols().getMonths()[lastDayToHaveSex.get(Calendar.MONTH)] + " " +
                lastDayToHaveSex.get(Calendar.YEAR);

        String messageToDisplay = "You are ready for sex from " + first  +
                " until " + last ;

        sexyMessage.setText(messageToDisplay);
    }


    @Override
    public void onStop() {
        super.onStop();
        //Sahraniavam shared preferences kato izlizam ot fragmenta

        SharedPreferences savedSettings = getActivity().getSharedPreferences("MYPREFS",0);
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putString("MainMessage", mainMessage.getText().toString());
        editor.putString("FertileMessage", sexyMessage.getText().toString());

        editor.commit();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(Statics.CALENDAR_YEAR, mYear);
        outState.putInt(Statics.CALENDAR_MONTH, mMonth);
        outState.putInt(Statics.CALENDAR_DAY, mDay);
        outState.putInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE, mAverageLengthOfMenstrualCycle);

    }
}


