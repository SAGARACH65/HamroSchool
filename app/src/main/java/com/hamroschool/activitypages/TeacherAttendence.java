package com.hamroschool.activitypages;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import Ads.GetTotalEntriesInDB;
import Ads.SelectWhichAdTOShow;
import Ads.ShowAds;
import Database.DBReceiveTokenAndUserType;
import Database.DBReceiverForProfile;
import Database.DBReceiverTeacherAttendance;
import Database.DataStoreInTokenAndUserType;
import service.AdChangeCheckerService;
import service.PollService;
import service.TeacherAttendanceListService;
import xmlparser.HamroSchoolXmlParser;
import xmlparser.XMLParserForTeachers;


public class TeacherAttendence extends AppCompatActivity {
    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";
    private static final String PREF_NAME = "LOGIN_PREF";
    private String urll = "http://www.hamroschool.net/myschoolapp/loginapi/teacherservice.php?usertoken=";
    private String result;
    private static final String PREF_NAME_FIRST_LOGIN = "FIRST LOGIN";
    private static final String PREF_NAME_HAS_INFO_SYNCED_FIRST_TIME = "HAS_INFO_SSYNCED_FIRST_TIME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendence);


        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);


        SharedPreferences settings = getSharedPreferences(PREF_NAME_FIRST_LOGIN, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean firstlogin = settings.getBoolean("isfirst", false);
        if (firstlogin) {
            TeacherAttendence.ConnectToServer connect = new TeacherAttendence.ConnectToServer();
            connect.execute("sagar");

            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME_FIRST_LOGIN, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isfirst", false);
            editor.apply();

        }


        //starts the services of this class
        startService();

        ///putting the teachers name in the titlebar

        DBReceiverTeacherAttendance receive = new DBReceiverTeacherAttendance(getApplicationContext());
        title_bar.setText(receive.getData("Name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkIfLoggedIn();
        showAds();


        SharedPreferences settings1 = getSharedPreferences(PREF_NAME_HAS_INFO_SYNCED_FIRST_TIME, 0);

        boolean hasSynced = settings1.getBoolean("hasInfoSynced", false);
        if(hasSynced) {
            showAttendanceSheetAndSendToServer();
        }
    }

    private void startService() {
        TeacherAttendanceListService.setServiceAlarm(getApplicationContext(), true);
        AdChangeCheckerService.setServiceAlarm(getApplicationContext(), true);
    }

    private void showAttendanceSheetAndSendToServer() {
        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);
        DBReceiverTeacherAttendance receive = new DBReceiverTeacherAttendance(getApplicationContext());
        String date = receive.getData("Current_date");
        String students_info = receive.getData("students_info");

        //showing todays current date on the textview
        showDate(date);

        //show the attendance ledger and take attendance
        int count = 0;
        //splitting the received data record according to its format
        ArrayList<String> roll_no = new ArrayList<String>();
        ArrayList<String> name_of_student = new ArrayList<String>();

        //example of he received format     25*ananda pudasaini#4*Tinku Raman#12*Laxmi Bai#12*Hiimesh Rimal#
        String[] split_data = students_info.split("#");

        for (int i = 0; i < split_data.length; i++) {
            String[] data = split_data[i].split("\\*");
            roll_no.add(data[0]);
            name_of_student.add(data[1]);
            count++;
        }

        for (int i = 0; i < count; i++) {
            TableRow row = new TableRow(this);
            row.setId(1000 + i);
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
            int dp = getResources().getDimensionPixelSize(R.dimen.student_name);
            TextView textview1 = new TextView(this);
            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            textview1.setGravity(Gravity.CENTER);
            textview1.setText(name_of_student.get(i));

            TextView textview2 = new TextView(this);
            textview2.setTextColor(Color.BLACK);
            dp = getResources().getDimensionPixelSize(R.dimen.roll_no);
            textview2.setWidth(dp);
            textview2.setGravity(Gravity.CENTER);
            textview2.setText(roll_no.get(i));

            //if the student is present color is green
            RadioButton radioButtonPresent = new RadioButton(this);
            radioButtonPresent.setHighlightColor(Color.GREEN);
            dp = getResources().getDimensionPixelSize(R.dimen.present_abscent);
            radioButtonPresent.setWidth(dp);
            //if the student is present color is red
            RadioButton radioButtonAbscent = new RadioButton(this);
            radioButtonAbscent.setHighlightColor(Color.GREEN);
            dp = getResources().getDimensionPixelSize(R.dimen.present_abscent);
            radioButtonAbscent.setWidth(dp);

            row.addView(textview1);
            row.addView(textview2);
            row.addView(radioButtonPresent);
            row.addView(radioButtonAbscent);

            tabLayout.addView(row, i);


        }

    }

    private class ConnectToServer extends AsyncTask<String, String, String> {


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
            urll = urll + token;
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

                        InputStream in;

                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            in = new BufferedInputStream(urlConnection.getInputStream());
                        } else {

                            in = new BufferedInputStream(urlConnection.getErrorStream());
                        }
                        Scanner s = new Scanner(in).useDelimiter("\\A");
                        result = s.hasNext() ? s.next() : "";
                        // received = readStream(in);


                    } finally {

                        urlConnection.disconnect();
                    }
                } else {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            XMLParserForTeachers hp = new XMLParserForTeachers(getApplicationContext());

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

        @Override
        protected void onPostExecute(String s) {
            //setting name of the teacher on top of the toolbar
            TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
            DBReceiverTeacherAttendance receive = new DBReceiverTeacherAttendance(getApplicationContext());
            title_bar.setText(receive.getData("Name"));


            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME_HAS_INFO_SYNCED_FIRST_TIME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hasInfoSynced", true);
            editor.apply();

            //this refreshes the activity by itself
            finish();
            startActivity(getIntent());

        }

    }

    private void showDate(String date) {
        TextView date_textview = (TextView) findViewById(R.id.date_table);
        String text_to_set = "Todays's Date:" + "  " + date;
        date_textview.setText(text_to_set);
    }


    private void checkIfLoggedIn() {
        SharedPreferences settings1 = getSharedPreferences(PREF_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLogged = settings1.getBoolean("hasLoggedIn", false);
        if (!hasLogged) {
            //stop the currently running services
            stopServices();
            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
            //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void stopServices() {
        stopService(new Intent(getApplicationContext(), TeacherAttendanceListService.class));
        stopService(new Intent(getApplicationContext(), PollService.class));
    }

    private void showAds() {
        SharedPreferences settings = getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
        boolean has_ads_synced = settings.getBoolean("hasSynced", false);
        if (has_ads_synced) {
            //showing ads
            GetTotalEntriesInDB total = new GetTotalEntriesInDB();
            int no_of_entries = total.getTotalEntries(getApplicationContext());
            SelectWhichAdTOShow select = new SelectWhichAdTOShow();
            int which_ad = select.select_which_ad(no_of_entries);
            //getting bitmap and redirect link of that ad
            ShowAds adsData = new ShowAds(getApplicationContext());
            try {
                Bitmap image_bitmap_data = adsData.getBitmap(which_ad);
                final String redirect_link = adsData.getRedirectLink(which_ad);

                //show the ad in imageview
                ImageView img = (ImageView) findViewById(R.id.imageView);
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                img.setImageBitmap(image_bitmap_data);

                //redirect link for the ad
                img.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(redirect_link));
                        startActivity(intent);
                    }
                });
            } catch (NullPointerException e) {
                SharedPreferences has_ads_synced1 = getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
                SharedPreferences.Editor editor2 = has_ads_synced1.edit();
                editor2.putBoolean("hasSynced", false);
                editor2.apply();
            }
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        Drawable drawable = menu.findItem(R.id.logout).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.white));
        menu.findItem(R.id.logout).setIcon(drawable);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Do you really want to Logout?")
                        .setIcon(R.drawable.logout)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("hasLoggedIn", false);
                                editor.apply();

                                stopService(new Intent(getApplicationContext(), TeacherAttendanceListService.class));
                                stopService(new Intent(getApplicationContext(), AdChangeCheckerService.class));
                                Intent intent = new Intent(TeacherAttendence.this, LoginChecker.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

                // /need to edit with database later
                return true;

            case android.R.id.home:

                //for back button in the toolbar
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }
}
