package com.mkd.popular.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import Constants.MovieConstant;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    String poster;
    String title;
    String vote;
    String synopsis;
    String releaseDate;

    TextView txtTitle, txtVote, txtSynopsis, txtReleaseDate;
    ImageView imagePoster;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();

        txtTitle = (TextView) rootView.findViewById(R.id.detail_title_text);
        txtVote = (TextView) rootView.findViewById(R.id.release_vote_text);
        txtSynopsis = (TextView) rootView.findViewById(R.id.release_synopsis_text);
        txtReleaseDate = (TextView) rootView.findViewById(R.id.release_date_text);
        imagePoster = (ImageView) rootView.findViewById(R.id.poster_detail_image);


        if (intent != null && intent.getStringExtra(MovieConstant.posterKey) != null) {

            setDetails(intent);
        }
        return rootView;
    }

    private void setDetails(Intent intent) {

        poster = intent.getStringExtra(MovieConstant.posterKey);
        title = intent.getStringExtra(MovieConstant.titleKey);
        vote = intent.getStringExtra(MovieConstant.voteKey);
        synopsis = intent.getStringExtra(MovieConstant.synopsis);
        releaseDate = intent.getStringExtra(MovieConstant.relaseDateKey);


        txtTitle.setText(title);
        txtReleaseDate.setText(releaseDate);
        txtSynopsis.setText(synopsis);
        txtVote.setText(vote+"/10");

        Picasso.with(getActivity()).load(poster).into(imagePoster);


    }
}
