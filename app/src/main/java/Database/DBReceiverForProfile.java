package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DBReceiverForProfile {
    private Context mContext;
    private String send;
    public DBReceiverForProfile(Context context) {
        this.mContext = context;
    }
    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getReadableDatabase();
    }
    //request specifies the data required by the caller
    public  String getData(String request){
        SQLiteDatabase db = open();
        Cursor cursor = db.query("student_profile",
                new String[]{"_id", "Student_info", "School_Name","photo_bitmap","result_type"},
                null,
                null, null, null, null
        );
        send = getDataAccToRequest(cursor,request);
        cursor.close();
        return send;
    }
    public  byte[] getDataBitmap(){
        SQLiteDatabase db = open();
        Cursor cursor = db.query("student_profile",
                new String[]{"_id", "Student_info", "School_Name","photo_bitmap"},
                null,
                null, null, null, null
        );
        cursor.moveToFirst();
        byte[] send = cursor.getBlob(3);
        cursor.close();
        return send;
    }

    private  String getDataAccToRequest(Cursor cursor, String request){
        switch (request){
            case "Students_info":
                String s=getEntry(cursor,1);
                return getEntry(cursor,1);

            case"School_Name":
                return getEntry(cursor,2);

            case"result_type":

                return getEntry(cursor,4);
                default:
                    return null;

        }
    }
    private String getEntry(Cursor cursor,int column_no){
        String queried_data=null;
        if(cursor.moveToFirst()) {
            do{
                queried_data=cursor.getString(column_no);
                cursor.moveToNext();

            }while(!cursor.isAfterLast());
        }
        return  queried_data;
    }
}
