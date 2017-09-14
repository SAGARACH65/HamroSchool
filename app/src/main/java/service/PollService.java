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

    String urll = "http://www.hamroschool.net/myschoolapp/loginapi/getstudentdetails.php?usertoken=";
    private Context mContext;
    private static final int POLL_INTERVAL = 1000 * 60;

    public PollService() {
        // Used to name the worker thread, important only for debugging.
        super(TAG);
    }

    /*@Override
    public void onCreate() {
        super.onCreate();
//whatever else you have to to here...
        android.os.Debug.waitForDebugger();  // this line is key
    }*/
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
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

    private void readFromWeb() throws IOException, XmlPullParserException {

        String returned = null;
        String parsedxml;
        URL url = new URL(urll);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection.getResponseCode() == 200) {


            try {
                //   urlConnection.setDoOutput(true);
                // urlConnection.setChunkedStreamingMode(0);
                //urlConnection.setConnectTimeout(5 * 1000);
                //OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                //writeStream(out,textTosend);


                int statusCode = urlConnection.getResponseCode();

                InputStream in;

                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } else {
                    in = new BufferedInputStream(urlConnection.getErrorStream());
                }


                returned = readStream(in);
                //  HamroSchoolXmlParser hp = new HamroSchoolXmlParser(getApplicationContext());
                //  InputStream stream = new ByteArrayInputStream(returned.getBytes(StandardCharsets.UTF_8));
                //     hp.parse(stream);
             /*   HamroSchoolXmlParser hp = new HamroSchoolXmlParser(getApplicationContext());
                InputStream stream = new ByteArrayInputStream(returned.getBytes(StandardCharsets.UTF_8));
               */
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
            if (count > 40) {
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

        //   received="<?xml version='1.0' encoding='UTF-8'?><student><profile><studentname>ananda pudasaini</studentname><rollno>25</rollno><classid>6</classid><mothername>sunmaya</mothername><fathername>bahadur thapa</fathername><dob>2040-07-27</dob><address>hetauda tole</address><contactnum>9841455777</contactnum><schoolid>3</schoolid><studentemail>ananda@kcc.com</studentemail><bloodgroup>B+</bloodgroup><loginemail>sunmaya@gmail.com</loginemail><attendance>,7-A:2017-01-25:1,7-A:2017-01-27:1,10-A:2017-01-27:0</attendance><lastlogin>0000-00-00 00:00:00</lastlogin><schoolname>milan vidya mandir</schoolname><studentphoto>www.hamroschool.net/myschoolapp/images/anandapudasaini_20170125035237.png</studentphoto></profile><examresult><exam><classid>6</classid><schoolid>3</schoolid><result>science:50:30:45#Social Studies:100:40:78#Environment, Population &amp; Health:100:40:32#Nepali:100:40:55#English:100:40:74#Mathematics:100:40:12#Optional Mathematics:100:40:85#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>513</marksobtained><resultdate>2017-01-27</resultdate><examid>Final Examination</examid><comment></comment></exam><exam><classid>6</classid><schoolid>3</schoolid><result>science:50:30:35#Social Studies:100:40:54#Environment, Population &amp; Health:100:40:58#Nepali:100:40:45#English:100:40:55#Mathematics:100:40:78#Optional Mathematics:100:40:74#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>399</marksobtained><resultdate>2017-01-27</resultdate><examid>Third Mid Terminal Examination</examid><comment></comment></exam><exam><classid>6</classid><schoolid>3</schoolid><result>science:50:30:20#Social Studies:100:40:54#Environment, Population &amp; Health:100:40:78#Nepali:100:40:58#English:100:40:65#Mathematics:100:40:74#Optional Mathematics:100:40:58#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>407</marksobtained><resultdate>2017-01-27</resultdate><examid>Second Terminal Examination</examid><comment>Everything is good but need to take action on Science. He is too not good in science.</comment></exam><exam><classid>6</classid><schoolid>3</schoolid><result>science:50:30:20#Social Studies:100:40:54#Environment, Population &amp; Health:100:40:78#Nepali:100:40:58#English:100:40:65#Mathematics:100:40:74#Optional Mathematics:100:40:58#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>407</marksobtained><resultdate>2017-01-27</resultdate><examid>Second Terminal Examination</examid><comment>Everything is good but need to take action on Science. He is too not good in science.</comment></exam><exam><classid>10</classid><schoolid>3</schoolid><result>nepali:100:40:55#English:100:40:70#</result><totalsubject>2</totalsubject><fullmarks>200</fullmarks><marksobtained>125</marksobtained><resultdate>2017-01-27</resultdate><examid>Final Examination</examid><comment></comment></exam><exam><classid>8</classid><schoolid>3</schoolid><result></result><totalsubject>5</totalsubject><fullmarks>500</fullmarks><marksobtained>300</marksobtained><resultdate>2017-01-01</resultdate><examid>Third Mid Terminal Examination</examid><comment></comment></exam><exam><classid>8</classid><schoolid>3</schoolid><result></result><totalsubject>5</totalsubject><fullmarks>600</fullmarks><marksobtained>300</marksobtained><resultdate>2017-01-12</resultdate><examid>Final Examination</examid><comment></comment></exam></examresult><feesrecord><fee><classid>6</classid><schoolid>3</schoolid><amount>1200</amount><ondate>2017-01-26</ondate><particulars>tution fee</particulars><month>February</month></fee><fee><classid>6</classid><schoolid>3</schoolid><amount>1200</amount><ondate>2017-01-26</ondate><particulars>tution fee</particulars><month>March</month></fee></feesrecord><attendancerecord><studentattendance><classname>7-A</classname><schoolid>3</schoolid><attendance>1</attendance><ondate>2017-01-25</ondate><month>01</month></studentattendance><studentattendance><classname>7-A</classname><schoolid>3</schoolid><attendance>1</attendance><ondate>2017-01-26</ondate><month>01</month></studentattendance><studentattendance><classname>8-A</classname><schoolid>3</schoolid><attendance>1</attendance><ondate>2018-01-27</ondate><month>01</month></studentattendance><studentattendance><classname>7-A</classname><schoolid>3</schoolid><attendance>0</attendance><ondate>2017-01-28</ondate><month>01</month></studentattendance><studentattendance><classname>10-A</classname><schoolid>3</schoolid><attendance>0</attendance><ondate>2017-01-29</ondate><month>01</month></studentattendance></attendancerecord><noticesrecord><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>educational tour</title><message>Hello parents, we have arranged educational tour for students.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-05</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>parents day </title><message>we welcome you all on parents day to be held on 2017-02-25</message><pubdate>2017-01-29</pubdate><expdate>2017-02-25</expdate><noticetype>School General Notice</noticetype></notice><notice><title>Prepare for 2nd terminal examination</title><message>It is to notify to all the parents/guardians that now its time to prepare your children for 2nd terminal examination.Thank you.</message><pubdate>2017-01-25</pubdate><expdate>2017-03-15</expdate><noticetype>Upcoming Exam</noticetype></notice><notice><title>Prepare for 2nd terminal examination</title><message>It is to notify to all the parents/guardians that now its time to prepare your children for 2nd terminal examination.Thank you.</message><pubdate>2017-01-25</pubdate><expdate>2017-03-15</expdate><noticetype>Upcoming Exam</noticetype></notice><notice><title>school reopens from 2017-02-12</title><message>It is to notify to all the parents/guardians that our school reopens from 2017-02-12. So, please send your children to school on the date.</message><pubdate>2017-01-25</pubdate><expdate>2017-02-15</expdate><noticetype>School General Notice</noticetype></notice></noticesrecord><teachersrecord><teacher><teachername>Ramesh Goja</teachername><teachertel>9851021906</teachertel><teacheremail>rameshgoja@gmail.com</teacheremail><subjects>science; Environment, Population &amp; Health; English</subjects></teacher><teacher><teachername>Ram Chandra Khadka</teachername><teachertel>985785458</teachertel><teacheremail>rcg@gmail.com</teacheremail><subjects>Social Studies</subjects></teacher><teacher><teachername>zubair ansari</teachername><teachertel>985475425</teachertel><teacheremail>zubair@gmail.com</teacheremail><subjects>Nepali; Nepali; nepali</subjects></teacher><teacher><teachername>Govinda</teachername><teachertel>984545454</teachertel><teacheremail>govinda@gmail.com</teacheremail><subjects></subjects></teacher><teacher><teachername>Akshay Kumar</teachername><teachertel>9846546884</teachertel><teacheremail>akshay@kumar.com</teacheremail><subjects>Mathematics</subjects></teacher><teacher><teachername>prachi desai</teachername><teachertel>985454548</teachertel><teacheremail>prachi@gmail.com</teacheremail><subjects>English</subjects></teacher><teacher><teachername>Amrita Arora</teachername><teachertel>97415458755</teachertel><teacheremail>amrita@arora.com</teacheremail><subjects>Optional Mathematics</subjects></teacher><teacher><teachername>ranga seth</teachername><teachertel>4454875454</teachertel><teacheremail>ranga@set.com</teacheremail><subjects></subjects></teacher></teachersrecord><ads><ad><imagelink>https://designcontest-com-designcontest.netdna-ssl.com/blog/wp-content/uploads/2015/07/banner-ad-fail-11.jpg</imagelink><adlink>https://designcontest-com-designcontest.netdna-ssl.com/blog/wp-content/uploads/2015/07/banner-ad-fail-11.jpg</adlink></ad></ads></student>";


        //   received="<?xml version='1.0' encoding='UTF-8'?><student><profile><studentname>ananda pudasaini</studentname><rollno>25</rollno><classid>6</classid><mothername>sunmaya</mothername><fathername>bahadur thapa</fathername><dob>2040-07-27</dob><address>hetauda tole</address><contactnum>9841455777</contactnum><schoolid>3</schoolid><studentemail>ananda@kcc.com</studentemail><bloodgroup>B+</bloodgroup><loginemail>sunmaya@gmail.com</loginemail><attendance>,7-A:2017-01-25:1,7-A:2017-01-27:1,10-A:2017-01-27:0</attendance><lastlogin>0000-00-00 00:00:00</lastlogin><schoolname>milan vidya mandir</schoolname><studentphoto>www.hamroschool.net/myschoolapp/images/anandapudasaini_20170125035237.png</studentphoto></profile><examresult><exam><classid>6</classid><schoolid>3</schoolid><result>science:50:30:45#Social Studies:100:40:78#Environment, Population &amp; Health:100:40:32#Nepali:100:40:55#English:100:40:74#Mathematics:100:40:12#Optional Mathematics:100:40:85#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>513</marksobtained><resultdate>2017-01-27</resultdate><examid>Final Examination</examid><comment></comment></exam><exam><classid>6</classid><schoolid>3</schoolid><result>science:50:30:35#Social Studies:100:40:54#Environment, Population &amp; Health:100:40:58#Nepali:100:40:45#English:100:40:55#Mathematics:100:40:78#Optional Mathematics:100:40:74#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>399</marksobtained><resultdate>2017-01-27</resultdate><examid>Third Mid Terminal Examination</examid><comment></comment></exam><exam><classid>6</classid><schoolid>3</schoolid><result>science:50:30:20#Social Studies:100:40:54#Environment, Population &amp; Health:100:40:78#Nepali:100:40:58#English:100:40:65#Mathematics:100:40:74#Optional Mathematics:100:40:58#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>407</marksobtained><resultdate>2017-01-27</resultdate><examid>Second Terminal Examination</examid><comment>Everything is good but need to take action on Science. He is too not good in science.</comment></exam><exam><classid>6</classid><schoolid>3</schoolid><result>science:50:30:20#Social Studies:100:40:54#Environment, Population &amp; Health:100:40:78#Nepali:100:40:58#English:100:40:65#Mathematics:100:40:74#Optional Mathematics:100:40:58#</result><totalsubject>7</totalsubject><fullmarks>650</fullmarks><marksobtained>407</marksobtained><resultdate>2017-01-27</resultdate><examid>Second Terminal Examination</examid><comment>Everything is good but need to take action on Science. He is too not good in science.</comment></exam><exam><classid>10</classid><schoolid>3</schoolid><result>nepali:100:40:55#English:100:40:70#</result><totalsubject>2</totalsubject><fullmarks>200</fullmarks><marksobtained>125</marksobtained><resultdate>2017-01-27</resultdate><examid>Final Examination</examid><comment></comment></exam><exam><classid>8</classid><schoolid>3</schoolid><result></result><totalsubject>5</totalsubject><fullmarks>500</fullmarks><marksobtained>300</marksobtained><resultdate>2017-01-01</resultdate><examid>Third Mid Terminal Examination</examid><comment></comment></exam><exam><classid>8</classid><schoolid>3</schoolid><result></result><totalsubject>5</totalsubject><fullmarks>600</fullmarks><marksobtained>300</marksobtained><resultdate>2017-01-12</resultdate><examid>Final Examination</examid><comment></comment></exam></examresult><feesrecord><fee><classid>6</classid><schoolid>3</schoolid><amount>1200</amount><ondate>2017-01-26</ondate><particulars>tution fee</particulars><month>February</month></fee><fee><classid>6</classid><schoolid>3</schoolid><amount>1200</amount><ondate>2017-01-26</ondate><particulars>tution fee</particulars><month>March</month></fee><fee><classid>6</classid><schoolid>3</schoolid><amount>1200</amount><ondate>2017-01-26</ondate><particulars>tution fee</particulars><month>March</month></fee></feesrecord><attendancerecord><studentattendance><classname>7-A</classname><schoolid>3</schoolid><attendance>1</attendance><ondate>2017-01-25</ondate><month>01</month></studentattendance><studentattendance><classname>7-A</classname><schoolid>3</schoolid><attendance>1</attendance><ondate>2017-01-26</ondate><month>01</month></studentattendance><studentattendance><classname>8-A</classname><schoolid>3</schoolid><attendance>1</attendance><ondate>2018-01-27</ondate><month>01</month></studentattendance><studentattendance><classname>7-A</classname><schoolid>3</schoolid><attendance>0</attendance><ondate>2017-01-28</ondate><month>01</month></studentattendance><studentattendance><classname>10-A</classname><schoolid>3</schoolid><attendance>0</attendance><ondate>2017-01-29</ondate><month>01</month></studentattendance></attendancerecord><noticesrecord><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>educational tour</title><message>Hello parents, we have arranged educational tour for students.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-05</expdate><noticetype>School General Notice</noticetype></notice><notice><title>museum visit</title><message>All parents are requested to send their children to school for museum visit on coming saturday.Thank you.</message><pubdate>2017-08-06</pubdate><expdate>2017-10-09</expdate><noticetype>School General Notice</noticetype></notice><notice><title>parents day </title><message>we welcome you all on parents day to be held on 2017-02-25</message><pubdate>2017-01-29</pubdate><expdate>2017-02-25</expdate><noticetype>School General Notice</noticetype></notice><notice><title>Prepare for 2nd terminal examination</title><message>It is to notify to all the parents/guardians that now its time to prepare your children for 2nd terminal examination.Thank you.</message><pubdate>2017-01-25</pubdate><expdate>2017-03-15</expdate><noticetype>Upcoming Exam</noticetype></notice><notice><title>Prepare for 2nd terminal examination</title><message>It is to notify to all the parents/guardians that now its time to prepare your children for 2nd terminal examination.Thank you.</message><pubdate>2017-01-25</pubdate><expdate>2017-03-15</expdate><noticetype>Upcoming Exam</noticetype></notice><notice><title>school reopens from 2017-02-12</title><message>It is to notify to all the parents/guardians that our school reopens from 2017-02-12. So, please send your children to school on the date.</message><pubdate>2017-01-25</pubdate><expdate>2017-02-15</expdate><noticetype>School General Notice</noticetype></notice></noticesrecord><teachersrecord><teacher><teachername>Ramesh Goja</teachername><teachertel>9851021906</teachertel><teacheremail>rameshgoja@gmail.com</teacheremail><subjects>science; Environment, Population &amp; Health; English</subjects></teacher><teacher><teachername>Ram Chandra Khadka</teachername><teachertel>985785458</teachertel><teacheremail>rcg@gmail.com</teacheremail><subjects>Social Studies</subjects></teacher><teacher><teachername>zubair ansari</teachername><teachertel>985475425</teachertel><teacheremail>zubair@gmail.com</teacheremail><subjects>Nepali; Nepali; nepali</subjects></teacher><teacher><teachername>Govinda</teachername><teachertel>984545454</teachertel><teacheremail>govinda@gmail.com</teacheremail><subjects></subjects></teacher><teacher><teachername>Akshay Kumar</teachername><teachertel>9846546884</teachertel><teacheremail>akshay@kumar.com</teacheremail><subjects>Mathematics</subjects></teacher><teacher><teachername>prachi desai</teachername><teachertel>985454548</teachertel><teacheremail>prachi@gmail.com</teacheremail><subjects>English</subjects></teacher><teacher><teachername>Amrita Arora</teachername><teachertel>97415458755</teachertel><teacheremail>amrita@arora.com</teacheremail><subjects>Optional Mathematics</subjects></teacher><teacher><teachername>ranga seth</teachername><teachertel>4454875454</teachertel><teacheremail>ranga@set.com</teacheremail><subjects></subjects></teacher></teachersrecord><ads><ad><imagelink>https://designcontest-com-designcontest.netdna-ssl.com/blog/wp-content/uploads/2015/07/banner-ad-fail-11.jpg</imagelink><adlink>https://designcontest-com-designcontest.netdna-ssl.com/blog/wp-content/uploads/2015/07/banner-ad-fail-11.jpg</adlink></ad></ads></student>";

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
