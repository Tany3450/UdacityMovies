package com.example.ozefet.udacitymovies.Main.Models;

/**
 * Created by ozefet on 09/08/16.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ozefet.udacitymovies.Main.Fragments.MoviePostersFragment;
import com.example.ozefet.udacitymovies.R;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_OF_TABS = 3;
    private List<List<MovieItem>> SortList;
    private String popularTitle;
    private String topRatedTitle;
    private String favoritedTitle;

    public MainPagerAdapter(List<List<MovieItem>> SortList, Context context, FragmentManager fm) {
        super(fm);
        popularTitle = context.getApplicationContext().getString(R.string.popular);
        topRatedTitle = context.getApplicationContext().getString(R.string.topRated);
        favoritedTitle = context.getApplicationContext().getString(R.string.favorited);
        this.SortList = SortList;
    }

    public void UpdateList(List<List<MovieItem>> SortList) {
        this.SortList = SortList;
    }

    @Override
    public Fragment getItem(int position) {

        return MoviePostersFragment.newInstance((ArrayList<MovieItem>) this.SortList.get(position), position);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return NUM_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return popularTitle;
        }

        if (position == 1) {
            return topRatedTitle;
        }

        return favoritedTitle;
    }
}