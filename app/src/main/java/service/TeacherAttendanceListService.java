package service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Database.DBReceiveTokenAndUserType;
import utility.Utility;
import xmlparser.XMLParserForAds;
import xmlparser.XMLParserForTeachers;

/**
 * Created by Sagar on 9/15/2017.
 */

public class TeacherAttendanceListService extends IntentService {

    private boolean donotcontinue = false;
    private static final String PREF_NAME = "LOGIN_PREF";
    private static final String TAG = "TeacherAttendanceListService";
    private String urll = "http://www.hamroschool.net/myschoolapp/loginapi/teacherservice.php?usertoken=";
    private static final int POLL_INTERVAL = 1000 * 60;
    private String user__type;

    public TeacherAttendanceListService() {
        // Used to name the worker thread, important only for debugging.
        super(TAG);
    }

    /*    @Override
      public void onCreate() {
          super.onCreate();

          android.os.Debug.waitForDebugger();  // this line is key
      }
      */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        DBReceiveTokenAndUserType receive = new DBReceiveTokenAndUserType(getApplicationContext());
        user__type = receive.getTokenAndLoginPersonType(2);

        if (user__type.equals("Teacher")) {
            boolean is_Network_Available = Utility.isNetworkAvailable(getApplicationContext());
            if (is_Network_Available) {
                try {
                    try {

                        DBReceiveTokenAndUserType rec = new DBReceiveTokenAndUserType(getApplicationContext());
                        String token = rec.getTokenAndLoginPersonType(1);
                        urll = urll + token;
                        readFromWeb();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void checkIfTOqkenChanged(InputStream in) throws XmlPullParserException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        String checker;
        int eventType = parser.getEventType();
        int count = 0;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            count++;
            if (count > 22) {
                break;
            }
            if (eventType == XmlPullParser.TEXT) {
                checker = parser.getText();
                if (checker.equals("Invalid Access")) {
                    SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("hasLoggedIn", false);
                    editor.apply();
                    donotcontinue = true;
                }
            }
            try {
                eventType = parser.next();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void readFromWeb() throws IOException, XmlPullParserException {
        String returned = null;
        URL url = new URL(urll);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection.getResponseCode() == 200) {

            try {

                int statusCode = urlConnection.getResponseCode();

                InputStream in;

                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } else {
                    in = new BufferedInputStream(urlConnection.getErrorStream());
                }


                returned = readStream(in);

                storeInformation(returned);
            } finally {
                //regardkless of success or faliure we disconnect the connection
                urlConnection.disconnect();
            }
        } else {
            urlConnection.disconnect();
        }

    }

    private void storeInformation(String received) throws IOException, XmlPullParserException {

        InputStream stream = new ByteArrayInputStream(received.getBytes());
        checkIfTOqkenChanged(stream);
        stream = new ByteArrayInputStream(received.getBytes());
        if (!donotcontinue) {

            //setting up xml pullparser
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);

            XMLParserForTeachers xml_parse = new XMLParserForTeachers(getApplicationContext());
            xml_parse.readFeedAndStore(parser);
        }

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

    public static void setServiceAlarm(Context context, boolean isOn) {

        Intent i = new Intent(context, TeacherAttendanceListService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);

        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static void CancelAlarm(Context context) {
        Intent intent = new Intent(context, TeacherAttendanceListService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}
