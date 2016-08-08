package com.example.ozefet.udacitymovies.Main.Fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ozefet.udacitymovies.Main.Activities.DetailsActivity;
import com.example.ozefet.udacitymovies.Main.Models.ImageAdapter;
import com.example.ozefet.udacitymovies.Main.Models.MovieItem;
import com.example.ozefet.udacitymovies.Main.Models.MovieJsonDeserializer;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieContract;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieDbHelper;
import com.example.ozefet.udacitymovies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoviePostersFragment extends Fragment {
    ImageAdapter imageAdapter;
    List<MovieItem> movieItemList = new ArrayList<MovieItem>();
    SharedPreferences settings;
    String lastpref;
    GridView movieposters;

    @Override
    public void onResume() {
        super.onResume();  // always call the superclass method first
        String currentsetting=settings.getString(("sort_order_list"), "");
        boolean isexecuted=false;
        if(lastpref!=null&&currentsetting!=""&&!lastpref.equals(currentsetting)){
            if ("2".equals(currentsetting)){
                isexecuted=true;imageAdapter= new ImageAdapter(getActivity());movieposters.setAdapter(imageAdapter); movieItemList.clear();GetLocalMovies();}
            else if(isOnline()){
                lastpref = currentsetting;
                if (lastpref.equals("")){
                    lastpref ="0";}
                String URI=null;
                if (lastpref.equals("0")) {URI= "http://api.themoviedb.org/3/movie/popular?api_key="+getString(R.string.api_key);}
                else if(lastpref.equals("1")){URI= "http://api.themoviedb.org/3/movie/top_rated?api_key="+getString(R.string.api_key);}

                MovieJsonDeserializer movieJsonDeserializer =new MovieJsonDeserializer();
                movieJsonDeserializer.JsonDeserializerMovie(0,getView(),0, URI);}

            else{AlertMsg();}}
        if ("2".equals(currentsetting)&&!isexecuted){ movieItemList.clear();GetLocalMovies();}
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(getFragmentLayoutId(), container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        movieposters = (GridView) getActivity().findViewById(R.id.movieposters_gridview);
        imageAdapter= new ImageAdapter(getActivity());
        movieposters.setAdapter(imageAdapter);
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if((isOnline()&&!"2".equals(settings.getString(("sort_order_list"), "")))){

            lastpref = settings.getString(("sort_order_list"), "");
            if (lastpref.equals("")){
                lastpref ="0";}
            String URI=null;
            if (lastpref.equals("0")) {URI= "http://api.themoviedb.org/3/movie/popular?api_key="+getString(R.string.api_key);}
            else if(lastpref.equals("1")){URI= "http://api.themoviedb.org/3/movie/top_rated?api_key="+getString(R.string.api_key);}
            MovieJsonDeserializer movieJsonDeserializer =new MovieJsonDeserializer();
            movieJsonDeserializer.JsonDeserializerMovie(0,getView(),0, URI);}
        else if(!isOnline()){AlertMsg();}
        }

    private void GetLocalMovies() {
        lastpref = "2";
        MovieDbHelper mDbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                MovieContract.MovieEntry.COLUMN_ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_RELEASEDATE,
                MovieContract.MovieEntry.COLUMN_VOTEAVERAGE,
                MovieContract.MovieEntry.COLUMN_GENRE,
                MovieContract.MovieEntry.COLUMN_BACKDROP_URL
        };

        Cursor c = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null  // The sort order
        );
        List<String> mThumbIds=new ArrayList<String>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        while (c.moveToNext()){
            MovieItem newmovie = new MovieItem();
            newmovie.id= Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_ID)));
            newmovie.title= c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE));
            newmovie.backdrop_url= c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_BACKDROP_URL));
            newmovie.genres= Arrays.asList(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_GENRE)).split(","));
            newmovie.overview= c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            try {
                newmovie.releasedate= formatter.parse(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RELEASEDATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newmovie.voteaverage= Double.parseDouble(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE)));
            movieItemList.add(newmovie);
            mThumbIds.add(String.valueOf(newmovie.id));
        }c.close();
            imageAdapter.updateList(mThumbIds,lastpref);
        movieposters.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myintent=new Intent(getActivity().getApplicationContext(),DetailsActivity.class);
                myintent.putExtra("movie_details", movieItemList.get(i));
                startActivity(myintent);
            }
        });
            mThumbIds=null;
    }

    @LayoutRes
    private int getFragmentLayoutId() {
        return R.layout.movie_posters_fragment;
    }

    public static MoviePostersFragment newInstance() {
        MoviePostersFragment fragment = new MoviePostersFragment();
        return fragment;
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void AlertMsg(){
        new AlertDialog.Builder(getActivity())
                .setTitle("No connection!")
                .setMessage("You need a valid internet connection to use the app.")
                .setNeutralButton("OK!", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();}
    }
