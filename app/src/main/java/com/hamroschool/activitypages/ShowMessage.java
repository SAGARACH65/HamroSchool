package com.hamroschool.activitypages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ShowMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);

        Bundle extras = getIntent().getExtras();

        String message = extras.getString("messgae");
        String title = extras.getString("notice_title");


        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(title);
        title_bar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv_msp = (TextView) findViewById(R.id.textView);
        tv_msp.setText(message);
    }
}
