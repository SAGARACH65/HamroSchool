package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DataStoreInDBProfile {
    private Context mContext;

    public DataStoreInDBProfile(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getWritableDatabase();
    }

    public void storeStudenInfo(String students_info, String school_name, byte[] photo_byte_array, String result_type, boolean delete_table, boolean notclear) {

        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("student_profile", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "student_profile" + "'");

        }
        if (!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("Student_info", students_info);
            user_data.put("School_Name", school_name);


            user_data.put("photo_bitmap", photo_byte_array);

            user_data.put("result_type", result_type);


            db.insert("student_profile", null, user_data);
        }
    }
}
