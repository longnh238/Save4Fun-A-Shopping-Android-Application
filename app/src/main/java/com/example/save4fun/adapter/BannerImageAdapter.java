package com.example.save4fun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.save4fun.R;
import com.example.save4fun.model.Banner;

import java.util.List;

public class BannerImageAdapter extends PagerAdapter {

    private Context context;

    private List<Banner> banners;

    public BannerImageAdapter(Context context, List<Banner> banners) {
        this.context = context;
        this.banners = banners;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_slider, container, false);
        ImageView imageViewBanner = view.findViewById(R.id.imageViewBanner);

        Banner banner = banners.get(position);
        if (banner != null) {
            Glide.with(context).load(banner.getResourceId()).into(imageViewBanner);
        }

        // Add view to view group
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        if (banners != null) {
            return banners.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // Remove view from view group
        container.removeView((View) object);
    }
}
