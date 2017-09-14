package com.hamroschool.activitypages;

import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.net.Uri;
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

import com.squareup.picasso.Picasso;

import Database.DBReceivedCachedImages;
import Database.DBReceiverForFeeRecord;
import service.PollService;
import utility.Utility;

public class FeeRecord extends AppCompatActivity {
    private static final String PREF_NAME = "LOGIN_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_record);
        //setting up toolbar
        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.fee_record);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences settings1 = getSharedPreferences(PREF_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLogged = settings1.getBoolean("hasLoggedIn", false);
        if (!hasLogged) {
            stopService(new Intent(getApplicationContext(), PollService.class));
            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
            //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);

        DBReceiverForFeeRecord received = new DBReceiverForFeeRecord(getApplicationContext());
        int count = received.getNoOfData();


        for (int i = 0; i < count; i++) {
            TableRow row = new TableRow(this);

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


            int dp = getResources().getDimensionPixelSize(R.dimen.classes);

            TextView textview1 = new TextView(this);
            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            textview1.setGravity(Gravity.CENTER);
            String s = received.getData(i + 1, 1);
            textview1.setText(received.getData(i + 1, 1));


            TextView textview2 = new TextView(this);
            textview2.setTextColor(Color.BLACK);
            dp = getResources().getDimensionPixelSize(R.dimen.particulars);
            textview2.setWidth(dp);
            textview2.setGravity(Gravity.CENTER);
            s = received.getData(i + 1, 3);
            textview2.setText(received.getData(i + 1, 2));


            int paddrt = getResources().getDimensionPixelSize(R.dimen.padd);
            dp = getResources().getDimensionPixelSize(R.dimen.amount);
            TextView textview3 = new TextView(this);
            textview3.setGravity(Gravity.CENTER);
            textview3.setWidth(dp);
            textview3.setTextColor(Color.BLACK);
            textview3.setPadding(0, 0, paddrt, 0);
            s = received.getData(i + 1, 3);
            textview3.setText(received.getData(i + 1, 3));


            TextView textview4 = new TextView(this);
            dp = getResources().getDimensionPixelSize(R.dimen.date);
            textview4.setGravity(Gravity.CENTER);

            textview4.setWidth(dp);
            textview4.setTextColor(Color.BLACK);
            s = received.getData(i + 1, 4);
            textview4.setText(received.getData(i + 1, 5));

            TextView textview5 = new TextView(this);
            dp = getResources().getDimensionPixelSize(R.dimen.month);
            textview5.setGravity(Gravity.CENTER);

            textview5.setWidth(dp);
            textview5.setTextColor(Color.BLACK);
            s = received.getData(i + 1, 5);
            textview5.setText(received.getData(i + 1, 4));

            row.addView(textview1);
            row.addView(textview2);
            row.addView(textview3);
            row.addView(textview4);
            row.addView(textview5);

            tabLayout.addView(row, i);
        }
        boolean isAvailable = Utility.isNetworkAvailable(getApplicationContext());
        if (isAvailable) {
            DBReceivedCachedImages ad=new DBReceivedCachedImages(getApplicationContext());
            String link= ad.getData("ad");
            ImageView img= (ImageView) findViewById(R.id.imageView);
            Picasso.with(getApplicationContext())
                    .load(link).fit()
                    .into(img);

            final String redirect= ad.getData("redirect");

            img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(redirect));
                    startActivity(intent);
                }
            });

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
