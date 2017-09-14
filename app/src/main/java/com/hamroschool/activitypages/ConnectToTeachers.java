package com.hamroschool.activitypages;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import Database.DBReceivedCachedImages;
import Database.DBReceiverForConnectToTeachers;
import service.PollService;
import utility.Utility;

public class ConnectToTeachers extends AppCompatActivity {

    private int m_clicked_positon;
    private static final String PREF_NAME = "LOGIN_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_teachers);
        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);

        //setting up toolbar
        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.connect_to_teacher);
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
        DBReceiverForConnectToTeachers received = new DBReceiverForConnectToTeachers(getApplicationContext());
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
            // part1
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            DisplayMetrics displaymetrics = new DisplayMetrics();

            int dp = getResources().getDimensionPixelSize(R.dimen.width);
            String showstring = "";
            String teachers = received.getData(i + 1, 1);
            String[] parts = teachers.split(";");
            for (int q = 0; q < parts.length; q++) {
                if (parts.length > 1 && q != (parts.length - 1)) {
                    showstring = showstring + parts[q] + "\n\n";
                } else {
                    showstring = showstring + parts[q];
                }
            }
            TextView textview1 = new TextView(this);
            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            textview1.setGravity(Gravity.CENTER);
            textview1.setText(showstring);

            showstring = "";
            TextView textview2 = new TextView(this);
            textview2.setWidth(dp);
            int paddrt = getResources().getDimensionPixelSize(R.dimen.padd);
            textview2.setTextColor(Color.BLACK);
            textview2.setGravity(Gravity.CENTER);
            textview2.setPadding(0, 0, paddrt, 0);
            textview2.setText(received.getData(i + 1, 2));
            int x = getResources().getDimensionPixelSize(R.dimen.width1);

            TextView textview3 = new TextView(this);
            textview3.setWidth(x);
            textview3.setTextColor(Color.BLACK);


            textview3.setText(received.getData(i + 1, 3));


            TextView textview4 = new TextView(this);
            textview4.setTextColor(Color.BLACK);
            textview4.setPaintFlags(textview4.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textview4.setWidth(dp);

            textview4.setText(received.getData(i + 1, 4));


            row.addView(textview1);
            row.addView(textview2);
            row.addView(textview3);
            row.addView(textview4);

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

    View.OnClickListener mlistner = new View.OnClickListener() {

        public void onClick(View v) {
            m_clicked_positon = v.getId() - 1000;

            DBReceiverForConnectToTeachers received = new DBReceiverForConnectToTeachers(getApplicationContext());
            String phone_no = received.getData(m_clicked_positon + 1, 4);//as column for phone is 4

            Intent intent = new Intent(Intent.ACTION_DIAL);
            try {
                intent.setData(Uri.parse("tel:" + phone_no));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    };


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
