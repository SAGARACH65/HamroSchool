package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DataStoreInDBConnectTeachers {

    private Context mContext;

    public DataStoreInDBConnectTeachers(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getWritableDatabase();
    }
    public void storeTeacherInformation(String subject, String teacher_name, String email,String contact_no,boolean delete_table,boolean notclear) {
        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("teacher_info", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "teacher_info" + "'");

        }
        if (!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("Subject", subject);
            user_data.put("Teacher_Name", teacher_name);
            user_data.put("Email", email);
            user_data.put("Contact_NO", contact_no);

            db.insert("teacher_info", null, user_data);
        }
    }
}
