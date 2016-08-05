package com.example.ozefet.udacitymovies.Main.Models;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    String lastpref;
    File directory;
    ContextWrapper cw;
    public ImageAdapter(Context c) {
        mContext = c;
        float density=c.getResources().getDisplayMetrics().density;
        height =c.getResources().getDisplayMetrics().heightPixels/density;
        if(density<=1){
            height /=2;}
        else if(density<=1.75){height/=1.5;}
        else if(density>=3.5){
            height *=1.5;}
    }

    public int getCount() {
    if(mThumbIds!=null){
        return mThumbIds.size();}
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
      //  if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, (int) height));
          // } else {
          //  imageView = (ImageView) convertView;
      //  }
        if ("2".equals(lastpref)){  //checking for each image

            cw = new ContextWrapper(mContext);
            // path to /data/data/yourapp/app_data/imageDir
            directory = cw.getDir("imageDir", mContext.MODE_PRIVATE);
            File f=new File(directory.getAbsolutePath(), mThumbIds.get(position)+".png");
            Picasso.with(mContext).load(f).into(imageView);
        }
        else{Picasso.with(mContext).load(mThumbIds.get(position)).into(imageView);}

        return imageView;
    }

    // references to our images
    List<String> mThumbIds=null;
    float height;

    public void updateList(List<String> data,String pref) {
        mThumbIds = data;
        lastpref=pref;
        notifyDataSetChanged();
    }

   }