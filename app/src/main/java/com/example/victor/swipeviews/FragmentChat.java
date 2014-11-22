package com.example.victor.swipeviews;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentChat extends ListFragment {
    public static final String TAG = FragmentChat.class.getSimpleName();

    protected List<ParseObject> mMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.frament_two_layout, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        //tarsim dali imame polucheni saobshtenia
        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECEPIENT_IDS, ParseUser.getCurrentUser()
                .getObjectId());
        query.addAscendingOrder(ParseConstants.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    //sucessful!
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    //sazdava masiv ot usernames
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }

                     //Tova e obiknoven array adapter
/*
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(),
                            android.R.layout.simple_list_item_1,
                            usernames);
*/

                    //Tova e po-gotinia ni ArrayAdaptor s kartinka v zavisimost ot tipa na file

                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(),
                            mMessages);

                    setListAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //otvariame saobshteniata i gi gledame

        ParseObject message = mMessages.get(position);
        String messageType = (String) message.get(ParseConstants.KEY_FILE_TYPE);
        String loveMessage = (String) message.get(ParseConstants.KEY_LOVE_MESSAGE);

        ParseFile file;
        Uri fileUri = null;

        //ako saobshtenieto ne e text, zapisvame reference kam file
        if(!messageType.equals(ParseConstants.TYPE_TEXTMESSAGE) ) {
            file = message.getParseFile(ParseConstants.KEY_FILE);
            fileUri = Uri.parse(file.getUrl());

        }
        if(messageType.equals(ParseConstants.TYPE_IMAGE)) {
            //view image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            intent.putExtra(ParseConstants.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);
        } else if (messageType.equals(ParseConstants.TYPE_TEXTMESSAGE)){
            //ako e text go otvariame v sashtotia view kato image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.putExtra(ParseConstants.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);

        } else  {
        //view video
            Intent intent = new Intent(getActivity(),ViewMovieActivity.class);
            intent.setData(fileUri);
            intent.putExtra(ParseConstants.KEY_LOVE_MESSAGE,loveMessage);
            startActivity(intent);


        }
    }
}
