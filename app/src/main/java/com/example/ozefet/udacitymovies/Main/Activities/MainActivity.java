package com.example.ozefet.udacitymovies.Main.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.ozefet.udacitymovies.Main.Models.MainPagerAdapter;
import com.example.ozefet.udacitymovies.Main.Models.MovieItem;
import com.example.ozefet.udacitymovies.Main.Models.MovieJsonDeserializer;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieContract;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieDbHelper;
import com.example.ozefet.udacitymovies.Main.Settings.SettingsActivity;
import com.example.ozefet.udacitymovies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieJsonDeserializer.Listener {
    private List<List<MovieItem>> Sort_List = new ArrayList<List<MovieItem>>();
    private MainPagerAdapter mainPagerAdapter;
    private ViewPager viewPager;

    @Override
    public void onStart() {
        super.onStart();
        if (mainPagerAdapter != null) {
            GetLocalMovies();
            mainPagerAdapter.UpdateList(Sort_List);
            mainPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            public void callSearch(String query) {

                Intent myintent = new Intent(getApplicationContext(), SearchActivity.class);
                myintent.putExtra("search_URL", "http://api.themoviedb.org/3/search/movie?query=" + query + "&sort_by=popularity.desc&api_key=" + getApplicationContext().getString(R.string.api_key));
                myintent.putExtra("query", query);
                startActivity(myintent);
            }
        });

        MenuItem mItem = (MenuItem) menu.findItem(R.id.favorite_button);
        mItem.setVisible(false);
        mItem = (MenuItem) menu.findItem(R.id.search);
        mItem.setVisible(true);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void GetLocalMovies() {
        List<MovieItem> movieItemList = new ArrayList<MovieItem>();
        MovieDbHelper mDbHelper = new MovieDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        while (c.moveToNext()) {
            MovieItem newmovie = new MovieItem();
            newmovie.id = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_ID)));
            newmovie.title = c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE));
            newmovie.backdrop_url = c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_BACKDROP_URL));
            newmovie.genres = Arrays.asList(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_GENRE)).split(","));
            newmovie.overview = c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            try {
                newmovie.releasedate = formatter.parse(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RELEASEDATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newmovie.voteaverage = Double.parseDouble(c.getString(c.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE)));
            movieItemList.add(newmovie);
        }
        if (Sort_List.size() == 3) {
            Sort_List.set(2, movieItemList);
        } else {
            Sort_List.add(movieItemList);
        }
        c.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            MovieJsonDeserializer movieJsonDeserializer = new MovieJsonDeserializer(this);
            movieJsonDeserializer.JsonDeserializerMovie("http://api.themoviedb.org/3/movie/popular?api_key=" + getString(R.string.api_key));
        }
    }

    @Override
    public void onCompleteMovieList(final List<MovieItem> movieItems) {
        Sort_List.add(movieItems);

        //if popular movies have been added
        if (Sort_List.size() == 1) {
            MovieJsonDeserializer movieJsonDeserializer = new MovieJsonDeserializer(this);
            movieJsonDeserializer.JsonDeserializerMovie("http://api.themoviedb.org/3/movie/top_rated?api_key=" + getString(R.string.api_key));
            return;
        }

        //if popular and top rated movies have been added
        if (Sort_List.size() == 2) {

            GetLocalMovies();
            if (mainPagerAdapter == null) {
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                viewPager = (ViewPager) findViewById(R.id.pager);
                mainPagerAdapter = new MainPagerAdapter(Sort_List, this, getSupportFragmentManager());
                viewPager.setAdapter(mainPagerAdapter);
                viewPager.setCurrentItem(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(("sort_order_list"), "0")));
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setTabTextColors(ContextCompat.getColor(this, android.R.color.black), ContextCompat.getColor(this, android.R.color.white));
                tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.background_light));
                return;
            }
            mainPagerAdapter.UpdateList(Sort_List);
            mainPagerAdapter.notifyDataSetChanged();
        }
    }

}
