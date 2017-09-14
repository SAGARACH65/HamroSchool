package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DataStoreInDBrNotices {
    private Context mContext;

    public DataStoreInDBrNotices(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getWritableDatabase();
    }

    public void storeNoticeRecord(String title, String message, String pub_date, String notice_type,
                                  boolean delete_table, boolean notclear) {
        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("notices", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "notices" + "'");

        }
        if (!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("Title", title);
            user_data.put("Message", message);
            user_data.put("pub_date", pub_date);
            user_data.put("Notice_type", notice_type);


            db.insert("notices", null, user_data);
        }
    }
}
