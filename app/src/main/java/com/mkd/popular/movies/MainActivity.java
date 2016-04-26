package com.mkd.popular.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.mkd.popular.movies.bean.Movies;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{
    boolean mTwoPane, mUserTouched = false;
    private static final String DETAIL_FRAGMENT_TAG = "DetailMovieTag";
    private static final String MAIN_FRAGMENT_TAG = "MainMoviesTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MainActivityFragment mainActivityFragment = null;
        if (savedInstanceState == null) {
            mainActivityFragment = new MainActivityFragment();
        } else {
            mainActivityFragment = (MainActivityFragment)getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_movie_list,
                mainActivityFragment, MAIN_FRAGMENT_TAG).commit();

        if(findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container,
                                new MovieDetailActivityFragment(),
                                DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }



    }

    @Override
    public void onItemSelected(Movies movie) {
        if(mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailActivityFragment.MOVIE_DETAIL_PARCEL_KEY, movie);

            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,
                    fragment, DETAIL_FRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.MOVIE_DETAIL_INTERMEDIATE_PARCEL_KEY, movie);
            startActivity(intent);
        }
    }

    private void onSortChanged() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_movie_list,
                new MainActivityFragment()).commit();
        if(mTwoPane) {
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,
                    fragment, DETAIL_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_sort);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);


        int position = Arrays.asList(getResources().getStringArray(R.array.sort_value))
                .indexOf(Utility.getSortOrder(getApplicationContext()));

        spinner.setSelection(position);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mUserTouched = true;
                return false;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mUserTouched) {
                    String values[] = getResources().getStringArray(R.array.sort_value);
                    String selectedItem = values[position];
                    Utility.setSortOrder(getApplicationContext(), selectedItem);
                    onSortChanged();
                    Log.v("MainActivity", "itemSlected");
                    mUserTouched = false;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }
}
