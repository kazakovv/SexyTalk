package com.example.victor.swipeviews;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;


public class LoginActivity extends Activity {
    protected TextView signUpClickButton;

    protected EditText mUserName;
    protected EditText mPassword;
    protected Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); //vzmoznost da pokazva spinner dokato misli
        setContentView(R.layout.activity_login);

        ActionBar actionbar = getActionBar();
        actionbar.hide();//skirvame actionabar che e po-krasivo

        //vrazvame ostanalite TextFields i butona
        mUserName = (EditText) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_username);
        mLoginButton = (Button) findViewById(R.id.logInButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //razkarvam ako ima intervali v username, passpword i email
                String userName = mUserName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(userName.isEmpty() || password.isEmpty() ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(R.string.login_error_title)
                            .setMessage(R.string.login_error_message)
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                else {
                    //ako username i password ne sa prazni se logvame s parse
                    setProgressBarIndeterminateVisibility(true); //pokazva spiner che se sluchva neshto
                    ParseUser.logInInBackground(userName,password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            //setProgressBarIndeterminate(false);//izkluchvame spinnera
                            if (parseUser != null) {
                                //logvame se uspeshno

                                //instalaciata vrazva device sas application. Tova se pravi za da mozhe da se poluchavat push notifications
                                //posle moga da napisha query, koiato da tarsi po parseuser ustroistvoto
                                SendParsePushMessagesAndParseObjects register = new SendParsePushMessagesAndParseObjects();
                                register.registerForPush( ParseUser.getCurrentUser());


                                //User successfully created!.Switch to main screen.
                                Intent intent = new Intent(LoginActivity.this,Main.class);
                                //dobaviame flagove, za da ne moze usera da se varne pak kam toya ekran
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                startActivity(intent);

                            } else {
                                //neuspeshen login

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle(R.string.login_error_title)
                                        .setMessage(R.string.general_login_error_message)
                                        .setPositiveButton(R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }


                    });


                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        signUpClickButton = (TextView) findViewById(R.id.signUpText);
        signUpClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        return true;
    }


}
