package service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Xml;

import Database.DataStoreInTokenAndUserType;
import utility.Utility;
import xmlparser.CheckIfXmlDifferent;
import xmlparser.DetectFieldsChanged;

import com.hamroschool.activitypages.Exams;
import com.hamroschool.activitypages.FeeRecord;

import xmlparser.HamroSchoolXmlParser;

import com.hamroschool.activitypages.Notices;
import com.hamroschool.activitypages.R;

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

/**
 * Created by Sagar on 6/10/2017.
 * This class poll the internet to check if any content has been changed and provides notification
 */

public class PollService extends IntentService {
    private static final String TAG = "PollService";

    private boolean donotcontinue = false;
    private static final String PREF_NAME = "LOGIN_PREF";

    private String urll = "http://www.hamroschool.net/myschoolapp/loginapi/getstudentdetails.php?usertoken=";
    private static final int POLL_INTERVAL = 1000 * 60;

    public PollService() {
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

    private void readFromWeb() throws IOException, XmlPullParserException {

        String returned = null;
        String parsedxml;
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

                checkAndStore(returned);
            } finally {
                //regardkless of success or faliure we disconnect the connection
                urlConnection.disconnect();
            }
        } else {
            urlConnection.disconnect();
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
            if (count > 20) {
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

    private void checkAndStore(String received) throws IOException, XmlPullParserException {

        InputStream stream = new ByteArrayInputStream(received.getBytes());
        checkIfTOqkenChanged(stream);
        if (!donotcontinue) {
            CheckIfXmlDifferent check = new CheckIfXmlDifferent(this);
            boolean isdifferent = check.checkIfDifferent(received);
            DataStoreInTokenAndUserType db_store = new DataStoreInTokenAndUserType(getApplicationContext());
            DBReceiveTokenAndUserType db_receive = new DBReceiveTokenAndUserType(getApplicationContext());
            String data_in_database = db_receive.receiveXML();
            HamroSchoolXmlParser hp = new HamroSchoolXmlParser(getApplicationContext());
            stream = new ByteArrayInputStream(received.getBytes());
            if (data_in_database == null) {
                db_store.storeXML(received, true, false);
                hp.parse(stream);

            } else if (isdifferent) {


                // InputStream stream = new ByteArrayInputStream(received.getBytes());

                DetectFieldsChanged detect = new DetectFieldsChanged(getApplicationContext());
                InputStream db_stream = new ByteArrayInputStream(data_in_database.getBytes());
                String change_received = null;
                try {
                    change_received = detect.checkWhichDifferent(received, stream, db_stream);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                db_store.storeXML(received, true, false);
                stream = new ByteArrayInputStream(received.getBytes());
                hp.parse(stream);

                //  db_store.storeXML(received);
                notifyUser(change_received);
            }
        }
    }

    private void notifyUser(String changes_received) {
        Resources r = getResources();
        //notification sound
        PendingIntent pi;
        Notification notification;
        NotificationManager notificationManager;
        String notification_string = null;
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //vibration pattern
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        String[] parts = changes_received.split("#");
        for (int i = 0; i < parts.length; i++) {

            switch (parts[i]) {
                case "fee":
                    changes_received = "New Bill Has Arrived";
                    pi = PendingIntent.getActivity(this, 0, new Intent(this, FeeRecord.class), 0);
                    notification = new NotificationCompat.Builder(this)
                            .setTicker(r.getString(R.string.amount))
                            .setSmallIcon(R.drawable.school)
                            .setContentTitle(r.getString(R.string.app_name))
                            .setContentText(changes_received)
                            .setContentIntent(pi)
                            .setVibrate(pattern)
                            .setSound(soundUri)
                            .setAutoCancel(true)
                            .build();

                    notificationManager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notification);
                    break;
                case "exam":
                    changes_received = " Exam Result Of Your Child Has Arrived ";
                    pi = PendingIntent.getActivity(this, 0, new Intent(this, Exams.class), 0);
                    notification = new NotificationCompat.Builder(this)
                            .setTicker(r.getString(R.string.amount))
                            .setSmallIcon(R.drawable.school)
                            .setContentTitle(r.getString(R.string.app_name))
                            .setContentText(changes_received)
                            .setContentIntent(pi)
                            .setVibrate(pattern)
                            .setSound(soundUri)
                            .setAutoCancel(true)
                            .build();

                    notificationManager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notification);
                    break;
                case "notices":
                    changes_received = "New Notice Has Arrived";
                    pi = PendingIntent.getActivity(this, 0, new Intent(this, Notices.class), 0);
                    notification = new NotificationCompat.Builder(this)
                            .setTicker(r.getString(R.string.amount))
                            .setSmallIcon(R.drawable.school)
                            .setContentTitle(r.getString(R.string.app_name))
                            .setContentText(changes_received)
                            .setContentIntent(pi)
                            .setVibrate(pattern)
                            .setSound(soundUri)
                            .setAutoCancel(true)
                            .build();

                    notificationManager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(2, notification);
                    break;
                default:
                    break;
            }
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

        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);

        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        //used to check if pending intent exists or not
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
