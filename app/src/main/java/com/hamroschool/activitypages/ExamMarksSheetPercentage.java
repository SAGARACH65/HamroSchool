package com.hamroschool.activitypages;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ExamMarksSheetPercentage extends AppCompatActivity {

    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";
    private String marks_sheet, marks_obtained, full_marks, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //note: herre send intent from the previous activity with the exam type and show in toolbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_marks_sheet_percentage);

        Bundle extras = getIntent().getExtras();
        marks_obtained = extras.getString("marks_obtained");
        marks_sheet = extras.getString("marks_sheet");
        full_marks = extras.getString("full_marks");
        comment = extras.getString("comment");
        //setting up toolbar
        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.marksheet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showResults();
    }

    private void showResults() {
        boolean ispass = true, currentispass = true;
        String subject, full_marks_subject, pass_marks_subject, obtained_marks_subject;
        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);

        if (marks_sheet.length() != 0) {
            String[] parts = marks_sheet.split("#");

            for (int i = 0; i < parts.length; i++) {
                TableRow row = new TableRow(this);
                String[] details = parts[i].split(":");
                subject = details[0];
                full_marks_subject = details[1];
                pass_marks_subject = details[2];
                obtained_marks_subject = details[3];
                //since in xml pass or failure is not given this caslculates if std is pass or fail
                if (ispass) {
                    if (Integer.parseInt(pass_marks_subject) > Integer.parseInt(obtained_marks_subject)) {
                        ispass = false;
                        currentispass = false;
                    }

                }
                row.setGravity(Gravity.CENTER);
                // part1
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                if (i % 2 != 0) {
                    row.setBackgroundColor(getResources().getColor(R.color.viewSplit));
                }
                int dp = getResources().getDimensionPixelSize(R.dimen.sn);
                TextView textview1 = new TextView(this);
                textview1.setWidth(dp);
                textview1.setTextColor(Color.BLACK);
                textview1.setGravity(Gravity.CENTER);
                String count = Integer.toString(i + 1);
                textview1.setText(count);


                dp = getResources().getDimensionPixelSize(R.dimen.subjects);
                TextView textview2 = new TextView(this);
                textview2.setWidth(dp);
                textview2.setTextColor(Color.BLACK);
                textview2.setGravity(Gravity.CENTER);
                textview2.setText(subject);


                dp = getResources().getDimensionPixelSize(R.dimen.full_marks);
                TextView textview3 = new TextView(this);
                textview3.setWidth(dp);
                textview3.setTextColor(Color.BLACK);
                textview3.setGravity(Gravity.CENTER);
                textview3.setText(full_marks_subject);

                dp = getResources().getDimensionPixelSize(R.dimen.pass_marks);
                TextView textview4 = new TextView(this);
                textview4.setWidth(dp);
                textview4.setTextColor(Color.BLACK);
                textview4.setGravity(Gravity.CENTER);
                textview4.setText(pass_marks_subject);

                dp = getResources().getDimensionPixelSize(R.dimen.marks_obtained);
                TextView textview5 = new TextView(this);
                if (!currentispass) {
                    textview5.setTextColor(Color.RED);
                    currentispass = true;
                }
                textview5.setWidth(dp);
                textview5.setGravity(Gravity.CENTER);
                textview5.setText(obtained_marks_subject);
                textview5.setTextColor(Color.BLACK);
                row.addView(textview1);
                row.addView(textview2);
                row.addView(textview3);
                row.addView(textview4);
                row.addView(textview5);


                tabLayout.addView(row, i);
            }
            //this is for the last bar which sows the fullmarks,total, wtc
            int x = parts.length;

            TableRow row = new TableRow(this);
            if (x % 2 != 0) {
                row.setBackgroundColor(getResources().getColor(R.color.viewSplit));
            }

            row.setGravity(Gravity.CENTER);
            // part1
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            int dp = getResources().getDimensionPixelSize(R.dimen.sn);
            TextView textview1 = new TextView(this);
            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            textview1.setText("");


            dp = getResources().getDimensionPixelSize(R.dimen.subjects);
            TextView textview2 = new TextView(this);
            textview2.setWidth(dp);
            textview2.setTextColor(Color.BLACK);
            textview2.setGravity(Gravity.CENTER);
            textview2.setText("Total:");


            dp = getResources().getDimensionPixelSize(R.dimen.full_marks);
            TextView textview3 = new TextView(this);
            textview3.setWidth(dp);
            textview3.setGravity(Gravity.CENTER);
            textview3.setTextColor(Color.BLACK);
            textview3.setText(full_marks);

            dp = getResources().getDimensionPixelSize(R.dimen.pass_marks);
            TextView textview4 = new TextView(this);
            textview4.setWidth(dp);
            textview4.setText("");

            dp = getResources().getDimensionPixelSize(R.dimen.marks_obtained);
            TextView textview5 = new TextView(this);

            textview5.setWidth(dp);
            textview5.setGravity(Gravity.CENTER);
            textview5.setText(marks_obtained);
            textview5.setTextColor(Color.BLACK);
            row.addView(textview1);
            row.addView(textview2);
            row.addView(textview3);
            row.addView(textview4);
            row.addView(textview5);

            tabLayout.addView(row, x);


            double percentage = ((Float.parseFloat(marks_obtained) / Float.parseFloat(full_marks)) * 100);

            DecimalFormat f = new DecimalFormat("##.00");


            TextView tv_per = (TextView) findViewById(R.id.percentage_value);
            TextView tv_result = (TextView) findViewById(R.id.result_value);
            TextView tv_comments = (TextView) findViewById(R.id.comment_value);
            tv_per.setText(f.format(percentage));
            if (ispass) {
                tv_result.setText(R.string.pass);
            } else {
                tv_result.setText(R.string.fail);
            }
            tv_comments.setText(comment);


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
