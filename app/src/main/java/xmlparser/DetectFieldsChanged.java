package xmlparser;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sagar on 6/16/2017.
 */

public class DetectFieldsChanged extends ContextWrapper {
    private String detected_change = "";
    private static  String fee_rec = "", exam_rec = "", notices_rec = "", fee_db = "", exam_db = "", notices_db = "";

    public DetectFieldsChanged(Context base) {
        super(base);
    }

    public String checkWhichDifferent(String received_XML, InputStream received_stream, InputStream db_stream)
            throws XmlPullParserException, IOException {


        try {
            XmlPullParser parser_web = Xml.newPullParser();
            parser_web.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser_web.setInput(received_stream, null);
            //parser.nextTag();
            // readFeed(parser_web);

            XmlPullParser parser_db = Xml.newPullParser();
            parser_db.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser_db.setInput(db_stream, null);
            checkforDB(parser_db);
            checkforWeb(parser_web);
            // return readFeed(parser);
            if(fee_db==null){
                detected_change=detected_change+"fee"+"#";
            }
        else if(!fee_db.equals(fee_rec)){
                detected_change=detected_change+"fee"+"#";
            }

            if(exam_db==null){
                detected_change=detected_change+"exam"+"#";
            }
            else if(!exam_db.equals(exam_rec)){
                detected_change=detected_change+"exam"+"#";
            }
            if(notices_db==null){
                detected_change=detected_change+"notices"+"#";
            }
            else if(!notices_db.equals(notices_rec)){
                detected_change=detected_change+"notices"+"#";
            }

            return detected_change;

        } finally {
            received_stream.close();
            db_stream.close();
        }
            }

    private void checkforWeb(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        String currentTag = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (currentTag == null || !(currentTag.equals("examresult") || currentTag.equals("noticesrecord") || currentTag.equals("feesrecord"))) {
                if (eventType == XmlPullParser.START_TAG) {
                    currentTag = parser.getName();
                }
            }
            if (eventType == XmlPullParser.TEXT) {
                if ("examresult".equals(currentTag)) {
                    exam_rec = exam_rec+parser.getText();
                }


                if ("noticesrecord".equals(currentTag)) {
                    notices_rec = notices_rec+parser.getText();
                }
                if ("feesrecord".equals(currentTag)) {
                    fee_rec = fee_rec+parser.getText();
                }
            }
             if (eventType == XmlPullParser.END_TAG&&(parser.getName().equals("examresult")||  parser.getName().equals("noticesrecord")||parser.getName().equals("feesrecord"))) {
                if ("examresult".equals(currentTag)) {
                    currentTag=null;
                }
                if ("noticesrecord".equals(currentTag)) {
                    currentTag=null;
                }
                if ("feesrecord".equals(currentTag)) {
                    currentTag=null;
                }
            }
            eventType = parser.next();
        }


    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private void checkforDB(XmlPullParser parser)throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        String currentTag = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (currentTag == null || !(currentTag.equals("examresult") || currentTag.equals("noticesrecord") || currentTag.equals("feesrecord"))) {
                if (eventType == XmlPullParser.START_TAG) {
                    currentTag = parser.getName();
                }
            }
            if (eventType == XmlPullParser.TEXT) {
                if ("examresult".equals(currentTag)) {
                    exam_db = exam_db+parser.getText();
                }
                if ("noticesrecord".equals(currentTag)) {
                    notices_db = notices_db+parser.getText();
                }
                if ("feesrecord".equals(currentTag)) {
                    fee_db = fee_db+parser.getText();
                }
            }
            else if (eventType == XmlPullParser.END_TAG&&(parser.getName().equals("examresult")||  parser.getName().equals("noticesrecord")||parser.getName().equals("feesrecord"))) {
                if ("examresult".equals(currentTag)) {
                    currentTag=null;
                }
                if ("noticesrecord".equals(currentTag)) {
                    currentTag=null;
                }
                if ("feesrecord".equals(currentTag)) {
                    currentTag=null;
                }
            }
            eventType = parser.next();
        }

    }
}
