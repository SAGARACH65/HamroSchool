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


import com.hamroschool.activitypages.MessagesList;
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
import Database.DataStoreInTokenAndUserType;
import utility.Utility;
import xmlparser.CheckIfXMLDifferentMSG;

import xmlparser.XMLParserForMessages;

/**
 * Created by Sagar on 9/23/2017.
 */

public class MessagesService extends IntentService {
    private String urll = "http://www.hamroschool.net/myschoolapp/loginapi/messageservice.php?usertoken=";
    private static final int POLL_INTERVAL = 1000 * 60 * 3;
    private static final String TAG = "MessagesService";
    private static final String PREF_NAME = "LOGIN_PREF";
    private String user__type;
    private boolean donotcontinue = false;

    public MessagesService() {
        // Used to name the worker thread, important only for debugging.
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        DBReceiveTokenAndUserType receive = new DBReceiveTokenAndUserType(getApplicationContext());
        user__type = receive.getTokenAndLoginPersonType(2);

        if (user__type.equals("Parent")) {
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


    private void readFromWeb() throws IOException, XmlPullParserException {
        String returned;
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

    private void checkAndStore(String received) throws IOException, XmlPullParserException {
//        received="<?xml version='1.0' encoding='UTF-8'?><student><profile><details>Student's Name * Ananda Pudasaini # Roll No. * 25 # Class ID * 10-A # Mother's Name * Sunmaya # Father's Name * Bahadur Thapa # Date Of Birth * 2040-07-27 # Address * Hetauda Tole # Contact No. * 9841455777 # Student's Email * Ananda@kcc.com # Blood Group * B+ # Login Email * Sunmaya@gmail.com</details><schoolname>milan vidya mandir</schoolname><studentphoto>http://www.hamroschool.net/myschoolapp/images/anandapudasaini_20170125035237.png</studentphoto></profile><examresult><exam><resulttype>percentage</resulttype><classid>6</classid><schoolid>3</schoolid><result>science:50:30:35#Social Studies:100:40:54#Environment, Population &amp; Health:100:40:58#Nepali:100:40:45#English:100:40:55#Mathematics:100:40:78#Optional Mathematics:100:40:74#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>399</marksobtained><resultdate>2017-01-27</resultdate><examid>Third Mid Terminal Examination</examid><comment></comment></exam><exam><resulttype>percentage</resulttype><classid>6</classid><schoolid>3</schoolid><result>science:50:30:20#Social Studies:100:40:54#Environment, Population &amp; Health:100:40:78#Nepali:100:40:58#English:100:40:65#Mathematics:100:40:74#Optional Mathematics:100:40:58#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>407</marksobtained><resultdate>2017-01-27</resultdate><examid>Second Terminal Examination</examid><comment>Everything is good but need to take action on Science. He is too not good in science.</comment></exam><exam><resulttype>percentage</resulttype><classid>10</classid><schoolid>3</schoolid><result>nepali:100:40:55#English:100:40:70#</result><totalsubject>2</totalsubject><fullmarks>200</fullmarks><marksobtained>125</marksobtained><resultdate>2017-01-27</resultdate><examid>Final Examination</examid><comment></comment></exam><exam><resulttype>gpa</resulttype><classid>6</classid><schoolid>3</schoolid><result>science:50:30:3.1#Social Studies:100:40:3.2#Environment, Population &amp; Health:100:40:2.5#Nepali:100:40:2.4#English:100:40:3.2#Mathematics:100:40:3.9#Optional Mathematics:100:40:3.8#</result><totalsubject>7</totalsubject><resultdate>2017-08-29</resultdate><examid>Third Mid Terminal Examination</examid><comment>very good dear.</comment><cgpa>3.15</cgpa></exam></examresult><feesrecord><fee><classid>6</classid><schoolid>3</schoolid><amount>1200</amount><ondate>2017-01-26</ondate><particulars>tution fee</particulars><month>February</month></fee><fee><classid>6</classid><schoolid>3</schoolid><amount>1200</amount><ondate>2017-01-26</ondate><particulars>tution fee</particulars><month>February</month></fee><fee><classid>6</classid><schoolid>3</schoolid><amount>1200</amount><ondate>2017-01-26</ondate><particulars>tution fee</particulars><month>March</month></fee></feesrecord><attendancerecord><studentattendance>2017-09-16*1#2017-09-15*0#2017-08-26*1#2017-08-10*0#2017-08-08*1#2017-01-29*0#2017-01-28*0#2018-01-27*1#2017-01-26*1#2017-01-25*1#</studentattendance></attendancerecord><noticesrecord><notice><title>Rec notice</title><message>Latest notice.</message><pubdate>2017-09-11</pubdate><expdate>2018-11-12</expdate><noticetype>School General Notice</noticetype></notice><notice><title>School Reopens on 23 September</title><message>This is to notify all the parents to send their children on 23 sept. 2017. thanks</message><pubdate>2017-08-26</pubdate><expdate>2017-09-24</expdate><noticetype>School General Notice</noticetype></notice><notice><title>My notice</title><message>Hello, it is a test notice.</message><pubdate>2017-08-07</pubdate><expdate>2017-10-11</expdate><noticetype>School General Notice</noticetype></notice><notice><title>Test</title><message>This is a test notice.</message><pubdate>2017-08-07</pubdate><expdate>2017-10-11</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>educational tour</title><message>Hello parents, we have arranged educational tour for students.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-05</expdate><noticetype>School General Notice</noticetype></notice><notice><title>parents day </title><message>we welcome you all on parents day to be held on 2017-02-25</message><pubdate>2017-01-29</pubdate><expdate>2017-02-25</expdate><noticetype>School General Notice</noticetype></notice><notice><title>Prepare for 2nd terminal examination</title><message>It is to notify to all the parents/guardians that now its time to prepare your children for 2nd terminal examination.Thank you.</message><pubdate>2017-01-25</pubdate><expdate>2017-03-15</expdate><noticetype>Upcoming Exam</noticetype></notice><notice><title>school reopens from 2017-02-12</title><message>It is to notify to all the parents/guardians that our school reopens from 2017-02-12. So, please send your children to school on the date.</message><pubdate>2017-01-25</pubdate><expdate>2017-02-15</expdate><noticetype>School General Notice</noticetype></notice></noticesrecord><teachersrecord><teacher><teachername>Ramesh Goja</teachername><teachertel>9851021906</teachertel><teacheremail>rameshgoja@gmail.com</teacheremail><subjects>science; Environment, Population &amp; Health; English</subjects></teacher><teacher><teachername>Ram Chandra Khadka</teachername><teachertel>985785458</teachertel><teacheremail>rcg@gmail.com</teacheremail><subjects>Social Studies</subjects></teacher><teacher><teachername>zubair ansari</teachername><teachertel>985475425</teachertel><teacheremail>zubair@gmail.com</teacheremail><subjects>Nepali; Nepali; nepali; EPH</subjects></teacher><teacher><teachername>Govinda</teachername><teachertel>984545454</teachertel><teacheremail>govinda@gmail.com</teacheremail><subjects></subjects></teacher><teacher><teachername>Akshay Kumar</teachername><teachertel>9846546884</teachertel><teacheremail>akshay@kumar.com</teacheremail><subjects>Mathematics</subjects></teacher><teacher><teachername>prachi desai</teachername><teachertel>985454548</teachertel><teacheremail>prachi@gmail.com</teacheremail><subjects>English</subjects></teacher><teacher><teachername>Amrita Arora</teachername><teachertel>97415458755</teachertel><teacheremail>amrita@arora.com</teacheremail><subjects>Optional Mathematics</subjects></teacher><teacher><teachername>ranga seth</teachername><teachertel>4454875454</teachertel><teacheremail>ranga@set.com</teacheremail><subjects></subjects></teacher><teacher><teachername>hfghdg</teachername><teachertel>465465</teachertel><teacheremail>fdhdfg@dgsdfg.com</teacheremail><subjects></subjects></teacher><teacher><teachername>Himal Ojha</teachername><teachertel>9813708383</teachertel><teacheremail>himalojhaok@gmail.com</teacheremail><subjects>life coaching</subjects></teacher></teachersrecord></student>";
        InputStream stream = new ByteArrayInputStream(received.getBytes());
        checkIfTOqkenChanged(stream);
        if (!donotcontinue) {
            CheckIfXMLDifferentMSG check = new CheckIfXMLDifferentMSG(this);
            boolean isdifferent = check.checkIfDifferent(received);

            DataStoreInTokenAndUserType db_store = new DataStoreInTokenAndUserType(getApplicationContext());
            DBReceiveTokenAndUserType db_receive = new DBReceiveTokenAndUserType(getApplicationContext());

            String data_in_database = db_receive.receiveXMLMSG();
            XMLParserForMessages hp = new XMLParserForMessages(getApplicationContext());
            stream = new ByteArrayInputStream(received.getBytes());


            if (isdifferent) {

                // InputStream stream = new ByteArrayInputStream(received.getBytes());

                db_store.storeXMLMSG(received, true, false);
                stream = new ByteArrayInputStream(received.getBytes());
                hp.parse(stream);


                notifyUser();
            }
        }
    }


    private void notifyUser() {
        Resources r = getResources();
        //notification sound
        PendingIntent pi;
        Notification notification;
        NotificationManager notificationManager;
        String notification_string = null;
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //vibration pattern
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};


        String changes_received = "New Message Has Arrived";
        pi = PendingIntent.getActivity(this, 0, new Intent(this, MessagesList.class), 0);
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
            if (count > 25) {
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


    public static void setServiceAlarm(Context context, boolean isOn) {

        Intent i = new Intent(context, MessagesService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);

        } else {
            alarmManager.cancel(pi);
            pi.cancel();
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


}
