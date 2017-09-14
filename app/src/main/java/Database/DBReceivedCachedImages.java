package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 7/19/2017.
 */

public class DBReceivedCachedImages {

    private Context mContext;
    private String send;
    private byte[]send_byte_array;
    public DBReceivedCachedImages(Context context) {
        this.mContext = context;
    }
    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);

        return DataBase.getReadableDatabase();
    }

    public int getNoOfData() {

        int count = 0;
        SQLiteDatabase db = open();
        Cursor cursor = db.query("cached_images",
                new String[]{"_id", "images","redirect_link"},
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



    public  byte[] getDataBitmap(int row_no){


        SQLiteDatabase db = open();
        Cursor cursor = db.query("cached_images",
                new String[]{"_id", "images","redirect_link"},
                null,
                null, null, null, null
        );
        if (cursor.moveToFirst()) {
            do {

                if (cursor.getInt(0) == row_no) {
                    send_byte_array = cursor.getBlob(1);
                    cursor.close();
                    return send_byte_array;

                }
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return send_byte_array;

    }

    public String getRedirectLink(int row_no) {
    /*
       *row no specifies the colun from which we want to get daat
       * column no specifies the typre of data to receive
     */

    //as this is only for string data
    int column_no=2;
        String send = null;
        SQLiteDatabase db = open();
        Cursor cursor = db.query("cached_images",
                new String[]{"_id", "images","redirect_link"},
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

    //request specifies the data required by the caller


}
