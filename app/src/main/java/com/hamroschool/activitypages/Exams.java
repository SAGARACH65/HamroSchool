package com.hamroschool.activitypages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import Ads.GetTotalEntriesInDB;
import Ads.SelectWhichAdTOShow;
import Ads.ShowAds;
import Database.DBReceivedCachedImages;
import Database.DBReceiverForExams;
import service.PollService;
import utility.Utility;

public class Exams extends AppCompatActivity {
    private int m_clicked_positon;
    private static final String PREF_NAME = "LOGIN_PREF";
    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);
        TableLayout tabLayout = (TableLayout) findViewById(R.id.main_table);
        //setting up toolbar
        Toolbar toolbar;
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title_bar=(TextView) findViewById(R.id.mainToolBar);
        title_bar.setText(R.string.Exams);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences settings1 = getSharedPreferences(PREF_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLogged = settings1.getBoolean("hasLoggedIn", false);
        if(!hasLogged){
            stopService(new Intent(getApplicationContext(), PollService.class));
            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

DBReceiverForExams received=new DBReceiverForExams(getApplicationContext());
        int count=received.getNoOfData();


        for(int i=0;i<count;i++){
            TableRow row= new TableRow(this);
            row.setId(1000+i);
            row.setOnClickListener(mlistner);
            //set the color only for the fields in odd places
            if(i%2!=0) {
                row.setBackgroundColor(getResources().getColor(R.color.viewSplit));
            }
            row.setGravity(Gravity.CENTER);
            int tt = getResources().getDimensionPixelSize(R.dimen.height);
            row.setMinimumHeight(tt);
            // part1
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            DisplayMetrics displaymetrics = new DisplayMetrics();

            int dp = getResources().getDimensionPixelSize(R.dimen.width);

            TextView textview1 = new TextView(this);

            textview1.setWidth(dp);
            textview1.setTextColor(Color.BLACK);
            textview1.setGravity(Gravity.CENTER);
            textview1.setText(received.getData(i+1,1));


            TextView textview2 = new TextView(this);
            textview2.setTextColor(Color.BLACK);
            textview2.setWidth(dp);
            textview2.setGravity(Gravity.CENTER);
            textview2.setText(received.getData(i+1,2));

            int x = getResources().getDimensionPixelSize(R.dimen.width2);
            int paddrt = getResources().getDimensionPixelSize(R.dimen.padd);
            TextView textview3 = new TextView(this);
            textview3.setGravity(Gravity.CENTER);
            textview3.setWidth(x);
            textview3.setTextColor(Color.BLACK);
            textview3.setPadding(0,0,paddrt,0);
                String s=received.getData(i+1,3);
            textview3.setText(received.getData(i+1,3));


            TextView textview4 = new TextView(this);

            textview4.setGravity(Gravity.CENTER);
            textview4 .setPaintFlags(textview4.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            textview4.setWidth(dp);
            textview4.setTextColor(Color.BLACK);
            textview4.setText(R.string.view);


            row.addView(textview1);
            row.addView(textview2);
            row.addView(textview3);
            row.addView(textview4);

            tabLayout.addView(row,i);

        }
        //showing ads
        SharedPreferences settings = getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
        boolean has_ads_synced = settings.getBoolean("hasSynced", false);
        if(has_ads_synced) {
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
    View.OnClickListener mlistner=new View.OnClickListener() {

        public void onClick(View v) {
            m_clicked_positon=v.getId()-1000;

            DBReceiverForExams received=new DBReceiverForExams(getApplicationContext());
            String marks_sheet=received.getData(m_clicked_positon+1,4);
            String full_marks=received.getData(m_clicked_positon+1,5);
            String marks_obtained =received.getData(m_clicked_positon+1,6);
            String comment=received.getData(m_clicked_positon+1,7);

            Intent intent = new Intent(getApplicationContext(), ExamMarksSheetPercentage.class);
            Bundle extras = new Bundle();
            extras.putString("marks_sheet",marks_sheet);
            extras.putString("marks_obtained",marks_obtained);
            extras.putString("full_marks",full_marks);
            extras.putString("comment",comment);
            intent.putExtras(extras);
            startActivity(intent);
        }
    };

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
