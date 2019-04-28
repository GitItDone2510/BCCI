package com.example.bcci;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    /*
    this constructor is used to pass the context here and if
    and we're gonna use it from the main activity then it
    will easy and error free.
    */
    public SliderAdapter(Context context){
        this.context = context;
    }

    // Arrays that will store the values of our slider
    public String[] slide_headings = {

            "Click It",
            "Scan It",
            "Add It"
    };

    public int[] slide_images = {

            R.drawable.business_card,
            R.drawable.scan_it,
            R.drawable.add_contacts
    };


    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        // get values of the view
        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_images);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_headings);

        // setting the values
        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);

        container.addView(view);

        return view;
    }

    @Override
    // this method will prevent any unusual error that can occur in the end of all screens.
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);

    }
}
