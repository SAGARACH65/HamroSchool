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
    public DBReceivedCachedImages(Context context) {
        this.mContext = context;
    }
    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);

        return DataBase.getReadableDatabase();
    }
    //request specifies the data required by the caller
    public  String getData(String request){
        SQLiteDatabase db = open();
        Cursor cursor = db.query("cached_images",
                new String[]{"_id", "images","redirect_link"},
                null,
                null, null, null, null
        );
        send = getDataAccToRequest(cursor,request);
        cursor.close();
        return send;
    }
    private  String getDataAccToRequest(Cursor cursor, String request){
        switch (request){
            case "images":
                String s=getEntry(cursor,1);
                return getEntry(cursor,1);
            case "redirect":
                return getEntry(cursor,2);

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
