package xmlparser;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import Database.DataStoreTeacherAttendance;

/**
 * Created by Sagar on 9/15/2017.
 */

public class XMLParserForTeachers {
    private static final String ns = null;
    private boolean complete_flag = false;
    private Context mContext;
    public XMLParserForTeachers(Context mContext) {
        this.mContext = mContext;
    }

    public void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            readFeedAndStore(parser);

        } finally {
            in.close();
        }
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
            if (name.equals("students")) {
                readInformation(parser);
            } else {
                skip(parser);
            }
        }

    }

    private void readInformation(XmlPullParser parser) throws IOException, XmlPullParserException {
       String teacher_name=null,current_date=null,students_info=null;

        parser.require(XmlPullParser.START_TAG, ns, "students");

        parser.next();
        String tagName = parser.getName();
        //TODO modify according to the XML
        teacher_name= readText(parser);

        DataStoreTeacherAttendance store=new DataStoreTeacherAttendance(mContext);
        store.storeTeacherInfo(teacher_name,current_date,students_info,true,false);
        complete_flag=true;
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
