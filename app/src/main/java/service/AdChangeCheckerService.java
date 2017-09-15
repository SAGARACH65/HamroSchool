package service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import utility.Utility;
import xmlparser.XMLParserForAds;

/**
 * Created by Sagar on 9/14/2017.
 */

/**
 * This is responsible for changing and storing ads in a byte array in database
 */
public class AdChangeCheckerService extends IntentService {


    private String urll = "http://www.hamroschool.net/myschoolapp/loginapi/adservice.php?action=getads";
    private static final int POLL_INTERVAL = 1000 * 60;
    private static final String TAG = "AdChangeCheckerService";

    /*@Override
       public void onCreate() {
           super.onCreate();
   //whatever else you have to to here...
           android.os.Debug.waitForDebugger();  // this line is key
       }
   */
    public AdChangeCheckerService() {
        // Used to name the worker thread, important only for debugging.
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {

            boolean isNetwork = Utility.isNetworkAvailable(getApplicationContext());
            if (isNetwork) {
                readAndStoreAds();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }


    }

    private void readAndStoreAds() throws IOException, XmlPullParserException {
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

                //converting inputstrream to string
                returned = readStream(in);

                storeAds(returned);
            } finally {
                //regardkless of success or faliure we disconnect the connection
                urlConnection.disconnect();
            }
        } else {
            urlConnection.disconnect();
        }

    }

    private void storeAds(String received) throws IOException, XmlPullParserException {

        InputStream stream = new ByteArrayInputStream(received.getBytes());


        //setting up xml pullparser
        XmlPullParser parser = Xml.newPullParser();

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(stream, null);
        XMLParserForAds xml_parse = new XMLParserForAds(getApplicationContext());
        xml_parse.readFeedAndStore(parser);


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

        Intent i = new Intent(context, AdChangeCheckerService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);

        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}
