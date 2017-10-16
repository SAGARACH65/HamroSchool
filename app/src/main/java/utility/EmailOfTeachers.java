package utility;

import android.content.Context;

import java.util.ArrayList;

import Database.DBReceiverForConnectToTeachers;

/**
 * Created by Sagar on 10/16/2017.
 */

public class EmailOfTeachers {

    public static ArrayList<String> getEmailOfTeachers(Context context) {
        ArrayList<String> email_list = new ArrayList<>();

        DBReceiverForConnectToTeachers received = new DBReceiverForConnectToTeachers(context);
        int count = received.getNoOfData();

        for (int i = 1; i <= count; i++) {
            email_list.add(received.getData(i, 3));

        }
        return email_list;

    }
}
