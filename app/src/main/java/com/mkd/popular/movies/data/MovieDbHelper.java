package com.mkd.popular.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mkd.popular.movies.bean.Movies;

/**
 * Created by mkdin on 17-04-2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " + MovieDbContract.MoviesEntry.TABLE_NAME + " (" +
                MovieDbContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieDbContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                MovieDbContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieDbContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieDbContract.MoviesEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MovieDbContract.MoviesEntry.COLUMN_VOTES + " REAL NOT NULL, " +
                MovieDbContract.MoviesEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                MovieDbContract.MoviesEntry.COLUMN_TRAILER_1 + " TEXT, " +

                " UNIQUE (" + MovieDbContract.MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDbContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
