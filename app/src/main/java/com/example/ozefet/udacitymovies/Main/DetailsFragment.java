package com.example.ozefet.udacitymovies.Main;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ozefet.udacitymovies.Main.MovieData.MovieContract;
import com.example.ozefet.udacitymovies.Main.MovieData.MovieDbHelper;
import com.example.ozefet.udacitymovies.R;

/**
 * Created by ozefet on 26/07/16.
 */
public class DetailsFragment extends Fragment {
    Boolean isfavorited=false;
    MovieDbHelper mDbHelper = new MovieDbHelper(getContext());
    SQLiteDatabase db;
    Movie selected_movie;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // initUserInterface(container, savedInstanceState);
        // Inflate the layout for this fragment

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
        selected_movie = getActivity().getIntent().getExtras().getParcelable("movie_details");
        //c.moveToFirst();
        while (c.moveToNext()&&!isfavorited){
             if(String.valueOf(selected_movie.id).equals(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_ID)))){
              isfavorited=true;}
        }c.close();
        return inflater.inflate(getFragmentLayoutId(), container, false);
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
        final Button mbutton=(Button) view.findViewById(R.id.favorite_button);
        mbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!isfavorited){
                mDbHelper = new MovieDbHelper(getContext());
                // Gets the data repository in write mode
                db = mDbHelper.getWritableDatabase();

                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(selected_movie.releasedate);
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_ID, selected_movie.id);
                values.put(MovieContract.MovieEntry.COLUMN_TITLE, selected_movie.title);
                values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, selected_movie.overview);
                values.put(MovieContract.MovieEntry.COLUMN_POSTER, selected_movie.poster_imagedata);
                values.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, String.valueOf(cal.get(java.util.Calendar.YEAR)));
                values.put(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE, selected_movie.voteaverage);

                db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                mbutton.setText(getResources().getString(R.string.favorited));mbutton.setBackgroundColor(Color.parseColor("#DDFF03"));
                isfavorited=true;

                }
                else {
                    isfavorited=false;mbutton.setText(getResources().getString(R.string.mark_as_favorite));mbutton.setBackgroundColor(Color.parseColor("#D3D3D3"));
                    // Define 'where' part of query.
                    String selection = MovieContract.MovieEntry.COLUMN_ID + "='"+selected_movie.id+"'";
                    // Issue SQL statement.
                    db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, null);

                }
            }
        });
        if(isfavorited){
            mbutton.setText(getResources().getString(R.string.favorited));mbutton.setBackgroundColor(Color.parseColor("#DDFF03"));}
        TextView mtext = (TextView) view.findViewById(R.id.movie_title_text_view);
        mtext.setText("  "+selected_movie.title);
        mtext = (TextView) view.findViewById(R.id.year_text);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(selected_movie.releasedate);
        mtext.setText(String.valueOf(cal.get(java.util.Calendar.YEAR)));
        mtext = (TextView) view.findViewById(R.id.vote_text);
        mtext.setText(String.valueOf(selected_movie.voteaverage)+"/10");
        mtext = (TextView) view.findViewById(R.id.summary_text);
        mtext.setText(getResources().getString(R.string.summary)+": "+selected_movie.overview);
        Bitmap bmp = BitmapFactory.decodeByteArray(selected_movie.poster_imagedata, 0, selected_movie.poster_imagedata.length);
        ImageView imageView ;
        imageView=(ImageView)view.findViewById(R.id.poster_image);
        imageView.setImageBitmap(bmp);

    }

    public static DetailsFragment newInstance() {
        Bundle args = new Bundle();
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}