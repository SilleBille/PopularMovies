package com.mkd.popular.movies;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mkd.popular.movies.bean.Movies;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String MOVIE_DETAIL_INTERMEDIATE_PARCEL_KEY = "MovieDetailActivityParcelKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(MovieDetailActivityFragment.MOVIE_DETAIL_PARCEL_KEY,
                    getIntent().getExtras().getParcelable(MOVIE_DETAIL_INTERMEDIATE_PARCEL_KEY));
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


}


