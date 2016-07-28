package com.example.ozefet.udacitymovies.Main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
        float density=c.getResources().getDisplayMetrics().density;
        height =c.getResources().getDisplayMetrics().heightPixels/density;
        if(density<=1){
            height /=2;}
        else if(density>=3.5){
            height *=1.5;}
    }

    public int getCount() {
    if(mThumbIds!=null){
        return mThumbIds.size();
    }
    else{return 0;}}

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, (int) height));
           } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load(mThumbIds.get(position)).into(imageView);
        return imageView;
    }

    // references to our images
    List<String> mThumbIds=null;
    float height;

    public void updateList(List<String> data) {
        mThumbIds = data;
        notifyDataSetChanged();
    }
   }