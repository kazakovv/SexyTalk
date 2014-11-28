package com.example.victor.swipeviews;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



/**
 * Created by Victor on 13/10/2014.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentDays();
            case 1:
                return new FragmentChat();
            default:
                break;
        }

        return null;
    }


    @Override
    public int getItemPosition(Object object) {
        //return POSITION_NONE; //tova oznachava da updatene fragmentite.

        //ako zapazim standartia statement dolu, ne se updatevat fragmentite

        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
