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

public class ExamMarkSheetGPA extends AppCompatActivity {
    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";
    private String marks_sheet, CGPA, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_mark_sheet_gp);

        Bundle extras = getIntent().getExtras();
        CGPA = extras.getString("CGPA");
        marks_sheet = extras.getString("marks_sheet");
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
        String subject, full_marks_subject, pass_marks_subject, obtained_GPA_subject;
        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);

        if (marks_sheet.length() != 0) {
            String[] parts = marks_sheet.split("#");

            for (int i = 0; i < parts.length; i++) {


                TableRow row = new TableRow(this);
                String[] details = parts[i].split(":");
                subject = details[0];
                obtained_GPA_subject = details[3];

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

                dp = getResources().getDimensionPixelSize(R.dimen.subject_GPA);
                TextView textview2 = new TextView(this);
                textview2.setWidth(dp);
                textview2.setTextColor(Color.BLACK);
                textview2.setGravity(Gravity.CENTER);
                textview2.setText(subject);

                dp = getResources().getDimensionPixelSize(R.dimen.gpa);
                TextView textview3 = new TextView(this);
                textview3.setWidth(dp);
                textview3.setTextColor(Color.BLACK);
                textview3.setGravity(Gravity.CENTER);
                textview3.setText(obtained_GPA_subject);

                row.addView(textview1);
                row.addView(textview2);
                row.addView(textview3);

                tabLayout.addView(row, i);
            }

            TextView tv_gpa = (TextView) findViewById(R.id.GPA_value);
            TextView tv_comments = (TextView) findViewById(R.id.comment_value);
            tv_gpa.setText(CGPA);
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
