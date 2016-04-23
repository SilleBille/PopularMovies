package com.mkd.popular.movies;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mkd.popular.movies.bean.Movies;
import com.mkd.popular.movies.constant.MovieConstant;
import com.mkd.popular.movies.customAdapter.GridViewAdapter;
import com.mkd.popular.movies.customAdapter.GridViewCursorAdapter;
import com.mkd.popular.movies.data.MovieDbContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int COLUMN_MOVIE_ID = 0;
    public static final int COLUMN_MOVIE_POSTER = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_RELEASE_DATE = 3;
    public static final int COLUMN_PLOT = 4;
    public static final int COLUMN_VOTES = 5;
    public static final int COLUMN_TRAILER = 6;
    public static final int COLUMN_ID = 7;
    private static final int MOVIES_LOADER = 1432;
    private static final String MOVIES_COLUMNS[] = {
            MovieDbContract.MoviesEntry.COLUMN_MOVIE_ID,
            MovieDbContract.MoviesEntry.COLUMN_POSTER,
            MovieDbContract.MoviesEntry.COLUMN_TITLE,
            MovieDbContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MovieDbContract.MoviesEntry.COLUMN_PLOT,
            MovieDbContract.MoviesEntry.COLUMN_VOTES,
            MovieDbContract.MoviesEntry.COLUMN_TRAILER_1,
            MovieDbContract.MoviesEntry._ID
    };
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    boolean userTouched = false;
    private GridView mGridView;
    private GridViewAdapter moviesArrayAdapter;
    private GridViewCursorAdapter moviesCursorAdapter;
    private ProgressBar mProgressBar;
    private ArrayList<Movies> mGridData;

    public MainActivityFragment() {
    }

    private boolean isFavoriteList(String sortOrder) {
        return sortOrder.contentEquals(getResources().getStringArray(R.array.sort_value)[2]);
    }

    private void updateMovies(ArrayList<Movies> gridData) {
        String sortOrder = Utility.getSortOrder(getActivity());
        if (!isFavoriteList(sortOrder)) {
            moviesArrayAdapter = new GridViewAdapter(getActivity(), R.layout.poster_grid_layout, mGridData);
            mGridView.setAdapter(moviesArrayAdapter);
            if (gridData == null || gridData.isEmpty())
                new LoadMovieDetails().execute(sortOrder);
            else {
                mGridData = gridData;
                moviesArrayAdapter.setGridData(mGridData);
            }
        } else {
            moviesCursorAdapter = new GridViewCursorAdapter(getActivity(), null, 0);
            mGridView.setAdapter(moviesCursorAdapter);
            getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        if (!isFavoriteList(Utility.getSortOrder(getActivity())) && !Utility.isOnline(getActivity())) {
            rootView = inflater.inflate(R.layout.empty_detail, container, false);
            ((TextView) rootView.findViewById(R.id.empty_text_placeholder)).
                    setText(getResources().getString(R.string.not_online_not_favorite));
        } else {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mGridView = (GridView) rootView.findViewById(R.id.posterGrid);
            mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

            if (savedInstanceState == null || !savedInstanceState.containsKey(MovieConstant.MOVIES_KEY)) {
                mGridData = new ArrayList<>();
            } else {
                mGridData = savedInstanceState.getParcelableArrayList(MovieConstant.MOVIES_KEY);
            }
            updateMovies(mGridData);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movies clickedMovie = null;
                    if (!isFavoriteList(Utility.getSortOrder(getContext()))) {
                        clickedMovie = moviesArrayAdapter.getItem(position);
                    } else {
                        Cursor clickedCursor = (Cursor) parent.getItemAtPosition(position);
                        if (clickedCursor != null) {
                            clickedMovie = new Movies();
                            clickedMovie.setMovieId(clickedCursor.getLong(COLUMN_MOVIE_ID));
                            clickedMovie.setDate(clickedCursor.getString(COLUMN_RELEASE_DATE));
                            clickedMovie.setPlotSynopsis(clickedCursor.getString(COLUMN_PLOT));
                            clickedMovie.setPosterURL("file://" + clickedCursor.getString(COLUMN_MOVIE_POSTER));
                            clickedMovie.setTitle(clickedCursor.getString(COLUMN_TITLE));
                            clickedMovie.setVoteAverage(clickedCursor.getDouble(COLUMN_VOTES));
                        }
                    }
                    ((Callback) getActivity()).onItemSelected(clickedMovie);
                }

            });

        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mGridData != null)
            outState.putParcelableArrayList(MovieConstant.MOVIES_KEY, mGridData);
        super.onSaveInstanceState(outState);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri moviesEntryUri = MovieDbContract.MoviesEntry.CONTENT_URI;
        String sortOrder = MovieDbContract.MoviesEntry.COLUMN_RELEASE_DATE + " DESC";
        return new CursorLoader(getActivity(),
                moviesEntryUri,
                MOVIES_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesCursorAdapter.swapCursor(null);
    }

    public interface Callback {
        void onItemSelected(Movies movie);
    }

    public class LoadMovieDetails extends AsyncTask<String, Void, Movies[]> {
        private final String LOG_TAG = LoadMovieDetails.class.getSimpleName();
        private Movies[] movieList = null;

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            if (mGridData.size() > 0) {
                mGridData.clear();
                moviesArrayAdapter.setGridData(mGridData);
            }
        }

        @Override
        protected Movies[] doInBackground(String... params) {
            String moviesStringJSON;
            String sortOrder = params[0];
            try {
                Uri buildUri = Uri.parse(MovieConstant.MOVIES_BASE_URL + sortOrder + "/?").buildUpon()
                        .appendQueryParameter(MovieConstant.API_KEY_PARAM, BuildConfig.MOVIES_DB_API_KEY)
                        .build();
                URL url = new URL(buildUri.toString());
                moviesStringJSON = Utility.getJsonFromAPI(url);
                movieList = parseJson(moviesStringJSON);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(Movies[] movies) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (movies != null) {
                Collections.addAll(mGridData, movies);
                moviesArrayAdapter.setGridData(mGridData);
            } else
                Toast.makeText(getActivity(), "Failed to load movie info...", Toast.LENGTH_SHORT).show();
        }


        private Movies[] parseJson(String result) {
            Movies[] movieCollection = null;
            try {
                JSONObject response = new JSONObject(result);
                JSONArray moviesArray = response.getJSONArray("results");
                Movies movie;
                movieCollection = new Movies[moviesArray.length()];
                // title, release date, movie poster, vote average, and plot synopsis.
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieJson = moviesArray.getJSONObject(i);
                    movie = new Movies();
                    movie.setTitle(movieJson.getString("title"));
                    movie.setDate(movieJson.getString("release_date"));
                    movie.setPlotSynopsis(movieJson.getString("overview"));
                    movie.setVoteAverage(movieJson.getDouble("vote_average"));
                    movie.setPosterURL(MovieConstant.POSTER_BASE_URL +
                            MovieConstant.POSTER_SIZE + movieJson.getString("poster_path"));
                    movie.setMovieId((movieJson.getLong("id")));
                    movieCollection[i] = movie;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
            return movieCollection;
        }
    }
}
