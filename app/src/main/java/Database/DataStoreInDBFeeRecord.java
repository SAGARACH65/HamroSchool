package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DataStoreInDBFeeRecord {
    private Context mContext;

    public DataStoreInDBFeeRecord(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getWritableDatabase();
    }

    public void storeFeeRecord(String grade, String amount, String on_date, String particulars,
                               String month, boolean delete_table,boolean notclear) {
        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("fee_record", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "fee_record" + "'");

        }
        if(!notclear){
        ContentValues user_data = new ContentValues();
        user_data.put("Class", grade);
        user_data.put("Particulars", particulars);
        user_data.put("Amount", amount);
        user_data.put("Month", month);
        user_data.put("Date", on_date);

        db.insert("fee_record", null, user_data);
    }
}
}

