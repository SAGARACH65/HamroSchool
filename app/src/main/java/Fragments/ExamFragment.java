package Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hamroschool.activitypages.Exams;
import com.hamroschool.activitypages.R;

import Ads.GetTotalEntriesInDB;
import Ads.SelectWhichAdTOShow;
import Ads.ShowAds;
import utility.Utility;

import com.squareup.picasso.Picasso;

import Database.DBReceivedCachedImages;

public class ExamFragment extends Fragment {
    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";

    public ExamFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        /*
            In all the three framgments the onclick listerners are for listening to click events of the imageview and text view
         */


        View view = inflater.inflate(R.layout.activity_one_fragment, container, false);
        TextView button = (TextView) view.findViewById(R.id.ButtonExams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Exams.class);
                startActivity(intent);

                // do something
            }
        });
        ImageButton img_btn = (ImageButton) view.findViewById(R.id.imageButtonExam);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Exams.class);
                startActivity(intent);

                // do something
            }
        });


        SharedPreferences settings = getContext().getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
        boolean has_ads_synced = settings.getBoolean("hasSynced", false);
        if (has_ads_synced) {
            //showing ads
            GetTotalEntriesInDB total = new GetTotalEntriesInDB();
            int no_of_entries = total.getTotalEntries(getContext());
            SelectWhichAdTOShow select = new SelectWhichAdTOShow();
            int which_ad = select.select_which_ad(no_of_entries);
            //getting bitmap and redirect link of that ad
            ShowAds adsData = new ShowAds(getContext());
           try{
            Bitmap image_bitmap_data = adsData.getBitmap(which_ad);
            final String redirect_link = adsData.getRedirectLink(which_ad);

            //show the ad in imageview
            ImageView img = (ImageView) view.findViewById(R.id.imageView);
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
            SharedPreferences has_ads_synced1 = getContext().getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
            SharedPreferences.Editor editor2 = has_ads_synced1.edit();
            editor2.putBoolean("hasSynced", false);
            editor2.apply();
        }
        }

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
