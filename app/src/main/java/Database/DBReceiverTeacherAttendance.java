package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sagar on 9/15/2017.
 */

public class DBReceiverTeacherAttendance  {

    private Context mContext;
    private String send;

    public DBReceiverTeacherAttendance(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getReadableDatabase();
    }

    //request specifies the data required by the caller
    public String getData(String request) {
        SQLiteDatabase db = open();
        Cursor cursor = db.query("class_teacher_db",
                new String[]{"_id", "Name", "Current_date", "students_info",},
                null,
                null, null, null, null
        );
        send = getDataAccToRequest(cursor, request);
        cursor.close();
        return send;
    }



    private String getDataAccToRequest(Cursor cursor, String request) {
        switch (request) {
            case "Name":
                String s = getEntry(cursor, 1);
                return getEntry(cursor, 1);

            case "Current_date":
                return getEntry(cursor, 2);

            case "students_info":
                return getEntry(cursor, 3);

            default:
                return null;

        }
    }

    private String getEntry(Cursor cursor, int column_no) {
        String queried_data = null;
        if (cursor.moveToFirst()) {
            do {
                queried_data = cursor.getString(column_no);
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        return queried_data;
    }


}
