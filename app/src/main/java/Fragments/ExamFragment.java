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

import com.hamroschool.activitypages.Exams;
import com.hamroschool.activitypages.R;
import utility.Utility;
import com.squareup.picasso.Picasso;

import Database.DBReceivedCachedImages;

public class ExamFragment extends Fragment {


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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
