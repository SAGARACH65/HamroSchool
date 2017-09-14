package Fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hamroschool.activitypages.AttendenceRecord;
import com.hamroschool.activitypages.FeeRecord;
import com.hamroschool.activitypages.Profile;
import com.hamroschool.activitypages.R;
import utility.Utility;
import com.squareup.picasso.Picasso;

import Database.DBReceivedCachedImages;

public class StudentInfoFragment extends Fragment {


    public StudentInfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_two_frament, container, false);

        ImageButton img_btn1 = (ImageButton) view.findViewById(R.id.imageButtonProfile);
        img_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Profile.class);
                startActivity(intent);

                // do something
            }
        });
        TextView button1 = (TextView) view.findViewById(R.id.ButtonProfile);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Profile.class);
                startActivity(intent);

                // do something
            }
        });


        ImageButton img_btn2 = (ImageButton) view.findViewById(R.id.imageButtonFee);
        img_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeeRecord.class);
                startActivity(intent);

                // do something
            }
        });
        TextView button2 = (TextView) view.findViewById(R.id.ButtonFeeRecord);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeeRecord.class);
                startActivity(intent);

                // do something
            }
        });


        ImageButton img_btn3 = (ImageButton) view.findViewById(R.id.imageButtonAttendance);
        img_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AttendenceRecord.class);
                startActivity(intent);

                // do something
            }
        });
        TextView button3 = (TextView) view.findViewById(R.id.ButtonAttendance);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AttendenceRecord.class);
                startActivity(intent);

                // do something
            }
        });
        boolean isAvailable = Utility.isNetworkAvailable(getActivity());
        if (isAvailable) {
            DBReceivedCachedImages ad=new DBReceivedCachedImages(getContext());
            String link= ad.getData("ad");
            ImageView img= (ImageView) view.findViewById(R.id.imageView);
            Picasso.with(getActivity())
                    .load(link).fit()
                    .into(img);
            final String redirect= ad.getData("redirect");

            img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(redirect));
                    startActivity(intent);
                }
            });

        }


        return view;
    }

}
