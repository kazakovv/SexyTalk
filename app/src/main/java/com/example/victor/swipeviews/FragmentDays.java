package com.example.victor.swipeviews;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentDays extends Fragment {

    private TextView mainMessage;
    private TextView fertileMessage;
    private Button showSexyCalendarButton;
    private Button showPrivateDaysCalendarButton;

    private static final int MENSTRUAL_CALENDAR_DIALOG = 11;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mAverageLengthOfMenstrualCycle;



    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.frament_love_days, container, false);

        //vrazvam poletata, koito shte zapametiavam
        mainMessage = (TextView) inflatedView.findViewById(R.id.mainMessage);
        fertileMessage = (TextView) inflatedView.findViewById(R.id.textViewFertileMessage);
        showSexyCalendarButton = (Button) inflatedView.findViewById(R.id.showSexyCalendarButton);
        showPrivateDaysCalendarButton = (Button) inflatedView.findViewById(R.id.showPrivateDaysDialog);

        //Zarezda mainMessage ot savedSettings. Ako niama nishto zapazeno mu dava prazen text
        SharedPreferences savedSettings = getActivity().getSharedPreferences("MYPREFS",0);
        fertileMessage.setText(savedSettings.getString("FertileMessage", ""));
        mainMessage.setText(savedSettings.getString("MainMessage","Welcome! Enter your settings to start using BabyTalk!"));

        showSexyCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SexyCalendar.class);
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

                    Toast.makeText(getActivity().getApplicationContext(),"The year of the truth is " + mYear +

                            "And the average duration of life is " + mAverageLengthOfMenstrualCycle,Toast.LENGTH_LONG).show();

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // After Cancel code.
                }

            }


    }


    @Override
    public void onStop() {
        super.onStop();
        //Sahraniavam shared preferences kato izlizam ot fragmenta

        SharedPreferences savedSettings = getActivity().getSharedPreferences("MYPREFS",0);
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putString("MainMessage", mainMessage.getText().toString());
        editor.putString("FertileMessage", fertileMessage.getText().toString());

        editor.commit();


    }



}

