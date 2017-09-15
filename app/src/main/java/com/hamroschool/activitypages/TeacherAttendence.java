package com.hamroschool.activitypages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import Ads.GetTotalEntriesInDB;
import Ads.SelectWhichAdTOShow;
import Ads.ShowAds;


public class TeacherAttendence extends AppCompatActivity {
    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendence);

        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);












        SharedPreferences settings = getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
        boolean has_ads_synced = settings.getBoolean("hasSynced", false);
        if (has_ads_synced) {
            //showing ads
            GetTotalEntriesInDB total = new GetTotalEntriesInDB();
            int no_of_entries = total.getTotalEntries(getApplicationContext());
            SelectWhichAdTOShow select = new SelectWhichAdTOShow();
            int which_ad = select.select_which_ad(no_of_entries);
            //getting bitmap and redirect link of that ad
            ShowAds adsData = new ShowAds(getApplicationContext());
            Bitmap image_bitmap_data = adsData.getBitmap(which_ad);
            final String redirect_link = adsData.getRedirectLink(which_ad);

            //show the ad in imageview
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setImageBitmap(image_bitmap_data);

            //redirect link for the ad
            img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(redirect_link));
                    startActivity(intent);
                }
            });
        }
    }
}
