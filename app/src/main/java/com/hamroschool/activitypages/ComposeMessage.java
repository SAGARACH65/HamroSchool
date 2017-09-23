package com.hamroschool.activitypages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import utility.NameOfTeachers;


public class ComposeMessage extends AppCompatActivity {


    private String name_of_teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);

        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.compose_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();

        name_of_teacher = extras.getString("teacher_name");

        showAndSetSpinnerEntries();

    }

    private void showAndSetSpinnerEntries() {


        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<String> spinnerArray = NameOfTeachers.getNameOfTeachers(getApplicationContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);//selected item will look like a spinner set from XML

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (!name_of_teacher.equals("Not Listed")) {
            int i = adapter.getPosition(name_of_teacher);
            spinner.setSelection(adapter.getPosition(name_of_teacher));
        }
            spinner.setAdapter(adapter);

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
