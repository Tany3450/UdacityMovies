package com.example.ozefet.udacitymovies.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.ozefet.udacitymovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(lastpref !=null&&!lastpref.equals(settings.getString(("sort_order_list"), ""))){
            if(isOnline()){ new RetrieveMovies().execute();}
            else{AlertMsg();}
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(getFragmentLayoutId(), container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(isOnline()){new RetrieveMovies().execute();}
        else{AlertMsg();}

        GridView movieposters = (GridView) getActivity().findViewById(R.id.movieposters_gridview);
        movieposters.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myintent=new Intent(getActivity().getApplicationContext(),DetailsActivity.class);
                myintent.putExtra("movie_details", movieList.get(i));
                startActivity(myintent);
            }
        });
        imageAdapter= new ImageAdapter(getActivity());
        movieposters.setAdapter(imageAdapter);}

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
            settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            lastpref = settings.getString(("sort_order_list"), "");
            if (lastpref.equals("")){
                lastpref ="0";}
            String URI=null;
            if (lastpref.equals("0")) {URI= "http://api.themoviedb.org/3/movie/popular?api_key="+getString(R.string.api_key);}
            else if(lastpref.equals("1")){URI= "http://api.themoviedb.org/3/movie/top_rated?api_key="+getString(R.string.api_key);}
            try {
                json = JsonReader.readJsonFromUrl(URI);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                    newmovie.poster=rec.getString("poster_path");
                    newmovie.overview=rec.getString("overview");
                    newmovie.releasedate=df.parse(rec.getString("release_date"));
                    newmovie.id=rec.getInt("id");
                    newmovie.title=rec.getString("title");
                    newmovie.popularity=rec.getDouble("popularity");
                    newmovie.votecount=rec.getInt("vote_count");
                    newmovie.voteaverage=rec.getDouble("vote_average");
                    movieList.add(newmovie);
                    mThumbIds.add("http://image.tmdb.org/t/p/w185/"+newmovie.poster);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            imageAdapter.updateList(mThumbIds);
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
