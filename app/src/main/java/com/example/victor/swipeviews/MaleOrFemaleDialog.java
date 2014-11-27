package com.example.victor.swipeviews;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


/**
 * Created by Victor on 19/10/2014.
 */
public class MaleOrFemaleDialog extends DialogFragment {
    TextView mainMessage;
    //View fertilityCalandarIcon;
    ParseUser parseUser;

    Context context;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //vrazvam osnovnoto sabshtenie i ikonata za kalendara
        parseUser = ParseUser.getCurrentUser();
        mainMessage = (TextView) getActivity().findViewById(R.id.mainMessage);
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_menu_title)
                .setItems(R.array.guy_or_girl_option, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                switch(which) {
                    case 0:
                        mainMessage.setText(R.string.main_message_male);

                          parseUser.put(ParseConstants.KEY_MALEORFEMALE, ParseConstants.SEX_MALE);
                            parseUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                    //sucess
                                    //
                                    Toast.makeText(context,R.string.messaged_successfully_saved_to_server,Toast.LENGTH_LONG).show();
                                    } else {
                                    //error
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle(R.string.login_error_title)
                                                .setMessage(R.string.messaged_unsuccessfully_saved_to_server)
                                                .setPositiveButton(R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                        break;
                    case 1:
                        mainMessage.setText(R.string.main_message_female);
                        parseUser.put(ParseConstants.KEY_MALEORFEMALE, ParseConstants.SEX_FEMALE);
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //sucess
                                    //
                                    Toast.makeText(context,R.string.messaged_successfully_saved_to_server,Toast.LENGTH_LONG).show();
                                } else {
                                    //error
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(R.string.login_error_title)
                                            .setMessage(R.string.messaged_unsuccessfully_saved_to_server)
                                            .setPositiveButton(R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        });
                        break;
                }
            }
        });
        return builder.create();

    }
}
