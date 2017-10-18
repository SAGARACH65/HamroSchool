package Caching_Tools;

import android.content.Context;
import android.util.Log;

import com.hamroschool.activitypages.ExamMarkSheetGPA;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import utility.Utility;

/**
 * Created by Sagar on 9/13/2017.
 */

public class ConvertToByteArray {

    public byte[] getLogoImage(String url) throws Exception {


//        try {

            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;

            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }

            baos.flush();

            return baos.toByteArray();

//        } catch (Exception e) {
//            Log.d("ConvertToByteArray", "Error: " + e.toString());
//        }


    }
}
