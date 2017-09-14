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

import java.util.ArrayList;

import Database.DBReceivedCachedImages;
import Database.DBReceiverForAttendance;
import utility.Utility;

public class AttendenceRecord extends AppCompatActivity {
    private static final String PREF_NAME = "LOGIN_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_record);

        SharedPreferences settings1 = getSharedPreferences(PREF_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLogged = settings1.getBoolean("hasLoggedIn", false);

        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);
        //setting up toolbar
        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.attendence_record);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBReceiverForAttendance received = new DBReceiverForAttendance(getApplicationContext());

        int count = 0;

        ArrayList<String> date = new ArrayList<String>();
        ArrayList<String> attendence_status = new ArrayList<String>();

        //receiving the data from db
        String s = received.getData(1, 1);

        String[] split_entries = s.split("#");

        for (int i = 0; i < split_entries.length; i++) {
            String[] att_data = split_entries[i].split("\\*");
            date.add(att_data[0]);

            if (att_data[1].equals("0")) {
                attendence_status.add("Absent");
            } else {
                attendence_status.add("Present");
            }
            count++;
        }
        for (int i = 0; i < count; i++) {
            TableRow row = new TableRow(this);

            //set the color only for the fields in odd places
            if (i % 2 != 0) {
                row.setBackgroundColor(getResources().getColor(R.color.viewSplit));
            }
            row.setGravity(Gravity.CENTER);

//settting height of each row
            int tt = getResources().getDimensionPixelSize(R.dimen.height);
            row.setMinimumHeight(tt);
            // part1
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            //adding data items in textviews
            int dp = getResources().getDimensionPixelSize(R.dimen.date_of_attendance);
            TextView textview1 = new TextView(this);
            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            textview1.setGravity(Gravity.CENTER);
            textview1.setText(date.get(i));


            TextView textview2 = new TextView(this);
            //setting green color if present red if abscent
            if (attendence_status.get(i).equals("Present")) {
                textview2.setTextColor(Color.GREEN);
            } else {
                textview2.setTextColor(Color.RED);
            }
            dp = getResources().getDimensionPixelSize(R.dimen.att_status);
            textview2.setWidth(dp);
            textview2.setGravity(Gravity.CENTER);
            textview2.setText(attendence_status.get(i));


            row.addView(textview1);
            row.addView(textview2);


            tabLayout.addView(row, i);
        }

        boolean isAvailable = Utility.isNetworkAvailable(getApplicationContext());
        if (isAvailable) {
            DBReceivedCachedImages ad = new DBReceivedCachedImages(getApplicationContext());
            String link = ad.getData("ad");
            ImageView img = (ImageView) findViewById(R.id.imageView);
            Picasso.with(getApplicationContext())
                    .load(link).fit()
                    .into(img);

            final String redirect = ad.getData("redirect");

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
