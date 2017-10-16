package com.hamroschool.activitypages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import Ads.GetTotalEntriesInDB;
import Ads.SelectWhichAdTOShow;
import Ads.ShowAds;
import Database.DBReceiverForMessages;
import Database.DBReceiverForNotices;
import service.AdChangeCheckerService;
import service.PollService;


public class MessagesList extends AppCompatActivity {
    private Context mContext;
    private int m_clicked_positon;

    private static final String PREF_NAME = "LOGIN_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.messgae_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), ComposeMessage.class);
                Bundle extras = new Bundle();
                extras.putString("teacher_name", "Not Listed");


                intent.putExtras(extras);
                startActivityForResult(intent,1);
            }
        });


        checkIfLoggedIn();

        showMessageList();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                //cereates certain  delay and restarts the activity as message has been registered
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //this piece of code is run after  5ms
                        finish();

                        startActivity(getIntent());
                    }
                }, 5);


            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }


        }
    }//onActivityResult

    private void showMessageList() {
        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);

        DBReceiverForMessages received = new DBReceiverForMessages(getApplicationContext());
        int count = received.getNoOfData();


        for (int i = 0; i < count; i++) {
            TableRow row = new TableRow(this);
            row.setId(1000 + i);
            row.setOnClickListener(mlistner);
            //set the color only for the fields in odd places
            if (i % 2 != 0) {
                row.setBackgroundColor(getResources().getColor(R.color.viewSplit));
            }
            row.setGravity(Gravity.CENTER);


            int tt = getResources().getDimensionPixelSize(R.dimen.height);
            row.setMinimumHeight(tt);
            // part1
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);


            int dp = getResources().getDimensionPixelSize(R.dimen.techer_name);

            TextView textview1 = new TextView(this);
            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            textview1.setGravity(Gravity.CENTER);
            textview1.setText(received.getData(i + 1, 1));


            TextView textview2 = new TextView(this);
            textview2.setTextColor(Color.GREEN);
            dp = getResources().getDimensionPixelSize(R.dimen.title);
            textview2.setWidth(dp);
            textview2.setPaintFlags(textview2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textview2.setGravity(Gravity.CENTER);
            textview2.setText(R.string.view);


            row.addView(textview1);
            row.addView(textview2);


            tabLayout.addView(row, i);
        }
    }


    View.OnClickListener mlistner = new View.OnClickListener() {

        public void onClick(View v) {
            m_clicked_positon = v.getId() - 1000;

            DBReceiverForMessages received = new DBReceiverForMessages(getApplicationContext());
            String name = received.getData(m_clicked_positon + 1, 1);
            String msg_list = received.getData(m_clicked_positon + 1, 2);


            Intent intent = new Intent(getApplicationContext(), MessgesHistory.class);
            Bundle extras = new Bundle();
            extras.putString("teacher_name", name);
            extras.putString("message_list", msg_list);

            intent.putExtras(extras);
            startActivityForResult(intent,1);

        }
    };


    private void checkIfLoggedIn() {
        SharedPreferences settings1 = getSharedPreferences(PREF_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLogged = settings1.getBoolean("hasLoggedIn", false);
        if (!hasLogged) {
            stopService(new Intent(getApplicationContext(), PollService.class));
            stopService(new Intent(getApplicationContext(), AdChangeCheckerService.class));
            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
            //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
