package com.example.victor.swipeviews;

import android.app.Application;

import com.parse.Parse;


public class SexyTalkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "q8kuy8UbICvlNOFsPLNCAMTTFyrcLbmC1jNKLddU", "ApdaaWEaZSNdob8maokZyxRHJlQA6BoOfWdh9y3L");



    }
}
