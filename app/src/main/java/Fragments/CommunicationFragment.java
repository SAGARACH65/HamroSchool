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

import com.hamroschool.activitypages.ConnectToTeachers;
import com.hamroschool.activitypages.Notices;
import com.hamroschool.activitypages.R;
import utility.Utility;
import com.squareup.picasso.Picasso;

import Database.DBReceivedCachedImages;

public class CommunicationFragment extends Fragment {


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

                // do something
            }
        });

        TextView button1 = (TextView) view.findViewById(R.id.ButtonNotices);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Notices.class);
                startActivity(intent);

                // do something
            }
        });


        ImageButton img_btn1 = (ImageButton) view.findViewById(R.id.imageButtonConnect);
        img_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConnectToTeachers.class);
                startActivity(intent);

                // do something
            }
        });


        TextView button = (TextView) view.findViewById(R.id.ButtonConnect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConnectToTeachers.class);
                startActivity(intent);

                // do something
            }
        });
        boolean isAvailable = Utility.isNetworkAvailable(getActivity());
        if (isAvailable) {
            DBReceivedCachedImages ad=new DBReceivedCachedImages(getContext());
            String link= ad.getData("ad");
            ImageView img= (ImageView) view.findViewById(R.id.imageView);
            Picasso.with(getContext())
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
