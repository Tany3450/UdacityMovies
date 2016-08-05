package com.example.ozefet.udacitymovies.Main.MovieDB;

import android.provider.BaseColumns;

/**
 * Created by ozefet on 28/07/16.
 */
public final class MovieContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MovieContract() {}

    /* Inner class that defines the table contents */
    public static final class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME="movies";
        public static final String COLUMN_GENRE="genres";
        public static final String COLUMN_OVERVIEW="summary";
        public static final String COLUMN_RELEASEDATE= "release_year";
        public static final String COLUMN_ID="movie_id";
        public static final String COLUMN_TITLE="movie_name";
        public static final String COLUMN_VOTEAVERAGE="average_vote";
        public static final String COLUMN_BACKDROP_URL="backdrop_url";
    }
}