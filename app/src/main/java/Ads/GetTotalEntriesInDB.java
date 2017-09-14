package Ads;

import android.content.Context;

import Database.DBReceivedCachedImages;

/**
 * Created by Sagar on 9/14/2017.
 */

public class GetTotalEntriesInDB {

    public int getTotalEntries(Context context) {
        DBReceivedCachedImages receiver = new DBReceivedCachedImages(context);
        return receiver.getNoOfData();
    }
}
