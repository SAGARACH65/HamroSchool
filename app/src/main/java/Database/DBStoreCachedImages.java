package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 7/19/2017.
 */

public class DBStoreCachedImages {
    private Context mContext;
    public DBStoreCachedImages(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getReadableDatabase();
    }

    public void storeAdlinks(String link,  String redirect_link, boolean delete_table,boolean notclear) {

        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("cached_images", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "cached_images" + "'");

        }
        if(!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("images", link);
            user_data.put("redirect_link", redirect_link);

            db.insert("cached_images", null, user_data);
        }
    }


}
