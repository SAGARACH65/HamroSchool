package Ads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

import Database.DBReceivedCachedImages;
import Database.DBReceiverForProfile;

/**
 * Created by Sagar on 9/14/2017.
 */

public class ShowAds {
    private Context context;

    public ShowAds(Context context) {
        this.context = context;
    }

    public Bitmap getBitmap(int row_no) {

        //showing random ad

        DBReceivedCachedImages dbr = new DBReceivedCachedImages(context);
        byte[] ad_to_show = dbr.getDataBitmap(row_no);
        //converting the byte array back into bitstream
        return BitmapFactory.decodeByteArray(ad_to_show, 0, ad_to_show.length);
    }


    public String getRedirectLink(int row_no) {
        DBReceivedCachedImages dbr = new DBReceivedCachedImages(context);
        return dbr.getRedirectLink(row_no);
    }

}
