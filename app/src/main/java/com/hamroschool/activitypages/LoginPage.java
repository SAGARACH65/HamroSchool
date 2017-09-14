package com.hamroschool.activitypages;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import Database.DataStoreInDBConnectTeachers;
import Database.DataStoreInDBExams;
import Database.DataStoreInDBFAttendanceRecord;
import Database.DataStoreInDBFeeRecord;
import Database.DataStoreInDBProfile;
import Database.DataStoreInTokenAndUserType;
import Database.DataStoreInDBrNotices;
import service.PollService;
import utility.Utility;

public class LoginPage extends AppCompatActivity {

    private static final String PREF_NAME_FIRST_LOGIN = "FIRST LOGIN";
    private static final String PREF_NAME = "LOGIN_PREF";
    private static final String URL = //"http://thenetwebs.com/myschoolapp/schoolapp/loginapi/getstudentdetails.php?usertoken=b6d16986f1d5460e";

            "http://www.hamroschool.net/myschoolapp/loginapi/checkuser.php?action=login&lgname=";
    private static final String URL_CONCAT = "&lgpassword=";
    private Context mContext;
    private String result;
    Dialog dialog;
    InputStream in;
    private String bool = "";
    private String user_name, pass_word;
    boolean callintent = true;
    boolean isdone = false, connection_faliure = false;
    private String user_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        stopService(new Intent(getApplicationContext(), PollService.class));

        DataStoreInDBExams dbs = new DataStoreInDBExams(getApplicationContext());
        DataStoreInDBConnectTeachers dbc = new DataStoreInDBConnectTeachers(getApplicationContext());
        DataStoreInDBFeeRecord dbf = new DataStoreInDBFeeRecord(getApplicationContext());
        DataStoreInDBFAttendanceRecord dba = new DataStoreInDBFAttendanceRecord(getApplicationContext());
        DataStoreInDBProfile dbp = new DataStoreInDBProfile(getApplicationContext());
        DataStoreInDBrNotices dbn = new DataStoreInDBrNotices(getApplicationContext());
        DataStoreInTokenAndUserType dunp = new DataStoreInTokenAndUserType(getApplicationContext());
        dbs.storeStudenInfo(" ", " ", " ", " ", " ", " ", " ", true, true);
        dbc.storeTeacherInformation(" ", " ", " ", " ", true, true);
        dbf.storeFeeRecord(" ", " ", " ", " ", " ", true, true);
        dbp.storeStudenInfo(" ", " ", null, "", true, true);
        dbn.storeNoticeRecord(" ", " ", " ", " ", true, true);
        dba.storeAttendanceRecord("", true, true);
        dunp.storeXML("", true, true);


        Button button = (Button) findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  SharedPreferences.Editor editor = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE).edit();
                editor.putString(KEY_CREDENTIALS, "DUMMY CREDENTIALS");
                editor.commit();
*/                  //checking if network is available
                boolean isAvailable = Utility.isNetworkAvailable(LoginPage.this);
                if (!isAvailable) {
                    Toast.makeText(getApplicationContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();

                } else {
                    EditText login_field = (EditText) findViewById(R.id.input_email);
                    EditText password_field = (EditText) findViewById(R.id.input_password);
                    callintent = true;
                    if (isempty(login_field)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();
                        callintent = false;
                    }
                    if (isempty(password_field)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
                        callintent = false;

                    }
                    user_name = login_field.getText().toString();
                    pass_word = password_field.getText().toString();


                    if (callintent) {
                        //   DataStoreInTokenAndUserType store = new DataStoreInTokenAndUserType(getApplicationContext());
                        // store.storeUserNameAndPassword(username, password);

                        String bool = "";
                        LoginPage.ConnectToServer connect = new LoginPage.ConnectToServer();
                        connect.execute("sagar");

                    }
                    // do something
                }
            }
        });

    }

    private boolean isempty(EditText et1) {

        return et1.getText().toString().trim().length() == 0;
    }


    private class ConnectToServer extends AsyncTask<String, String, String> {
        String returnS;
        EditText login_field = (EditText) findViewById(R.id.input_email);
        EditText password_field = (EditText) findViewById(R.id.input_password);
        Button login_button = (Button) findViewById(R.id.btn_login);
        TextInputLayout tvpass = (TextInputLayout) findViewById(R.id.textinputpass);
        TextInputLayout tvun = (TextInputLayout) findViewById(R.id.textinputun);

        @Override
        protected void onPreExecute() {
            //has access to main thread(i.e UI thread)
            login_button.setVisibility(View.INVISIBLE);
            login_field.setVisibility(View.INVISIBLE);
            password_field.setVisibility(View.INVISIBLE);

            //showning dialog box
            dialog = new Dialog(LoginPage.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.progress_dialog_box);
            dialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            //all code here runs in background thread
            String urll = URL
                    + user_name + URL_CONCAT + pass_word;
            URL url = null;
            try {
                url = new URL(urll);
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

                        //InputStream in;

                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            isdone = true;
                            in = new BufferedInputStream(urlConnection.getInputStream());
                        } else {

                            in = new BufferedInputStream(urlConnection.getErrorStream());
                        }

                        Scanner s = new Scanner(in).useDelimiter("\\A");
                        result = s.hasNext() ? s.next() : "";

                        ///i////nput = in;
                        int a = 0;
                        a++;

                    } finally {

                        urlConnection.disconnect();
                        in.close();
                    }
                } else {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                connection_faliure = true;
            }


            if (connection_faliure) {


                return "problem";


            } else if (isdone) {
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    Boolean bool_token = false;
                    InputStream stream = new ByteArrayInputStream(result.getBytes());

                    String token = "";
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(stream, null);
                    bool = "Fail";
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.TEXT) {
                            String checker = parser.getText();
                            if (bool_token) {
                                user_type = checker;
                                bool = "Success";
                                token = parser.getText();

                                parser.next();
                                parser.next();
                                parser.next();
                                token = parser.getText();

                                storeToken(token, user_type);
                            }
                            if (checker.equals("Login Successful")) {
                                bool_token = true;
                            }
                        }
                        eventType = parser.next();
                    }

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (bool.length() != 0) {
                if (bool.equals("Success")) {
                    SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("hasLoggedIn", true);
                    editor.apply();


                    SharedPreferences sharedPreferences1 = getSharedPreferences(PREF_NAME_FIRST_LOGIN, 0);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putBoolean("isfirst", true);
                    editor1.apply();


                    //selects activity to pass intent according if the user is teacher or parent
                    if (user_type.equals("Parent")) {
                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoginPage.this, TeacherAttendence.class);
                        startActivity(intent);
                        finish();
                    }
                } else if (bool.equals("Fail")) {
                    return "fail";

                }

            }

            return "sagar";
        }

        @Override
        protected void onPostExecute(String s) {


            if (s.equals("fail")) {
                Toast.makeText(getApplicationContext(), "Please Enter Correct Username and Password ", Toast.LENGTH_LONG).show();
                login_button.setVisibility(View.VISIBLE);
                login_field.setVisibility(View.VISIBLE);
                password_field.setVisibility(View.VISIBLE);
                dialog.dismiss();
                tvpass.setHintEnabled(true);
                tvun.setHintEnabled(true);
            } else if (s.equals("problem")) {
                Toast.makeText(getApplicationContext(), "There is Problem With Your Internet Connection ", Toast.LENGTH_LONG).show();
                login_button.setVisibility(View.VISIBLE);
                login_field.setVisibility(View.VISIBLE);
                password_field.setVisibility(View.VISIBLE);
                dialog.dismiss();
                tvpass.setHintEnabled(true);
                tvun.setHintEnabled(true);
            }
        }

    }


    private void storeToken(String token, String user_type) {
        DataStoreInTokenAndUserType store = new DataStoreInTokenAndUserType(getApplicationContext());
        store.storeUserNameAndPassword(token, user_type, true);

    }

}
