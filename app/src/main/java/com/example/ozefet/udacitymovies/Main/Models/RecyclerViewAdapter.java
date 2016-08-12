package com.example.ozefet.udacitymovies.Main.Models;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ozefet.udacitymovies.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by ozefet on 12/08/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private final static int SIMILAR_MOVIE_PAGE = 3;
    private final static int FAVORITED_MOVIE_PAGE = 2;
    private List<String> moviesPosterList;
    private View view;
    private int pageid;
    ContextWrapper cw;
    File directory;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView poster;

        public MyViewHolder(View view) {
            super(view);
            poster = (ImageView) view.findViewById(R.id.poster_image);
        }
    }


    public RecyclerViewAdapter(List<String> moviesList, View view, int pageid) {
        this.moviesPosterList = moviesList;
        this.view = view;
        this.pageid = pageid;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (pageid == SIMILAR_MOVIE_PAGE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.similar_movie_list, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_poster_list, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String movie_poster = moviesPosterList.get(position);
        if (pageid == FAVORITED_MOVIE_PAGE) {
            cw = new ContextWrapper(view.getContext().getApplicationContext());
            directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory.getAbsolutePath(), movie_poster + ".png");
            Picasso.with(view.getContext().getApplicationContext()).load(f).into(holder.poster);
        } else {
            Picasso.with(view.getContext()).load(movie_poster).into(holder.poster);
        }

    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    @Override
    public int getItemCount() {
        return moviesPosterList.size();
    }
}