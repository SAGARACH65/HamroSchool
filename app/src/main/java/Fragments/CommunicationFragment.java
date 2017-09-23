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

import com.hamroschool.activitypages.ConnectToTeachers;
import com.hamroschool.activitypages.MessagesList;
import com.hamroschool.activitypages.Notices;
import com.hamroschool.activitypages.R;

import Ads.GetTotalEntriesInDB;
import Ads.SelectWhichAdTOShow;
import Ads.ShowAds;
import utility.Utility;

import com.squareup.picasso.Picasso;

import Database.DBReceivedCachedImages;

public class CommunicationFragment extends Fragment {

    private static final String PREF_NAME_ADS_SYNCED = "HAS_ADS_SYNCED";

    public CommunicationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_three_fragment, container, false);


        ImageButton img_btn = (ImageButton) view.findViewById(R.id.imageButtonNotices);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Notices.class);
                startActivity(intent);

            }
        });

        TextView button1 = (TextView) view.findViewById(R.id.ButtonNotices);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Notices.class);
                startActivity(intent);

            }
        });


        ImageButton img_btn1 = (ImageButton) view.findViewById(R.id.imageButtonConnect);
        img_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConnectToTeachers.class);
                startActivity(intent);

            }
        });


        TextView button = (TextView) view.findViewById(R.id.ButtonConnect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConnectToTeachers.class);
                startActivity(intent);

            }
        });

        ImageButton img_btn2 = (ImageButton) view.findViewById(R.id.imageButtonMessage);
        img_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessagesList.class);
                startActivity(intent);

            }
        });
        //TODO remove these invisiblility



        TextView button2 = (TextView) view.findViewById(R.id.ButtonMessgae);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessagesList.class);
                startActivity(intent);

            }
        });


        Bitmap image_bitmap_data;
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
            //this is  for the rare case that can occour when while converting process of image network fails

            try {
                image_bitmap_data = adsData.getBitmap(which_ad);

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

            } catch (NullPointerException e) {
                SharedPreferences has_ads_synced1 = getContext().getSharedPreferences(PREF_NAME_ADS_SYNCED, 0);
                SharedPreferences.Editor editor2 = has_ads_synced1.edit();
                editor2.putBoolean("hasSynced", false);
                editor2.apply();
            }


        }


        return view;
    }
}
