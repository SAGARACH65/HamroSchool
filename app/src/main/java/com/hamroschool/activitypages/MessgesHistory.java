package com.hamroschool.activitypages;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import Database.DBReceiverForMessages;

public class MessgesHistory extends AppCompatActivity {
    String message;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messges_history);


        //data for top table
        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.message_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();

        name = extras.getString("teacher_name");
        message = extras.getString("message_list");


        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ComposeMessage.class);
                Bundle extras = new Bundle();
                extras.putString("teacher_name", name);


                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
        });

        showData();

    }

    private void showData() {

        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);

//1 is for teacher 2 is for parent
        ArrayList<String> parent_or_teacher = new ArrayList<String>();
        ArrayList<String> message_sent = new ArrayList<String>();

//decoding the information obtained
        String[] split_data = message.split("#");
        for (int i = 0; i < split_data.length; i++) {

            String[] data = split_data[i].split("\\*");
            parent_or_teacher.add(data[0]);
            message_sent.add(data[1]);
        }

        //now show the data obtained
        for (int i = 0; i < split_data.length; i++) {
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

            int dp = getResources().getDimensionPixelSize(R.dimen.name_width);
            TextView textview1 = new TextView(this);
            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            textview1.setGravity(Gravity.LEFT);

            //Showing different if the message was sent by teracher or the parent itself
            String text_to_show;
            if (parent_or_teacher.get(i).equals("1")) {
                text_to_show = name + ":";
                textview1.setText(text_to_show);
            } else {
                text_to_show = "You" + ":";
                textview1.setText(text_to_show);

            }


            TextView textview2 = new TextView(this);
            dp = getResources().getDimensionPixelSize(R.dimen.max_width);
            textview2.setWidth(dp);
            textview2.setTextColor(Color.BLACK);
            textview2.setGravity(Gravity.LEFT);
            textview2.setText(message_sent.get(i));

            row.addView(textview1);
            row.addView(textview2);

            tabLayout.addView(row, i);
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
