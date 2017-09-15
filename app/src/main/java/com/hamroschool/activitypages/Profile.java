package com.hamroschool.activitypages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Ads.GetTotalEntriesInDB;
import Ads.SelectWhichAdTOShow;
import Ads.ShowAds;
import Database.DBReceivedCachedImages;
import Database.DBReceiverForProfile;
import service.AdChangeCheckerService;
import service.PollService;
import utility.Utility;

public class Profile extends AppCompatActivity {
    private static final String PREF_NAME = "LOGIN_PREF";
    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //setting up toolbar
        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar = (TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkIfLoggedIn();
        //shows the tabs of the Profile table
        showData();
    }

    private void checkIfLoggedIn() {
        SharedPreferences settings1 = getSharedPreferences(PREF_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLogged = settings1.getBoolean("hasLoggedIn", false);
        if (!hasLogged) {
            stopService(new Intent(getApplicationContext(), PollService.class));
            stopService(new Intent(getApplicationContext(), AdChangeCheckerService.class));
            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
            //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void showData() {

        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);
        int count = 0;
        ArrayList<String> info_field = new ArrayList<String>();
        ArrayList<String> info_field_data = new ArrayList<String>();


        DBReceiverForProfile dbrec = new DBReceiverForProfile(getApplicationContext());
        //receiving the data from db
        String data = dbrec.getData("Students_info");


        String[] split_entries = data.split("#");

        for (int i = 0; i < split_entries.length; i++) {
            String[] user_data = split_entries[i].split("\\*");
            info_field.add(user_data[0]);

            info_field_data.add(user_data[1]);

            count++;
        }
        for (int i = 0; i < count; i++) {
            TableRow row = new TableRow(this);

            //set the color only for the fields in odd places
            if (i % 2 != 0) {
                row.setBackgroundColor(getResources().getColor(R.color.viewSplit));
            }
            row.setGravity(Gravity.CENTER);

//settting height of each row
            int tt = getResources().getDimensionPixelSize(R.dimen.heightprofile);
            row.setMinimumHeight(tt);
            // part1
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);


            //adding data items in textviews
            int dp = getResources().getDimensionPixelSize(R.dimen.profilewidth);
            TextView textview1 = new TextView(this);
            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            dp = getResources().getDimensionPixelSize(R.dimen.paddingstart);
            textview1.setPadding(dp, 0, 0, 0);
            textview1.setGravity(Gravity.START);

            //this is done because if not some text is hidden due to padding
            String show_text = "       " + info_field.get(i);
            textview1.setText(show_text);


            TextView textview2 = new TextView(this);
            //setting green color if present red if abscent

            textview2.setTextColor(Color.BLACK);

            dp = getResources().getDimensionPixelSize(R.dimen.profilewidth);
            textview2.setWidth(dp);
            textview2.setGravity(Gravity.START);
            textview2.setText(info_field_data.get(i));


            row.addView(textview1);
            row.addView(textview2);


            tabLayout.addView(row, i);
        }

//retriving and displaying user images from database
        DBReceiverForProfile dbr = new DBReceiverForProfile(getApplicationContext());
        byte[] byte_array = dbr.getDataBitmap();
        //converting the byte array back into bitstream
        Bitmap image_bitmap = BitmapFactory.decodeByteArray(byte_array, 0, byte_array.length);
        ImageView img_user_photo = (ImageView) findViewById(R.id.imageViewProfile);
        img_user_photo.setImageBitmap(image_bitmap);

//showing ads
        checkAndShowAds();


    }

    private void checkAndShowAds() {
        SharedPreferences settings = getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
        boolean has_ads_synced = settings.getBoolean("hasSynced", false);
        if (has_ads_synced) {
            DBReceivedCachedImages total = new DBReceivedCachedImages(getApplicationContext());
            int no_of_entries = total.getNoOfData();
            // no_of_entries++;
            SelectWhichAdTOShow select = new SelectWhichAdTOShow();
            int which_ad = select.select_which_ad(no_of_entries);
            //getting bitmap and redirect link of that ad
            ShowAds adsData = new ShowAds(getApplicationContext());
            try{
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
        }catch (NullPointerException e){
            SharedPreferences has_ads_synced1 = getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
            SharedPreferences.Editor editor2 = has_ads_synced1.edit();
            editor2.putBoolean("hasSynced", false);
            editor2.apply();
        }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
