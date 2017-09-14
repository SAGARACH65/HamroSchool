package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DBReceiverForConnectToTeachers {
    private Context mContext;

    public DBReceiverForConnectToTeachers(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getReadableDatabase();
    }

    public int getNoOfData() {

        int count = 0;
        SQLiteDatabase db = open();
        Cursor cursor = db.query("teacher_info",
                new String[]{"_id", "Subject", "Teacher_Name", "Email", "Contact_NO",},
                null,
                null, null, null, null
        );


        if (cursor.moveToFirst()) {
            do {
                count++;
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return count;
    }


    public String getData(int row_no, int column_no) {
    /*
       *row no specifies the colun from which we want to get daat
       * column no specifies the typre of data to receive
     */
        String send = null;
        SQLiteDatabase db = open();
        Cursor cursor = db.query("teacher_info",
                new String[]{"_id", "Subject", "Teacher_Name", "Email", "Contact_NO",},
                null,
                null, null, null, null
        );
        if (cursor.moveToFirst()) {
            do {

                int i = cursor.getInt(0);
                if (cursor.getInt(0) == row_no) {
                    send = cursor.getString(column_no);
                    cursor.close();
                    return send;

                }
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return send;
    }


}
