package com.example.ozefet.udacitymovies.Main.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ozefet.udacitymovies.Main.Fragments.DetailsFragment;
import com.example.ozefet.udacitymovies.Main.Models.MovieItem;
import com.example.ozefet.udacitymovies.Main.Models.MovieJsonDeserializer;
import com.example.ozefet.udacitymovies.Main.Models.TrailerItem;
import com.example.ozefet.udacitymovies.Main.Models.TrailerJsonDeserializer;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieContract;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieDbHelper;
import com.example.ozefet.udacitymovies.Main.Settings.SettingsActivity;
import com.example.ozefet.udacitymovies.R;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements MovieJsonDeserializer.Listener, TrailerJsonDeserializer.Listener {
    ActionBar actionBar;
    private MovieItem selectedMovie;
    private Bundle data;
    private boolean noGenre = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        } else {
            data = new Bundle();
            selectedMovie = this.getIntent().getExtras().getParcelable("movie_details");
            data.putParcelable("selectedMovie", selectedMovie);
            if (selectedMovie.genres.size() != 0) {
                String genreIds = "";
                for (int i = 0; i < selectedMovie.genres.size(); i++) {
                    genreIds += selectedMovie.genres.get(i) + ",";
                }
                genreIds = genreIds.substring(0, genreIds.length() - 1);
                TrailerJsonDeserializer trailerJsonDeserializer = new TrailerJsonDeserializer(this);
                trailerJsonDeserializer.JsonDeserializerTrailer("http://api.themoviedb.org/3/movie/" + selectedMovie.id + "/videos?api_key=" + getString(R.string.api_key));
                isFavoritedMovie();
                MovieJsonDeserializer movieJsonDeserializer = new MovieJsonDeserializer(this);
                movieJsonDeserializer.JsonDeserializerMovie("discover/movie?with_genres=" + genreIds + "&sort_by=popularity.desc&api_key=" + getString(R.string.api_key));
            } else {
                noGenre = true;
                isFavoritedMovie();
                TrailerJsonDeserializer trailerJsonDeserializer = new TrailerJsonDeserializer(this);
                trailerJsonDeserializer.JsonDeserializerTrailer("http://api.themoviedb.org/3/movie/" + selectedMovie.id + "/videos?api_key=" + getString(R.string.api_key));
            }
        }
        setContentView(R.layout.activity_details);
        setupActionBar();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void isFavoritedMovie() {
        boolean isFavorited = false;
        MovieDbHelper mDbHelper;
        mDbHelper = new MovieDbHelper(this);
        SQLiteDatabase db;
        db = mDbHelper.getReadableDatabase();
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
        while (c.moveToNext() && !isFavorited) {
            if (String.valueOf(selectedMovie.id).equals(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_ID)))) {
                isFavorited = true;
            }
        }
        c.close();
        data.putBoolean("isFavorited", isFavorited);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setActionBarTitle(String title) {
        actionBar.setTitle(title);
    }

    @Override
    public void onCompleteMovieList(List<MovieItem> movieItems) {
        data.putParcelableArrayList("similarMovies", (ArrayList<? extends Parcelable>) movieItems);
        if (findViewById(R.id.fragment_container_movie_details) != null) {
            Fragment detailsFragment = DetailsFragment.newInstance();
            detailsFragment.setArguments(data);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_movie_details, detailsFragment).commit();
        }
    }

    @Override
    public void onCompleteTrailer(List<TrailerItem> trailerItems) {
        data.putParcelableArrayList("trailerItems", (ArrayList<? extends Parcelable>) trailerItems);
        if (findViewById(R.id.fragment_container_movie_details) != null && noGenre) {
            Fragment detailsFragment = DetailsFragment.newInstance();
            detailsFragment.setArguments(data);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_movie_details, detailsFragment).commit();
        }
    }
}
