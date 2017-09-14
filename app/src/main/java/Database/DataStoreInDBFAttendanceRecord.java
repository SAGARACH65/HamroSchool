package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DataStoreInDBFAttendanceRecord {
    private Context mContext;

    public DataStoreInDBFAttendanceRecord(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getWritableDatabase();
    }

    public void storeAttendanceRecord(String attendance_record, boolean delete_table, boolean notclear) {
        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("attendance", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "attendance" + "'");

        }
        if (!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("attendance_record", attendance_record);
            db.insert("attendance", null, user_data);
        }
    }
}
