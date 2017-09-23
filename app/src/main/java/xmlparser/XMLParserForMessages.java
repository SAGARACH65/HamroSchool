package xmlparser;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import Caching_Tools.ConvertToByteArray;
import Database.DBStoreCachedImages;
import Database.DBStoreForMessages;
import Database.DataStoreInTokenAndUserType;
import utility.Utility;

/**
 * Created by Sagar on 9/23/2017.
 */

public class XMLParserForMessages {
    private static final String ns = null;
    private Context mContext;
    private boolean complete_flag = false;

    // We don't use namespaces
    public XMLParserForMessages(Context context) {
        this.mContext = context;
    }


    //this is only called when user loggs in for the first time
    //t constructs xmlpull parser. but background service does this by itself
    public void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            readFeed(parser);

        } finally {
            in.close();
        }
    }

    //this is called by the service directly
    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {


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
            if (name.equals("chat")) {
                readEntry(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readEntry(XmlPullParser parser) throws IOException, XmlPullParserException {


        String name = null, history = null;
        parser.require(XmlPullParser.START_TAG, ns, "chat");
        parser.next();

        int i = 0;

        String tagName = parser.getName();
        while (!tagName.equals("chat")) {
            parser.next();
            tagName = parser.getName();

            while (!tagName.equals("teacher")) {
                name = readText(parser);
                parser.next();



                history = readText(parser);
                parser.next();
                tagName = parser.getName();

            }
            parser.next();
            tagName = parser.getName();
            DBStoreForMessages dsm = new DBStoreForMessages(mContext);

            //if network is lost during the entry we do not store the data

                if (i == 0) {
                    dsm.storeMessages(name, history, true, false);

                    i++;

                } else {
                    dsm.storeMessages(name, history, false, false);

                }

        }
        complete_flag = true;


    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
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
