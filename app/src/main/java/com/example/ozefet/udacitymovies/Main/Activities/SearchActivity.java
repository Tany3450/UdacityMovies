package com.example.ozefet.udacitymovies.Main.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.ozefet.udacitymovies.Main.Models.MovieItem;
import com.example.ozefet.udacitymovies.Main.Models.MovieJsonDeserializer;
import com.example.ozefet.udacitymovies.Main.Models.RecyclerViewAdapter;
import com.example.ozefet.udacitymovies.Main.Settings.SettingsActivity;
import com.example.ozefet.udacitymovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ozefet on 11/08/16.
 */
public class SearchActivity extends AppCompatActivity implements MovieJsonDeserializer.Listener {
    ActionBar actionBar;
    private static final int PAGE_ID_NORMAL = 0;
    private static final int ROW_NUMBER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_poster_fragment);
        Intent intent = getIntent();
        String URL = intent.getStringExtra("search_URL");
        String query = intent.getStringExtra("query");
        setActionBarTitle(this.getApplicationContext().getString(R.string.search_results_title) + " \"" + query + "\"");
        MovieJsonDeserializer movieJsonDeserializer = new MovieJsonDeserializer(this);
        movieJsonDeserializer.JsonDeserializerMovie(URL);

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
    public void onCompleteMovieList(final List<MovieItem> movieItems) {
        List<String> mThumbIds = new ArrayList<String>();
        for (int i = 0; i < movieItems.size(); i++) {
            if (movieItems.get(i).poster_url != null) {
                mThumbIds.add("http://image.tmdb.org/t/p/w185" + movieItems.get(i).poster_url);
            } else {
                movieItems.remove(i);
                i--;
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(mThumbIds, this.findViewById(android.R.id.content), PAGE_ID_NORMAL);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, ROW_NUMBER);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewAdapter.RecyclerTouchListener(this, recyclerView, new RecyclerViewAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent myintent = new Intent(view.getContext().getApplicationContext(), DetailsActivity.class);
                myintent.putExtra("movie_details", movieItems.get(position));
                view.getContext().startActivity(myintent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
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
}
