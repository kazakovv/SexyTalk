package com.example.victor.swipeviews;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import java.util.ArrayList;


public class Main extends FragmentActivity implements ActionBar.TabListener {
    ViewPager pager;
    ActionBar actionbar;
    static Context context;
    static MenuItem fertilityCalandarIcon; //izplolzva se za reference v MaleOrFemaleDialog.
    protected ParseUser currentUser;

    protected String MaleOrFemale;
    TextView mainMessage;

    public static final int ACTIVITY_SEND_TO = 11;

    public static final String TAG = Main.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        //vrazvame osnovnotosaobshtenie
        currentUser = ParseUser.getCurrentUser();
        //ako niama lognat potrebitel preprashta kam log-in ekrana

        if (currentUser == null) {
            //prashta ni kam login screen
            navigateToLogin();
        } else {
            // ako ima lognat potrebitel prodalzhava natatak
            Log.i(TAG, "imame lognat potrebitel");

            //proveriavame dali e maz ili zhena
            MaleOrFemale = currentUser.getString(ParseConstants.KEY_MALEORFEMALE);



        }

        pager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter pAdapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pAdapter);
        pager.setOffscreenPageLimit(1);

        actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.addTab(actionbar.newTab().setText(R.string.tab_days_title).setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText(R.string.tab_chat_title).setTabListener(this));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionbar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }


    protected void navigateToLogin() {
        //preprashta kam login screen
        Intent intent = new Intent(this, LoginActivity.class);

        //Celta na sledvashtite 2 reda e da ne moze da otidesh ot log-in ekrana
        //kam osnovnia ekran, ako natisnesh back butona

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //sazdavo zadacha
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //iztriva vsichki predishni zadachi.
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.menu_send_kiss:

                //SendPushMessages sadarza metoda za izprashtane na push
                SendParsePushMessagesAndParseObjects pushM = new SendParsePushMessagesAndParseObjects();
                String message = ParseUser.getCurrentUser().getUsername() + " " +
                        getString(R.string.send_a_kiss_message); //niakoi ti izprati celuvka
                Intent intentSendTo = new Intent(Main.this, ListWithPartners.class);
                startActivityForResult(intentSendTo, ACTIVITY_SEND_TO);
                return true;
            case R.id.menu_send_message:
                Intent intent = new Intent(this, SendMessage.class);
                startActivity(intent);
                return true;

            case R.id.menu_fertility_calendar:
                //MenstrualCalendarDialog newDialog = new MenstrualCalendarDialog();
                //newDialog.show(getFragmentManager(), "Welcome");
                //Log.d("Vic", "Calendar menu");
                return true;

            case R.id.menu_sex:
                DialogFragment sexDialog = new MaleOrFemaleDialog();
                sexDialog.show(getFragmentManager(), "Welcome");
                Log.d("Vic", "Sex menu");
                return true;
            case R.id.menu_logout:
                currentUser.logOut();

                //prashta kam login screen
                navigateToLogin();
                return true;

            case R.id.menu_edit_friends:
                Intent intentSendMessage = new Intent(this, EditFriendsActivity.class);
                startActivity(intentSendMessage);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //tuk se izprashta push message sled cakane za izprashtane na celuvka

        String user = ParseUser.getCurrentUser().getUsername();
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_SEND_TO) {
                ArrayList<String> parseUserNames =
                        data.getStringArrayListExtra(ParseConstants.KEY_USERNAME);
                ArrayList<String> parseObjectIDs =
                        data.getStringArrayListExtra(ParseConstants.KEY_RECEPIENT_IDS);

                SendParsePushMessagesAndParseObjects sendKiss = new SendParsePushMessagesAndParseObjects();
                sendKiss.sendPush(parseObjectIDs, parseUserNames,"not used", ParseConstants.TYPE_PUSH_KISS,
                        user + " " + getString(R.string.send_a_kiss_message), Main.this);
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        //vrazvam osnovnoto saobshtenie( your fertile days are/your partner fertile days are)
        mainMessage = (TextView) findViewById(R.id.mainMessage);
        //zadava dali iconkata na kalendara e enabled ili ne
        fertilityCalandarIcon =  menu.findItem(R.id.menu_fertility_calendar);
        //proveriavame dali current user ne e null.
        if(currentUser != null) {
            if (MaleOrFemale.equals(ParseConstants.SEX_FEMALE)) {
                fertilityCalandarIcon.setVisible(true);
                mainMessage.setText(R.string.main_message_female);

            } else {
                //ako ne e zhena triabva da e maz
                fertilityCalandarIcon.setVisible(false);
                mainMessage.setText(R.string.main_message_male);

            }
        }
        //SharedPreferences savedSettings = getSharedPreferences("MYPREFS",0);
        //fertilityCalandarIcon.setVisible(savedSettings.getBoolean("FertilityCalendar", true));

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }






    @Override
    protected void onStop() {
        super.onStop();

        //Sahraniavam shared preferences kato izlizam ot fragmenta

        SharedPreferences savedSettings = getSharedPreferences("MYPREFS",0);
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putBoolean("FertilityCalendar",fertilityCalandarIcon.isVisible());
        editor.commit();

    }
}
