package com.example.victor.swipeviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Victor on 17/10/2014.
 */
public class MenstrualCalendarDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {
    DatePicker datePicker;
    Spinner spinnerCycle;
    int averageLengthOfMenstrualCycle;

    protected static int LENGHT_OF_MENSTRUATION = 5;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.set_sex_and_date,null);

        //vrazvam kalendara
        datePicker = (DatePicker) inflatedView.findViewById(R.id.datePicker);

        //vrazvam text message
        final TextView sexyMessage = (TextView) getActivity().findViewById(R.id.textViewFertileMessage);

        //vrazvam spinnerCycle
        spinnerCycle = (Spinner) inflatedView.findViewById(R.id.spinnerMenstrualCycleLength);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.LengthOfCycle, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCycle.setAdapter(adapter);
        spinnerCycle.setSelection(7);
        spinnerCycle.setOnItemSelectedListener(this);

        // Set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String messageToDisplay = calculateFertileDays();
                        sexyMessage.setText(messageToDisplay);

                        //vrashta infoto kam onActivityResult v FragmentDays
                        String averageCycleLength = spinnerCycle.getSelectedItem().toString();

                        Intent i = new Intent();
                        Bundle extras = new Bundle();

                        extras.putInt(Statics.CALENDAR_YEAR,datePicker.getYear());
                        extras.putInt(Statics.CALENDAR_MONTH,datePicker.getMonth());
                        extras.putInt(Statics.CALENDAR_DAY,datePicker.getDayOfMonth());
                        extras.putInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE,
                                Integer.parseInt(averageCycleLength));

                        i.putExtras(extras);
                        getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK,i);
                        dismiss() ;

                        //getTargetFragment().onActivityResult(getTargetRequestCode(),
                        //        Activity.RESULT_OK, getActivity().getIntent());

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MenstrualCalendarDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

    private String calculateFertileDays() {
        //vimam den, msesec, godina ot kalendara
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        //pravia si dva kalendara. I dvata sega sochat kam parvia den ot predishnia menstrualen cikal
        Calendar firstDayToHaveSex = new GregorianCalendar(year,month,day);
        Calendar lastDayToHaveSex = new GregorianCalendar(year,month,day);



        //izchisliavane na dnite
        firstDayToHaveSex.add(Calendar.DAY_OF_MONTH, LENGHT_OF_MENSTRUATION);
        lastDayToHaveSex.add(Calendar.DAY_OF_MONTH, averageLengthOfMenstrualCycle);

        //sastaviane na message
        String first = firstDayToHaveSex.get(Calendar.DAY_OF_MONTH) + " " +
                new DateFormatSymbols().getMonths()[firstDayToHaveSex.get(Calendar.MONTH)] + " " +
                firstDayToHaveSex.get(Calendar.YEAR);

        String last = lastDayToHaveSex.get(Calendar.DAY_OF_MONTH) + " " +
                new DateFormatSymbols().getMonths()[lastDayToHaveSex.get(Calendar.MONTH)] + " " +
                lastDayToHaveSex.get(Calendar.YEAR);
        String messageToDisplay = "You are ready for sex from " + first  +
                " until " + last ;

        return messageToDisplay;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d("Vic", "this is the dialog " + parent.getItemAtPosition(position));
        Log.d("Vic","this is the dialog " + position);

        averageLengthOfMenstrualCycle = Integer.parseInt((String) parent.getItemAtPosition(position));

        Log.d("Vic","conversion succesfull " + averageLengthOfMenstrualCycle);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}