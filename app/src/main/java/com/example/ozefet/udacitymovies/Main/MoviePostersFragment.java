package com.example.ozefet.udacitymovies.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.ozefet.udacitymovies.Main.MovieData.MovieContract;
import com.example.ozefet.udacitymovies.Main.MovieData.MovieDbHelper;
import com.example.ozefet.udacitymovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MoviePostersFragment extends Fragment {
    ImageAdapter imageAdapter;
    List<Movie> movieList= new ArrayList<Movie>();
    SharedPreferences settings;
    String lastpref;
    GridView movieposters;

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        String currentsetting=settings.getString(("sort_order_list"), "");
        if(lastpref!=null&&!lastpref.equals(currentsetting)){
            if ("2".equals(currentsetting)){
                imageAdapter= new ImageAdapter(getActivity());movieposters.setAdapter(imageAdapter); movieList.clear();GetLocalMovies();}
            else if(isOnline()){
                imageAdapter= new ImageAdapter(getActivity());movieposters.setAdapter(imageAdapter); movieList.clear(); new RetrieveMovies().execute();}
            else{AlertMsg();}
                                            }
        //Favoriler seçiliyken favorilerden çıkarma.
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
        if ("2".equals(settings.getString(("sort_order_list"), ""))){GetLocalMovies();}
        else if(isOnline()){new RetrieveMovies().execute();}
        else{AlertMsg();}
        movieposters.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView img=(ImageView) view;
                img.buildDrawingCache();
                Bitmap bmap = img.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                movieList.get(i).poster_imagedata=stream.toByteArray();
                Intent myintent=new Intent(getActivity().getApplicationContext(),DetailsActivity.class);
                myintent.putExtra("movie_details", movieList.get(i));
                startActivity(myintent);
            }
        });
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
                String.valueOf(MovieContract.MovieEntry.COLUMN_POSTER),
                MovieContract.MovieEntry.COLUMN_RELEASEDATE,
                MovieContract.MovieEntry.COLUMN_VOTEAVERAGE
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
        List<byte[]> mThumbIds=new ArrayList<byte[]>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        while (c.moveToNext()){
            Movie newmovie = new Movie();
            newmovie.id= Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_ID)));
            newmovie.title= c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE));
            newmovie.overview= c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            byte[] contentByte= c.getBlob(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
            mThumbIds.add(contentByte);
            try {
                newmovie.releasedate= formatter.parse(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RELEASEDATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newmovie.voteaverage= Double.parseDouble(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE)));
            movieList.add(newmovie);
        }c.close();
            imageAdapter.updateList_local(mThumbIds,lastpref);
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
    public class RetrieveMovies extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings)  {
            JSONObject json = null;
            JSONArray results= null;
            lastpref = settings.getString(("sort_order_list"), "");
            if (lastpref.equals("")){
                lastpref ="0";}
            String URI=null;
            if (lastpref.equals("0")) {URI= "http://api.themoviedb.org/3/movie/popular?api_key="+getString(R.string.api_key);}
            else if(lastpref.equals("1")){URI= "http://api.themoviedb.org/3/movie/top_rated?api_key="+getString(R.string.api_key);}
            try {
                json = JsonReader.readJsonFromUrl(URI);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            try {
                results = json.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        protected void onPostExecute(JSONArray results) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            List<String> mThumbIds=new ArrayList<String>();
            for (int i = 0; i < results.length(); ++i) {
                Movie newmovie = new Movie();
                JSONObject rec = null;
                try {
                    rec = results.getJSONObject(i);
                    newmovie.isadult=rec.getBoolean("adult");
                    newmovie.poster_url =rec.getString("poster_path");
                    newmovie.overview=rec.getString("overview");
                    newmovie.releasedate=df.parse(rec.getString("release_date"));
                    newmovie.id=rec.getInt("id");
                    newmovie.title=rec.getString("title");
                    newmovie.popularity=rec.getDouble("popularity");
                    newmovie.votecount=rec.getInt("vote_count");
                    newmovie.voteaverage=rec.getDouble("vote_average");
                    movieList.add(newmovie);
                    mThumbIds.add("http://image.tmdb.org/t/p/w185/"+newmovie.poster_url);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
            imageAdapter.updateList(mThumbIds,lastpref);
        }

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
