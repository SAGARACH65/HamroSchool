package Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sagar on 6/7/2017.
 */

public class Database extends SQLiteOpenHelper {
    private static String DB_NAME = "HamroSchoolDatabase";
    private static String TABLE_LOGIN_INFO = "login_info";

    private static String TABLE_TEACHERS_INFO = "teacher_info";

    private static String TABLE_FEE_RECORD = "fee_record";
    //this is for percentage
    private static String TABLE_EXAMS = "Exams";

    private static String TABLE_PROFILE = "student_profile";
    private static String TABLE_XML_DATA = "xml_data";
    private static String TABLE_XML_DATA_MSG = "xml_data_msg";
    private static String TABLE_NOTICES = "notices";

    private static String TABLE_ATTENDANCE = "attendance";
    private static String TABLE_CACHED_IMAGES_ADS = "cached_images";
    private static String CLASS_TEACHER = "class_teacher_db";

    private static String PARENT_TEACHER_CHAT = "chat";
    private static int DB_VERSION = 1;


    Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);//null is for cursors//sqlite helper classes constructor is being called

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Sqlitedatabase class gives us access to database


        db.execSQL("CREATE TABLE " + TABLE_LOGIN_INFO + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Token TEXT,"
                + "isteacher_or_parent TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_NOTICES + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Title TEXT,"
                + "Message TEXT,"
                + "pub_date TEXT,"
                + "Notice_type TEXT);");


        db.execSQL("CREATE TABLE " + TABLE_TEACHERS_INFO + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Subject TEXT,"
                + "Teacher_Name TEXT,"
                + "Email TEXT,"
                + "Contact_NO TEXT);");


        db.execSQL("CREATE TABLE " + TABLE_FEE_RECORD + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Class TEXT,"
                + "Particulars TEXT,"
                + "Amount TEXT,"
                + "Month TEXT,"
                + "Date TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_EXAMS + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Result_type TEXT,"
                + "Class TEXT,"
                + "Exam_Type TEXT,"
                + "Date TEXT,"
                + "Marks_Sheet TEXT,"
                + "Full_Marks TEXT,"
                + "Obtained_Marks TEXT,"
                + "Comments TEXT,"
                + "CGPA TEXT);");


        db.execSQL("CREATE TABLE " + TABLE_PROFILE + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Student_info TEXT,"
                + "School_Name TEXT,"
                + "photo_bitmap BLOB);");

        db.execSQL("CREATE TABLE " + TABLE_XML_DATA + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "XML TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_XML_DATA_MSG + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "XML_MSG TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"

                + "attendance_record TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_CACHED_IMAGES_ADS + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "images BLOB,"
                + "redirect_link TEXT);");

        db.execSQL("CREATE TABLE " + CLASS_TEACHER + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Name TEXT,"
                + "Current_date TEXT,"
                + "students_info TEXT);");


        db.execSQL("CREATE TABLE " + PARENT_TEACHER_CHAT + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Name_of_teacher TEXT,"
                + "Chat_history TEXT);");

        //to check if the ads of profile has synced or not


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
