package com.example.ozefet.udacitymovies.Main.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ozefet.udacitymovies.Main.Activities.DetailsActivity;
import com.example.ozefet.udacitymovies.Main.Models.MovieItem;
import com.example.ozefet.udacitymovies.Main.Models.MovieJsonDeserializer;
import com.example.ozefet.udacitymovies.Main.Models.TrailerJsonDeserializer;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieContract;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieDbHelper;
import com.example.ozefet.udacitymovies.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * Created by ozefet on 26/07/16.
 */
public class DetailsFragment extends Fragment {
    SharedPreferences settings;
    File directory;
    ContextWrapper cw;
    Boolean isfavorited=false;
    MovieDbHelper mDbHelper = new MovieDbHelper(getContext());
    SQLiteDatabase db;
    public MovieItem selected_movieItem;
    MenuItem mitem;
    ImageView imageView ;
    String[] localgenreids = {"28", "12", "16", "35", "80", "99", "18", "10751", "14", "10769", "36", "27", "10402", "9648", "10749", "878", "10770", "53", "10752", "37"};
    String[] localgenrenames = {"Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Foreign", "History", "Horror", "Music", "Mystery", "Romance", "Science Fiction", "TV Movie", "Thriller", "War", "Western"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // initUserInterface(container, savedInstanceState);
        // Inflate the layout for this fragment
        cw = new ContextWrapper(getActivity());
        directory = cw.getDir("imageDir", getActivity().MODE_PRIVATE);
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);
        mDbHelper = new MovieDbHelper(getContext());
        db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                MovieContract.MovieEntry.COLUMN_ID,
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
        selected_movieItem = getActivity().getIntent().getExtras().getParcelable("movie_details");
        //c.moveToFirst();
        while (c.moveToNext()&&!isfavorited){
             if(String.valueOf(selected_movieItem.id).equals(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_ID)))){
              isfavorited=true;}
        }c.close();//mDbHelper.close();
        return inflater.inflate(getFragmentLayoutId(), container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        mitem=(MenuItem) menu.findItem(R.id.favorite_button);
        mitem.setVisible(true);
        if (isfavorited) {
            mitem.setTitle(getResources().getString(R.string.favorited));
            mitem.setIcon(R.drawable.favorited);
        } else {
            mitem.setTitle(getResources().getString(R.string.mark_as_favorite));
            mitem.setIcon(R.drawable.mark_as_favorite);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    @LayoutRes
    private int getFragmentLayoutId() {
        return R.layout.details_fragment;
    }

//    private void initUserInterface(@NonNull  View view, @Nullable Bundle savedInstanceState) {
//        savedInstanceState.getChar("");
//    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        if(isOnline()){
            TextView mtext = (TextView) view.findViewById(R.id.genre_text);
            MovieJsonDeserializer movieJsonDeserializer =new MovieJsonDeserializer();
            TrailerJsonDeserializer trailerJsonDeserializer =new TrailerJsonDeserializer();
            trailerJsonDeserializer.JsonDeserializerTrailer(view,"http://api.themoviedb.org/3/movie/"+selected_movieItem.id+"/videos?api_key=8843b049a06411f051b8cc5857095472");
            String genreids = "";
            for(int i=0;i<selected_movieItem.genres.size();i++){genreids+=selected_movieItem.genres.get(i)+",";
            mtext.setText(mtext.getText()+", "+ localgenrenames[Arrays.asList(localgenreids).indexOf(selected_movieItem.genres.get(i))]);}
            mtext.setText(mtext.getText().toString().substring(2,mtext.getText().length()));
            genreids = genreids.substring(0, genreids.length()-1);
           movieJsonDeserializer.JsonDeserializerMovie(1,view,selected_movieItem.id,"discover/movie?with_genres="+genreids+"&sort_by=popularity.desc&api_key=8843b049a06411f051b8cc5857095472");
    }
        else {}

        ((DetailsActivity) getActivity()).setActionBarTitle(selected_movieItem.title);
        TextView mtext = (TextView) view.findViewById(R.id.year_text);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(selected_movieItem.releasedate);
        mtext.setText(String.valueOf(cal.get(java.util.Calendar.YEAR)));
        mtext = (TextView) view.findViewById(R.id.vote_text);
        int lenght=String.valueOf(selected_movieItem.voteaverage).length();
        final SpannableStringBuilder sb = new SpannableStringBuilder(String.valueOf(selected_movieItem.voteaverage)+"/10");
        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs ;
        if(selected_movieItem.voteaverage>8){fcs = new ForegroundColorSpan(Color.rgb(0, 153, 0));}
        else if(selected_movieItem.voteaverage>6){fcs = new ForegroundColorSpan(Color.rgb(255, 204, 0));}
        else if(selected_movieItem.voteaverage>4){fcs = new ForegroundColorSpan(Color.rgb(255, 102, 0));}
        else {fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));}
        // Set the text color for first 4 characters
        sb.setSpan(fcs, 0, lenght, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mtext.setText(sb);
        mtext = (TextView) view.findViewById(R.id.summary_text);
        mtext.setText(selected_movieItem.overview);

        imageView=(ImageView)view.findViewById(R.id.poster_image);
        String currentsetting=settings.getString(("sort_order_list"), "");
//        if ("2".equals(currentsetting)){
//            File f=new File(directory.getAbsolutePath(), selected_movieItem.id+".png");
//            Picasso.with(getActivity()).load(f).into(imageView);
//        }
       // else{
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500/"+ selected_movieItem.backdrop_url).into(imageView);
    //}
        }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
            {
                switch (item.getItemId()) {
                    case  R.id.favorite_button:
                if(!isfavorited){
                mDbHelper = new MovieDbHelper(getContext());
                // Gets the data repository in write mode
                db = mDbHelper.getWritableDatabase();
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(selected_movieItem.releasedate);
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_ID, selected_movieItem.id);
                values.put(MovieContract.MovieEntry.COLUMN_TITLE, selected_movieItem.title);
                values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, selected_movieItem.overview);
                values.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, String.valueOf(cal.get(java.util.Calendar.YEAR)));
                values.put(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE, selected_movieItem.voteaverage);
                    values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, selected_movieItem.backdrop_url);
                values.put(MovieContract.MovieEntry.COLUMN_GENRE, TextUtils.join(",", selected_movieItem.genres));

                db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);


                    // Create imageDir
                    File mypath=new File(directory.getAbsolutePath(), selected_movieItem.id+".png");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mypath);
                        downloadImage("http://image.tmdb.org/t/p/w185/"+selected_movieItem.poster_url,fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mitem.setTitle(getResources().getString(R.string.favorited));
                isfavorited=true;
                mitem.setIcon(R.drawable.favorited);
                }
                else {
                    mitem.setIcon(R.drawable.mark_as_favorite);
                    isfavorited=false;mitem.setTitle(getResources().getString(R.string.mark_as_favorite));
                    // Define 'where' part of query.
                    String selection = MovieContract.MovieEntry.COLUMN_ID + "='"+ selected_movieItem.id+"'";
                    // Issue SQL statement.
                    db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, null);
                    new File(directory.getAbsolutePath(), selected_movieItem.id+".png").delete();
                }
                       // mDbHelper.close();
                return true;
                    default:
                        return super.onOptionsItemSelected(item);
            }}


    public static DetailsFragment newInstance() {
        Bundle args = new Bundle();
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void downloadImage(String downloadUrl, FileOutputStream fos)
    {
        try
        {
            URL url = new URL(downloadUrl);

                /* Open a connection */
            URLConnection ucon = url.openConnection();
            InputStream inputStream = null;
            HttpURLConnection httpConn = (HttpURLConnection)ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            inputStream = httpConn.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ( (bufferLength = inputStream.read(buffer)) >0 )
            {
                fos.write(buffer, 0, bufferLength);
            }

            fos.close();
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }
}