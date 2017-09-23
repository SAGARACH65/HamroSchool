package utility;

import android.content.Context;

import java.util.ArrayList;

import Database.DBReceiverForConnectToTeachers;

/**
 * Created by Sagar on 9/23/2017.
 */

public class NameOfTeachers {



    public static ArrayList<String> getNameOfTeachers(Context context) {
        ArrayList<String> techers_list = new ArrayList<>();

        DBReceiverForConnectToTeachers received = new DBReceiverForConnectToTeachers(context);
        int count = received.getNoOfData();

        for (int i = 1; i <= count; i++) {
            techers_list.add(received.getData(i, 2));

        }
        return techers_list;

    }
}
