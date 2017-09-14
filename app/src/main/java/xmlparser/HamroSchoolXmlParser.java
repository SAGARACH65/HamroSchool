package xmlparser;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import Caching_Tools.ConvertToByteArray;
import Database.DBStoreCachedImages;
import Database.DataStoreInDBConnectTeachers;
import Database.DataStoreInDBExams;
import Database.DataStoreInDBFAttendanceRecord;
import Database.DataStoreInDBFeeRecord;
import Database.DataStoreInDBProfile;
import Database.DataStoreInDBrNotices;

/**
 * Created by Sagar on 9/11/2017.
 */

public class HamroSchoolXmlParser {

    private static final String ns = null;
    private Context mContext;
    private boolean complete_flag = false;

    // We don't use namespaces
    public HamroSchoolXmlParser(Context context) {
        this.mContext = context;
    }

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

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        String name = parser.getName();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {

                if (complete_flag) {
                    break;
                }
                continue;
            }
            name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("student")) {
                readEntry(parser);
            } else {
                skip(parser);
            }
        }

    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private void readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case "profile":
                    readProfile(parser);
                    break;
                case "examresult":
                    readExamResult(parser);
                    break;
                case "feesrecord":
                    readFee(parser);
                    break;
                case "attendancerecord":
                    readAttendance(parser);
                    break;
                case "noticesrecord":
                    readNoticeRecord(parser);
                    break;
                case "teachersrecord":
                    readTeacherRecord(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }

        }
        complete_flag = true;
    }


    private void readTeacherRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        String subject = null, teacher_name = null, email = null, contact_no = null;
        parser.require(XmlPullParser.START_TAG, ns, "teachersrecord");
        // String title = readText(parser);
        int i = 0;
        parser.next();
        String tagName = parser.getName();
        while (!tagName.equals("teachersrecord")) {
            parser.next();
            tagName = parser.getName();

            while (!tagName.equals("teacher")) {
                teacher_name = readText(parser);
                parser.next();

                contact_no = readText(parser);
                parser.next();

                email = readText(parser);
                parser.next();

                subject = readText(parser);
                parser.next();

                tagName = parser.getName();

            }
            parser.next();
            tagName = parser.getName();
            DataStoreInDBConnectTeachers dbs = new DataStoreInDBConnectTeachers(mContext);
            if (i == 0) {
                dbs.storeTeacherInformation(subject, teacher_name, email, contact_no, true, false);
                i++;
            } else {
                dbs.storeTeacherInformation(subject, teacher_name, email, contact_no, false, false);
            }

        }
        parser.require(XmlPullParser.END_TAG, ns, "teachersrecord");

    }

    private void readNoticeRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        String title_notice = null, message = null, notice_type = null, pub_date = null, exp_date = null;
        parser.require(XmlPullParser.START_TAG, ns, "noticesrecord");
        int i = 0;
        parser.next();
        String tagName = parser.getName();
        while (!tagName.equals("noticesrecord")) {
            parser.next();
            tagName = parser.getName();

            while (!tagName.equals("notice")) {
                title_notice = readText(parser);
                parser.next();

                message = readText(parser);
                parser.next();

                pub_date = readText(parser);
                parser.next();

                exp_date = readText(parser);
                parser.next();

                notice_type = readText(parser);
                parser.next();

                tagName = parser.getName();

            }
            parser.next();
            tagName = parser.getName();
            DataStoreInDBrNotices dbs = new DataStoreInDBrNotices(mContext);
            if (i == 0) {
                dbs.storeNoticeRecord(title_notice, message, pub_date, notice_type, true, false);
                i++;
            } else {
                dbs.storeNoticeRecord(title_notice, message, pub_date, notice_type, false, false);

            }

        }

        parser.require(XmlPullParser.END_TAG, ns, "noticesrecord");

    }

    private void readFee(XmlPullParser parser) throws IOException, XmlPullParserException {
        String grade = null, amount = null, on_date = null, particulars = null, month = null;
        parser.require(XmlPullParser.START_TAG, ns, "feesrecord");

        int i = 0;
        parser.next();
        String tagName = parser.getName();
        while (!tagName.equals("feesrecord")) {
            parser.next();
            tagName = parser.getName();

            while (!tagName.equals("fee")) {
                grade = readText(parser);
                parser.next();
                parser.next();
                parser.next();
                parser.next();

                amount = readText(parser);
                parser.next();

                on_date = readText(parser);
                parser.next();

                particulars = readText(parser);
                parser.next();

                month = readText(parser);
                parser.next();
                tagName = parser.getName();

            }
            parser.next();
            tagName = parser.getName();
            DataStoreInDBFeeRecord dbs = new DataStoreInDBFeeRecord(mContext);
            if (i == 0) {
                dbs.storeFeeRecord(grade, amount, on_date, particulars, month, true, false);
                i++;
            } else {
                dbs.storeFeeRecord(grade, amount, on_date, particulars, month, false, false);

            }

        }

        parser.require(XmlPullParser.END_TAG, ns, "feesrecord");

    }

    private void readExamResult(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result_type = null, grade = null, examtype = null, date = null,
                marks_sheet = null, full_marks = null, obtained_marks = null, comments = null, CGPA = null;
        int i = 0;

        if (parser.getName().equals("examresult")) {
            parser.require(XmlPullParser.START_TAG, ns, "examresult");

            parser.next();
            String tagName = parser.getName();
            while (!tagName.equals("examresult")) {
                parser.next();
                tagName = parser.getName();

                while (!tagName.equals("exam")) {
                    result_type = readText(parser);
                    parser.next();

                    grade = readText(parser);
                    parser.next();
                    parser.next();
                    parser.next();
                    parser.next();

                    marks_sheet = readText(parser);
                    parser.next();
                    parser.next();
                    parser.next();
                    parser.next();
                    if (result_type.equals("percentage")) {
                        full_marks = readText(parser);
                        parser.next();

                        obtained_marks = readText(parser);
                        parser.next();
                    } else {
                        //this is for gpa
                        full_marks = "0";
                        obtained_marks = "0";
                    }
                    date = readText(parser);
                    parser.next();

                    examtype = readText(parser);
                    parser.next();

                    comments = readText(parser);
                    parser.next();

                    if (result_type.equals("percentage")) {
                        CGPA = "0";
                    } else {
                        CGPA = readText(parser);
                        parser.next();
                    }
                    tagName = parser.getName();

                }
                parser.next();
                tagName = parser.getName();
                DataStoreInDBExams dbs = new DataStoreInDBExams(mContext);
                if (i == 0) {
                    dbs.storeStudenInfo(result_type, grade, examtype, date, marks_sheet, full_marks, obtained_marks, comments, CGPA, true, false);
                    i++;
                } else {
                    dbs.storeStudenInfo(result_type, grade, examtype, date, marks_sheet, full_marks, obtained_marks, comments, CGPA, false, false);

                }
            }

            parser.require(XmlPullParser.END_TAG, ns, "examresult");
        }
    }

    // Processes profile tags in the feed.
    private void readProfile(XmlPullParser parser) throws IOException, XmlPullParserException {
        String students_info, photo_link, school_name, result_type;
        parser.require(XmlPullParser.START_TAG, ns, "profile");
        parser.next();

        students_info = readText(parser);
        parser.next();


        school_name = readText(parser);
        parser.next();

        photo_link = readText(parser);
        ConvertToByteArray cvb = new ConvertToByteArray();
        byte[] byte_array = cvb.getLogoImage(photo_link);

        parser.next();

        DataStoreInDBProfile dsp = new DataStoreInDBProfile(mContext);
        dsp.storeStudenInfo(students_info, school_name, byte_array, true, false);

    }

    private void readAttendance(XmlPullParser parser) throws IOException, XmlPullParserException {
        String attendance_list = null;
        int i = 0;

        parser.require(XmlPullParser.START_TAG, ns, "attendancerecord");

        parser.next();
        String tagName = parser.getName();
        attendance_list = readText(parser);
        parser.next();
        DataStoreInDBFAttendanceRecord dsp=new DataStoreInDBFAttendanceRecord(mContext);
        dsp.storeAttendanceRecord(attendance_list, true, false);

        parser.require(XmlPullParser.END_TAG, ns, "attendancerecord");
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
