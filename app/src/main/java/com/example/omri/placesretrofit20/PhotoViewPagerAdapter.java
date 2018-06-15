package com.example.omri.placesretrofit20;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by omri on 18/01/2018.
 */

public class PhotoViewPagerAdapter   extends PagerAdapter {

    ImageView imageView;
    private Context context;
    private LayoutInflater layoutInflater;
    public ArrayList<String> photoStr;

    public PhotoViewPagerAdapter(Context context, ArrayList<String>photoStr) {
        this.context = context;
        this.photoStr = photoStr;

    }
    @Override
    public int getCount() {
        return photoStr.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.photo_item, null);
        imageView = view.findViewById(R.id.place_photo);
        Picasso.with(view.getContext()).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400" +
                "&photoreference=" + photoStr.get(position) +
                "&key=" + MyDataManager.key
        ).into(imageView);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}