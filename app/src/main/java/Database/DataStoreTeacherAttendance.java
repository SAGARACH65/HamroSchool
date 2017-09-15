package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sagar on 9/15/2017.
 */

public class DataStoreTeacherAttendance {


    private Context mContext;

    public DataStoreTeacherAttendance(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getWritableDatabase();
    }

    public void storeTeacherInfo(String teacher_name, String current_date, String student_data,
                                    boolean delete_table, boolean notclear) {

        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("class_teacher_db", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "class_teacher_db" + "'");

        }
        if (!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("Name", teacher_name);
            user_data.put("Current_date", current_date);

            user_data.put("students_info", student_data);

            db.insert("class_teacher_db", null, user_data);
        }
    }
}
