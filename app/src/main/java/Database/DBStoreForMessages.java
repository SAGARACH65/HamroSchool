package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sagar on 9/23/2017.
 */

public class DBStoreForMessages {

    private Context mContext;

    public DBStoreForMessages(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getWritableDatabase();
    }

    public void storeMessages(String name_of_teacher, String chat_history,
                                  boolean delete_table, boolean notclear) {
        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("chat", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "chat" + "'");

        }
        if (!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("Name_of_teacher", name_of_teacher);
            user_data.put("Chat_history", chat_history);



            db.insert("chat", null, user_data);
        }
    }


}
