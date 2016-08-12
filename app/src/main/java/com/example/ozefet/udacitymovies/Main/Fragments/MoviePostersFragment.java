package com.example.ozefet.udacitymovies.Main.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ozefet.udacitymovies.Main.Activities.DetailsActivity;
import com.example.ozefet.udacitymovies.Main.Models.MovieItem;
import com.example.ozefet.udacitymovies.Main.Models.RecyclerViewAdapter;
import com.example.ozefet.udacitymovies.R;

import java.util.ArrayList;
import java.util.List;

public class MoviePostersFragment extends Fragment {

    private final static int FAVORITED_MOVIE_PAGE = 2;
    private static final String KEY_LIST = "keyList";
    private static final int ROW_NUMBER = 2;

    public static MoviePostersFragment newInstance(ArrayList<MovieItem> movieItemList, int position) {
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_LIST, movieItemList);
        bundle.putInt("position", position);

        final MoviePostersFragment fragment = new MoviePostersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayoutId(), container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        final ArrayList<MovieItem> movieItemList = getArguments().getParcelableArrayList(KEY_LIST);
        int position = getArguments().getInt("position");
        List<String> mThumbIds = new ArrayList<String>();

        if (movieItemList != null) {
            for (int i = 0; i < movieItemList.size(); i++) {
                if (movieItemList.get(i).poster_url != null) {
                    mThumbIds.add("http://image.tmdb.org/t/p/w185" + movieItemList.get(i).poster_url);
                } else if (position != FAVORITED_MOVIE_PAGE) {
                    movieItemList.remove(i);
                } else {
                    mThumbIds.add(String.valueOf(movieItemList.get(i).id));
                }
            }
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(mThumbIds, view, position);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(view.getContext().getApplicationContext(), ROW_NUMBER);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewAdapter.RecyclerTouchListener
                (view.getContext(), recyclerView, new RecyclerViewAdapter.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent myintent = new Intent(view.getContext().getApplicationContext(), DetailsActivity.class);
                        if (movieItemList != null) {
                            myintent.putExtra("movie_details", movieItemList.get(position));
                        }
                        view.getContext().startActivity(myintent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }

    @LayoutRes
    private int getFragmentLayoutId() {
        return R.layout.movie_poster_fragment;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void AlertMsg() {
        new AlertDialog.Builder(getActivity())
                .setTitle("No connection!")
                .setMessage("You need a valid internet connection to use the app.")
                .setNeutralButton("OK!", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}