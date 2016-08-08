package com.example.ozefet.udacitymovies.Main.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.ozefet.udacitymovies.Main.Fragments.MoviePostersFragment;
import com.example.ozefet.udacitymovies.Main.Models.MovieJsonDeserializer;
import com.example.ozefet.udacitymovies.Main.Settings.SettingsActivity;
import com.example.ozefet.udacitymovies.R;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {
                //callSearch(newText);}
                return true;
            }

            public void callSearch(String query) {
                MovieJsonDeserializer movieJsonDeserializer =new MovieJsonDeserializer();
                movieJsonDeserializer.JsonDeserializerMovie(0,findViewById(android.R.id.content),0, "http://api.themoviedb.org/3/search/movie?query="+query+"&sort_by=popularity.desc&api_key=8843b049a06411f051b8cc5857095472");}
        });

        MenuItem mitem=(MenuItem) menu.findItem(R.id.favorite_button);
        mitem.setVisible(false);
        mitem=(MenuItem) menu.findItem(R.id.search);
        mitem.setVisible(true);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // if (findViewById(R.id.fragment_container_movie_posters) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            // Create a new Fragment to be placed in the activity layout
            Fragment moviePostersFragment= MoviePostersFragment.newInstance();
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_movie_posters, moviePostersFragment).commit();
    }
}