package com.mkd.popular.movies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by mkdin on 17-04-2016.
 */
public class MovieProvider extends ContentProvider {
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sMovieWithIdQueryBuilder;
    private static final String sMovieWithIdSelection =
            MovieDbContract.MoviesEntry.TABLE_NAME +
                    "." + MovieDbContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?";

    static {
        sMovieWithIdQueryBuilder = new SQLiteQueryBuilder();
    }

    MovieDbHelper mMovieDbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieDbContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieDbContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MovieDbContract.PATH_MOVIES + "/*", MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                returnCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieDbContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_WITH_ID:
                returnCursor = getMovieWithId(uri, projection, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return MovieDbContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieDbContract.MoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                long _id = db.insert(MovieDbContract.MoviesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieDbContract.MoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        if (selection == null) selection = "1";
        int numberOfRowsDeleted = 0;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                numberOfRowsDeleted = db.delete(
                        MovieDbContract.MoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
            }
            break;
            default:
                throw new UnsupportedOperationException("Not supported operation");
        }
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int numberOfRowsUpdated = 0;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                numberOfRowsUpdated = db.update(MovieDbContract.MoviesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("No operation SUpported for " + uri);
        }
        if (numberOfRowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return numberOfRowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieDbContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getMovieWithId(
            Uri uri, String[] projection, String sortOrder) {
        long movieId = MovieDbContract.MoviesEntry.getMovieIdFromUri(uri);

        return mMovieDbHelper.getReadableDatabase().query(
                MovieDbContract.MoviesEntry.TABLE_NAME,
                projection,
                sMovieWithIdSelection,
                new String[]{Long.toString(movieId)},
                null,
                null,
                sortOrder
        );
    }
}
