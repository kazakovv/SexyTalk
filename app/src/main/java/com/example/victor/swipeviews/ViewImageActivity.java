package com.example.victor.swipeviews;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.victor.swipeviews.R.id.imageView_to_display_picture;


public class ViewImageActivity extends Activity {
    TextView loveMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        loveMessage = (TextView) findViewById(R.id.loveMessage);
        ImageView imageView = (ImageView) findViewById(imageView_to_display_picture);
        String loveMessageToDisplay = getIntent().getStringExtra(ParseConstants.KEY_LOVE_MESSAGE);
        loveMessage.setText(loveMessageToDisplay);
        if (getIntent().getData() != null) {
            Uri imageUri = getIntent().getData(); //vzima Uri deto go podadohme ot drugata strana
            //Picasso e vanshta bibilioteka, koito ni pozvoliava da otvariame snimki ot internet
            Picasso.with(this).load(imageUri).into(imageView);
        }
    }





}



