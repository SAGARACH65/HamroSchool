package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sagar on 9/23/2017.
 */

public class DBReceiverForMessages {

    private Context mContext;

    public DBReceiverForMessages(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getReadableDatabase();
    }


    //still ned to change the fields
    public int getNoOfData() {

        int count = 0;
        SQLiteDatabase db = open();
        Cursor cursor = db.query("chat",
                new String[]{"_id", "Name_of_teacher","Chat_history",},
                null,
                null, null, null, null
        );

//int column_no=1;
        if (cursor.moveToFirst()) {
            do {
                count++;
                cursor.moveToNext();
//               String  send = cursor.getString(column_no);
//                column_no++;

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
        Cursor cursor = db.query("chat",
                new String[]{"_id", "Name_of_teacher","Chat_history",},
                null,
                null, null, null, null
        );

        if (cursor.moveToFirst()) {
            do {


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
