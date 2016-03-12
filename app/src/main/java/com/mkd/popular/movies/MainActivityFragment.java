package com.mkd.popular.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import Constants.MovieConstant;
import CustomAdapter.GridViewAdapter;
import MovieBean.Movies;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private GridView mGridView;
    private GridViewAdapter moviesArrayAdapter;
    private ProgressBar mProgressBar;
    private ArrayList<Movies> mGridData;

    public MainActivityFragment() {
    }



    private void updateMovies() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = preferences.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));
        new LoadMovieDetails().execute(sortOrder);
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.posterGrid);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mGridData = new ArrayList<>();
        moviesArrayAdapter = new GridViewAdapter(getActivity(), R.layout.poster_grid_layout, mGridData);

        mGridView.setAdapter(moviesArrayAdapter);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies clickedMovie = moviesArrayAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra(MovieConstant.posterKey, clickedMovie.getPosterURL())
                        .putExtra(MovieConstant.relaseDateKey, clickedMovie.getDate())
                        .putExtra(MovieConstant.synopsis, clickedMovie.getPlotSynopsis())
                        .putExtra(MovieConstant.titleKey, clickedMovie.getTitle())
                        .putExtra(MovieConstant.voteKey, clickedMovie.getVoteAverage());

                startActivity(detailIntent);

            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class LoadMovieDetails extends AsyncTask<String, Void, Movies[]> {
        private final String LOG_TAG = LoadMovieDetails.class.getSimpleName();
        private Movies[] movieList = null;

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            if(mGridData.size() >0 ) {
                mGridData.clear();
                moviesArrayAdapter.setGridData(mGridData);
            }
        }

        @Override
        protected Movies[] doInBackground(String... params) {

            HttpURLConnection urlConnection;
            BufferedReader reader;

            String moviesStringJSON;
            String sortOrder = params[0];
            try {


                Uri buildUri = Uri.parse(MovieConstant.MOVIES_BASE_URL  + sortOrder + "/?").buildUpon()
                        .appendQueryParameter(MovieConstant.API_KEY_PARAM, BuildConfig.MOVIES_DB_API_KEY)
                        .build();


                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesStringJSON = buffer.toString();
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
            }
            else
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
                    movie.setVoteAverage(String.valueOf(movieJson.getDouble("vote_average")));
                    movie.setPosterURL(MovieConstant.POSTER_BASE_URL +
                            MovieConstant.POSTER_SIZE + movieJson.getString("poster_path"));
                    movieCollection[i] = movie;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
            return  movieCollection;
        }
    }
}
