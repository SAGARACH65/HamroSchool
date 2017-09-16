package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sagar on 6/7/2017.
 */

public class DBReceiveTokenAndUserType {
    private Context mContext;

    public DBReceiveTokenAndUserType(Context context) {
        this.mContext = context;
    }

    private String send;

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getReadableDatabase();
    }


    public String getTokenAndLoginPersonType(int whichdata) {
        SQLiteDatabase db = open();
        Cursor cursor = db.query("login_info",
                new String[]{"_id", "Token", "isteacher_or_parent",},
                null,
                null, null, null, null
        );


        send = receiveData(cursor, whichdata);
        cursor.close();
        return send;
    }

    private String receiveData(Cursor cursor, int whichdata) {
        String token = null;
        String user_type = null;
        if (cursor.moveToFirst()) {
            do {
                token = cursor.getString(1);
                user_type = cursor.getString(2);
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        if (whichdata == 1) return token;
        else return user_type;
    }

    public String receiveXML() {
        SQLiteDatabase db = open();
        Cursor cursor = db.query("xml_data",
                new String[]{"_id", "XML",},
                null,
                null, null, null, null
        );
        if (cursor.moveToFirst()) {
            do {
                send = cursor.getString(1);
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return send;
    }

}