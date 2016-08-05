package com.example.ozefet.udacitymovies.Main.MovieDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                    MovieContract.MovieEntry.COLUMN_ID + " TEXT PRIMARY KEY, " +
                    MovieContract.MovieEntry.COLUMN_OVERVIEW+ TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_RELEASEDATE + TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_VOTEAVERAGE + TEXT_TYPE + COMMA_SEP+
                    MovieContract.MovieEntry.COLUMN_BACKDROP_URL + TEXT_TYPE + COMMA_SEP+
                    MovieContract.MovieEntry.COLUMN_GENRE + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}