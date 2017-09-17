package com.hamroschool.activitypages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import Database.DBReceiveTokenAndUserType;

/**
 * Created by Sagar on 6/7/2017.
 */

public class LoginChecker extends AppCompatActivity {

    private static final String PREF_NAME = "LOGIN_PREF";
    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = null;
        SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);



        if (hasLoggedIn) {
            //if user is currently logged in;
            //Go directly to main activity.
            DBReceiveTokenAndUserType receive=new DBReceiveTokenAndUserType(getApplicationContext());
           String user_type= receive.getTokenAndLoginPersonType(2);
            if (user_type.equals("Parent")) {
                intent = new Intent(this, MainActivity.class);
            }
            else{
                intent = new Intent(this, TeacherAttendence.class);
            }

        } else {
            intent = new Intent(this, LoginPage.class);
        }
        startActivity(intent);
        finish();


    }
}
