package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DBReceiverForExams {
    private String send;
    private Context mContext;

    public DBReceiverForExams(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);
        return DataBase.getReadableDatabase();
    }

    public int getNoOfData() {

        int count = 0;
        SQLiteDatabase db = open();
        Cursor cursor = db.query("Exams",
                new String[]{"_id","Result_type","Class",
                        "Exam_Type", "Date", "Marks_Sheet", "Full_Marks",
                        "Obtained_Marks", "Comments","CGPA",},
                null,
                null, null, null, null
        );
//
//        db.execSQL("CREATE TABLE " + TABLE_EXAMS + "("
//                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
//                +"Result_type TEXT"
//                + "Class TEXT,"
//                + "Exam_Type TEXT,"
//                + "Date TEXT,"
//                + "Marks_Sheet TEXT,"
//                + "Full_Marks TEXT,"
//                + "Obtained_Marks TEXT,"
//                + "Comments TEXT,"
//                + "CGPA TEXT);");
        if (cursor.moveToFirst()) {
            do {
                count++;
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return count;
    }

    //request specifies the data required by the caller
    public String getData(int row_no, int column_no) {
    /*
       *row no specifies the colun from which we want to get daat
       * column no specifies the typre of data to receive
     */
        String send = null;
        SQLiteDatabase db = open();
        Cursor cursor = db.query("Exams",
                new String[]{"_id","Result_type","Class",
                        "Exam_Type", "Date", "Marks_Sheet", "Full_Marks",
                        "Obtained_Marks", "Comments","CGPA",},
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
