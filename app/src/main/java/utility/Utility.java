package utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sagar on 6/15/2017.
 * checks if internet connection is available or not
 */

public class Utility {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
//http://thenetwebs.com/myschoolapp/schoolapp/loginapi/getstudentdetails.php?usertoken=c44cbef5621fd6cc
//      http://thenetwebs.com/myschoolapp/schoolapp/loginapi/studentlogin.php

// 