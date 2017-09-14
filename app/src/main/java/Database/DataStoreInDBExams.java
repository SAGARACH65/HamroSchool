package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Database.Database;

/**
 * Created by Sagar on 6/12/2017.
 */

public class DataStoreInDBExams {
    private Context mContext;

    public DataStoreInDBExams(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase open() {
        SQLiteOpenHelper DataBase = new Database(mContext);

        return DataBase.getWritableDatabase();
    }

    public void storeStudenInfo(String result_type,String grade, String exam_type, String date, String markssheet,
                                String full_marks, String obt_marks, String comments,String CGPA ,boolean delete_table, boolean notclear) {

        SQLiteDatabase db = open();
        if (delete_table) {
            db.delete("Exams", null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "Exams" + "'");

        }
        if (!notclear) {
            ContentValues user_data = new ContentValues();
            user_data.put("Result_type", result_type);
            user_data.put("Class", grade);
            user_data.put("Exam_Type", exam_type);
            user_data.put("Date", date);
            user_data.put("Marks_Sheet", markssheet);
            user_data.put("Full_Marks", full_marks);
            user_data.put("Obtained_Marks", obt_marks);
            user_data.put("Comments", comments);
            user_data.put("CGPA", CGPA);
            db.insert("Exams", null, user_data);
        }
    }
}
