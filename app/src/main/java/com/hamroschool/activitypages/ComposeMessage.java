package com.hamroschool.activitypages;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import Database.DBReceiveTokenAndUserType;
import Database.DataStoreInTokenAndUserType;
import service.AdChangeCheckerService;
import service.PollService;
import utility.EmailOfTeachers;
import utility.NameOfTeachers;
import utility.Utility;
import xmlparser.XMLParserForMessages;

public class ComposeMessage extends AppCompatActivity {

    private static final String PREF_NAME = "LOGIN_PREF";
    private String name_of_teacher;
    private String urll = "http://hamroschool.net/myschoolapp/loginapi/messageupdateservice.php?action=msgupdate&usertoken=";
    private static String message_to = "&msg_to=";
    private static String message = "&msg=";

    private String result;
    private boolean failed = false;

    private String spinner_data_selected, message_written;

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

        setOnClickListenerForSendButton();

        checkifLoggedIn();


    }

    private void checkifLoggedIn() {

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


    private void setOnClickListenerForSendButton() {

        Button myFab = (Button) findViewById(R.id.send);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                boolean isAvailable = Utility.isNetworkAvailable(ComposeMessage.this);
                if (!isAvailable) {
                    Toast.makeText(getApplicationContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();

                } else {

                    Spinner spinner = (Spinner) findViewById(R.id.spinner);
                    EditText et1 = (EditText) findViewById(R.id.editText);

                    spinner_data_selected = spinner.getSelectedItem().toString();
                    message_written = et1.getText().toString();

                  if(!message_written.equals("")) {
                      ComposeMessage.sendDataToServer connect = new ComposeMessage.sendDataToServer();
                      connect.execute("sagar");
                  }else{
                      Toast.makeText(getApplicationContext(), "Please Enter A Message", Toast.LENGTH_SHORT).show();
                  }
                }
            }
        });
    }

    private class sendDataToServer extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            //has access to main thread(i.e UI thread)

        }

        @Override
        protected String doInBackground(String... params) {
            //all code here runs in background thread
            DBReceiveTokenAndUserType rec = new DBReceiveTokenAndUserType(getApplicationContext());

            String token = rec.getTokenAndLoginPersonType(1);

            //replacing white spaces with %20 for web services
            //  spinner_data_selected = spinner_data_selected.replaceAll("\\s+", "%20");

            int entryno = 0;
            //this portion selects the email of the teacher selected

            ArrayList<String> email_array = EmailOfTeachers.getEmailOfTeachers(getApplicationContext());

            ArrayList<String> spinnerArray = NameOfTeachers.getNameOfTeachers(getApplicationContext());

            for (int i = 0; i < spinnerArray.size(); i++) {
                if (spinnerArray.get(i).equals(spinner_data_selected)) {
                    entryno = i;
                    break;
                }
            }

            String email = email_array.get(entryno);

            message_written=message_written.replaceAll("\\s+", "%20");
            urll = urll + token + message_to + email + message + message_written;
            URL url = null;

            try {
                url = new URL(urll);
                //clearing it for next version
                urll = "";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (urlConnection.getResponseCode() == 200) {
                    urlConnection.setConnectTimeout(5 * 1000);

                    try {

                        int statusCode = urlConnection.getResponseCode();

                        InputStream in;

                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            in = new BufferedInputStream(urlConnection.getInputStream());
                        } else {

                            in = new BufferedInputStream(urlConnection.getErrorStream());
                        }
                        Scanner s = new Scanner(in).useDelimiter("\\A");
                        result = s.hasNext() ? s.next() : "";

                    } finally {

                        urlConnection.disconnect();
                    }
                } else {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }

            try {
                XmlPullParser parser = Xml.newPullParser();
                InputStream stream = new ByteArrayInputStream(result.getBytes());


                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(stream, null);
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.TEXT) {
                        String checker = parser.getText();


                        if (checker.equals("The message has been successfully sent.")) {
                            failed = false;


                        } else if (checker.equals("The message could not be sent.")) {
                            failed = true;
                        } else if (checker.equals("Invalid Token")) {
                            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("hasLoggedIn", false);
                            failed = true;
                            editor.apply();
                        }

                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "sa";

        }

        @Override
        protected void onPostExecute(String s) {

            if (failed) {
                Toast.makeText(getApplicationContext(), "Message Couldnot Be Send. Try Again Later", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Message Was Successfully Send", Toast.LENGTH_LONG).show();

                //setting the database again so that notification will not come for the user sent data
                ComposeMessage.ConnectToServerForMessages connect_msg = new ComposeMessage.ConnectToServerForMessages();
                connect_msg.execute("sagar");


                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();


            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private class ConnectToServerForMessages extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            //has access to main thread(i.e UI thread)

        }

        @Override
        protected String doInBackground(String... params) {
            //all code here runs in background thread
            DBReceiveTokenAndUserType rec = new DBReceiveTokenAndUserType(getApplicationContext());
            //1 is for getting token 2 is for getting user type
            String token = rec.getTokenAndLoginPersonType(1);
            String urlll = "http://www.hamroschool.net/myschoolapp/loginapi/messageservice.php?usertoken=";
            urlll = urlll + token;
            URL url = null;
            try {
                url = new URL(urlll);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (urlConnection.getResponseCode() == 200) {
                    urlConnection.setConnectTimeout(5 * 1000);

                    try {

                        int statusCode = urlConnection.getResponseCode();

                        InputStream in;

                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            in = new BufferedInputStream(urlConnection.getInputStream());
                        } else {

                            in = new BufferedInputStream(urlConnection.getErrorStream());
                        }
                        result = readStream(in);


                    } finally {

                        urlConnection.disconnect();
                    }
                } else {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            XMLParserForMessages hp = new XMLParserForMessages(getApplicationContext());

            DataStoreInTokenAndUserType db_store = new DataStoreInTokenAndUserType(getApplicationContext());
            db_store.storeXMLMSG(result, true, false);

            try {
                InputStream stream = new ByteArrayInputStream(result.getBytes());
                hp.parse(stream);

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "sa";
        }

        private String readStream(InputStream in) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            return (result.toString());
        }


        @Override
        protected void onPostExecute(String s) {

        }

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
