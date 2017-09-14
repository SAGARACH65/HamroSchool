package xmlparser;

import android.content.Context;
import android.content.SharedPreferences;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import Caching_Tools.ConvertToByteArray;
import Database.DBStoreCachedImages;

/**
 * Created by Sagar on 9/14/2017.
 */

public class XMLParserForAds {
    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";
    private static final String ns = null;
    private boolean complete_flag = false;
    private Context mContext;
    private byte[] ad_byte_array;

    public XMLParserForAds(Context mContext) {
        this.mContext = mContext;
    }

    public void readFeedAndStore(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {

                if (complete_flag) {
                    break;
                }
                continue;
            }
            name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("ads")) {
                readAdvertisement(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readAdvertisement(XmlPullParser parser) throws IOException, XmlPullParserException {
        String ad = null, redirect = null;
        parser.require(XmlPullParser.START_TAG, ns, "ads");
        parser.next();

        int i = 0;

        String tagName = parser.getName();
        while (!tagName.equals("ads")) {
            parser.next();
            tagName = parser.getName();

            while (!tagName.equals("ad")) {
                ad = readText(parser);
                parser.next();

                ConvertToByteArray convert = new ConvertToByteArray();
                ad_byte_array = convert.getLogoImage(ad);
                redirect = readText(parser);
                parser.next();
                tagName = parser.getName();

            }
            parser.next();
            tagName = parser.getName();
            DBStoreCachedImages dsa = new DBStoreCachedImages(mContext);
            if (i == 0) {
                dsa.storeAdlinks(ad_byte_array, redirect, true, false);

                i++;
            } else {
                dsa.storeAdlinks(ad_byte_array, redirect, false, false);

            }
        }
        complete_flag = true;
        SharedPreferences has_ads_synced = mContext.getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
        SharedPreferences.Editor editor2 = has_ads_synced.edit();
        editor2.putBoolean("hasSynced", true);
        editor2.apply();

    }


    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
