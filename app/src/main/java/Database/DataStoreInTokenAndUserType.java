package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/7/2017.
 */

public class DataStoreInTokenAndUserType {

    private Context mContext;

    public DataStoreInTokenAndUserType(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getWritableDatabase();
    }

    public void storeUserNameAndPassword(String token, String user_type,boolean delete_table) {
        SQLiteDatabase db = open();

        if (delete_table) {
            db.delete("login_info", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "login_info" + "'");

        }
        ContentValues user_data = new ContentValues();
        user_data.put("Token", token);
        user_data.put("isteacher_or_parent", user_type);
        db.insert("login_info", null, user_data);

    }

    public void storeXML(String received,boolean delete_table,boolean notclear) {
        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("xml_data", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "xml_data" + "'");

        }
        if(!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("XML", received);

            db.insert("xml_data", null, user_data);
        }
    }

}
