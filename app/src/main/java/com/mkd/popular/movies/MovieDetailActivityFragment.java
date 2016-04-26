package com.mkd.popular.movies;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mkd.popular.movies.bean.Movies;
import com.mkd.popular.movies.bean.Reviews;
import com.mkd.popular.movies.bean.YoutubeLinks;
import com.mkd.popular.movies.constant.MovieConstant;
import com.mkd.popular.movies.customAdapter.ReviewsAdapter;
import com.mkd.popular.movies.customAdapter.VideosAdapter;
import com.mkd.popular.movies.customlistview.NonScrollListView;
import com.mkd.popular.movies.data.MovieDbContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    public static final String MOVIE_DETAIL_PARCEL_KEY = "MovieDetailParcel";
    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieDbContract.MoviesEntry.COLUMN_TRAILER_1
    };
    private static final String LOCAL_POSTER_PATH_BASE = Environment.getExternalStorageDirectory().getPath()
            + "/" + MovieConstant.LOCAL_POSTER_PATH_BASE + "/";
    private static final String MOVIE_ID_WHERE = MovieDbContract.MoviesEntry.COLUMN_MOVIE_ID + "=?";
    private static final String COLUMN_VIDEOS[] = {MovieDbContract.MoviesEntry.COLUMN_TRAILER_1};
    private static final int COL_VIDEO_POS = 0;
    String poster;
    String title;
    double vote;
    String synopsis;
    String releaseDate;
    String youtubeKey;
    long movieId;
    Uri mUri;
    Movies movie;
    TextView txtTitle, txtVote, txtSynopsis, txtReleaseDate, txtEmptyView, mTxtReviewsEmptyPlaceHolder,
    mTxtVideosEmptyPlaceHolder;
    ImageView imagePoster, favoriteMovie;
    ProgressBar mVideosProgressBar, mReviewsProgressBar;
    NonScrollListView reviewsListView, videosListView;
    ReviewsAdapter mReviewsAdapter;
    ArrayList<Reviews> mReviewsList;
    VideosAdapter mVideosAdapter;
    ArrayList<YoutubeLinks> mVideosList;
    ShareActionProvider mShareActionProvider;
    boolean mRequestVideosUrlInsert = false;
    String mShareContent = "";
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

            File mFolder = new File(Environment.getExternalStorageDirectory() + "/" + MovieConstant.LOCAL_POSTER_PATH_BASE);
            Log.v(LOG_TAG, "PATH: " + Environment.getExternalStorageDirectory() + "/" + MovieConstant.LOCAL_POSTER_PATH_BASE);
            if (!mFolder.exists()) {
                Log.i(LOG_TAG, "New Folder Create:" + mFolder.mkdir());
            }
            File file = new File(mFolder, movieId + ".jpg");
            try {
                Log.i(LOG_TAG, "New File Creation:" + file.createNewFile());
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.flush();
                ostream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    public MovieDetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);
        // Get the menu item.
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(!Utility.isOnline(getActivity()) && movie != null) {
            mShareContent = String.format(getResources().getString(R.string.share_content),
                    movie.getTitle(), (mVideosList.size() != 0)
                            ? (MovieConstant.TRAILER_BASE_URL + mVideosList.get(0).getKey())
                            : "<NOT AVAILABLE>");
            mShareActionProvider.setShareIntent(createActionShare());
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        View rootView;
        if (args != null) {
            movie = args.getParcelable(MovieDetailActivityFragment.MOVIE_DETAIL_PARCEL_KEY);

            rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            txtTitle = (TextView) rootView.findViewById(R.id.detail_title_text);
            txtVote = (TextView) rootView.findViewById(R.id.release_vote_text);
            txtSynopsis = (TextView) rootView.findViewById(R.id.release_synopsis_text);
            txtReleaseDate = (TextView) rootView.findViewById(R.id.release_date_text);
            imagePoster = (ImageView) rootView.findViewById(R.id.poster_detail_image);
            favoriteMovie = (ImageView) rootView.findViewById(R.id.favorite_image);
            mTxtReviewsEmptyPlaceHolder = (TextView) rootView.findViewById(R.id.reviewsEmptyPlaceHolder);
            mTxtVideosEmptyPlaceHolder = (TextView) rootView.findViewById(R.id.trailersEmptyPlaceHolder);
            reviewsListView = (NonScrollListView) rootView.findViewById(R.id.reviews_list_view);
            videosListView = (NonScrollListView) rootView.findViewById(R.id.videos_list_view);

            mVideosProgressBar = (ProgressBar) rootView.findViewById(R.id.videosprogressBar);
            mReviewsProgressBar = (ProgressBar) rootView.findViewById(R.id.reviewsprogressBar);

            if (savedInstanceState == null) {
                mReviewsList = new ArrayList<>();
                mVideosList = new ArrayList<>();
            } else {
                if (savedInstanceState.containsKey(MovieConstant.REVIEWS_KEY))
                    mReviewsList = savedInstanceState.getParcelableArrayList(MovieConstant.REVIEWS_KEY);
                else
                    mReviewsList = new ArrayList<>();

                if (savedInstanceState.containsKey(MovieConstant.VIDEOS_KEY))
                    mVideosList = savedInstanceState.getParcelableArrayList(MovieConstant.VIDEOS_KEY);
                else
                    mVideosList = new ArrayList<>();
            }
            mReviewsAdapter = new ReviewsAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    mReviewsList);
            mVideosAdapter = new VideosAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    mVideosList);
            reviewsListView.setAdapter(mReviewsAdapter);
            videosListView.setAdapter(mVideosAdapter);

            favoriteMovie.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    performDbOperation();
                }
            });

            videosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    YoutubeLinks trailer = (YoutubeLinks) parent.getItemAtPosition(position);
                    playYoutubeVideo(trailer.getKey());
                }
            });


            if (movie != null) {
                if (Utility.isOnline(getActivity())) {
                    startRetrievingTask(new LoadTrailerAndReviewsDetails(),
                            movie.getMovieId());
                } else {
                    mTxtReviewsEmptyPlaceHolder.setText(getResources().getString(R.string.reviews_unavailable));
                    mTxtReviewsEmptyPlaceHolder.setVisibility(View.VISIBLE);
                    Cursor c = getActivity().getContentResolver().query(
                            MovieDbContract.MoviesEntry.buildMovieUri(movie.getMovieId()),
                            MOVIE_DETAIL_COLUMNS,
                            null,
                            null,
                            null
                    );

                    if (c != null && c.moveToFirst()) {
                        if( c.getString(COL_VIDEO_POS) != null) {
                            String tokens[] = c.getString(COL_VIDEO_POS).split("_");

                            YoutubeLinks links = new YoutubeLinks();

                            links.setName(tokens[0]);
                            links.setKey(tokens[1]);
                            mVideosList.add(links);
                            c.close();
                        } else {
                            mTxtVideosEmptyPlaceHolder.setText(getResources().getString(R.string.no_trailers));
                            mTxtVideosEmptyPlaceHolder.setVisibility(View.VISIBLE);
                        }
                    }
                }
                setDetails(movie);

            }

        } else {
            rootView = inflater.inflate(R.layout.empty_detail, container, false);
            txtEmptyView = (TextView) rootView.findViewById(R.id.empty_text_placeholder);
            txtEmptyView.setText(getResources().getString(R.string.empty_detail_message));
        }


        return rootView;
    }

    private void playYoutubeVideo(String videoId) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(MovieConstant.TRAILER_BASE_URL + videoId));
            startActivity(intent);
        }
    }

    private Intent createActionShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareContent);

        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mVideosList != null) {
            outState.putParcelableArrayList(MovieConstant.VIDEOS_KEY, mVideosList);
            outState.putParcelableArrayList(MovieConstant.REVIEWS_KEY, mReviewsList);
        }
        super.onSaveInstanceState(outState);
    }

    private void performDbOperation() {
        if (!checkFavoriteMovie(movieId)) {
            ContentValues movieDetails = new ContentValues();
            movieDetails.put(MovieDbContract.MoviesEntry.COLUMN_MOVIE_ID, movieId);
            movieDetails.put(MovieDbContract.MoviesEntry.COLUMN_TITLE, title);
            movieDetails.put(MovieDbContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieDetails.put(MovieDbContract.MoviesEntry.COLUMN_POSTER,
                    LOCAL_POSTER_PATH_BASE + movieId + ".jpg");
            movieDetails.put(MovieDbContract.MoviesEntry.COLUMN_VOTES, vote);
            movieDetails.put(MovieDbContract.MoviesEntry.COLUMN_PLOT, synopsis);
            if (youtubeKey != null && !youtubeKey.equalsIgnoreCase(""))
                movieDetails.put(MovieDbContract.MoviesEntry.COLUMN_TRAILER_1, youtubeKey);
            else
                mRequestVideosUrlInsert = true;

            getActivity().getContentResolver().insert(MovieDbContract.MoviesEntry.CONTENT_URI,
                    movieDetails);


            Picasso.with(getActivity()).load(poster).into(target);

            setFavoriteIcon(true);
        } else {
            getActivity().getContentResolver().delete(MovieDbContract.MoviesEntry.CONTENT_URI,
                    MOVIE_ID_WHERE,
                    new String[]{String.valueOf(movieId)});
            File deleteFile = new File(LOCAL_POSTER_PATH_BASE + movieId + ".jpg");
            if (!deleteFile.delete())
                Log.e(LOG_TAG, "File deleting failed!");

            setFavoriteIcon(false);
        }
    }


    private void setDetails(Movies movie) {

        poster = movie.getPosterURL();
        title = movie.getTitle();
        vote = movie.getVoteAverage();
        synopsis = movie.getPlotSynopsis();
        releaseDate = movie.getDate();
        movieId = movie.getMovieId();
        if (movieId != -1)
            mUri = MovieDbContract.MoviesEntry.buildMovieUri(movieId);

        txtTitle.setText(title);
        txtReleaseDate.setText(releaseDate);
        txtSynopsis.setText(synopsis);
        txtVote.setText(String.format(getContext().getString(R.string.format_votes), vote));
        setFavoriteIcon(checkFavoriteMovie(movieId));

        Picasso.with(getActivity())
                .load(poster)
                .placeholder(R.drawable.ic_image_place_holder)
                .error(R.drawable.ic_error_place_holder)
                .into(imagePoster);


    }

    private void setFavoriteIcon(boolean set) {
        if (set)
            favoriteMovie.setImageResource(R.drawable.ic_favorite_black_36dp);
        else
            favoriteMovie.setImageResource(R.drawable.ic_favorite_border_black_36dp);
    }

    private boolean checkFavoriteMovie(long movieId) {

        Cursor c = getActivity().getContentResolver().query(
                mUri,
                MOVIE_DETAIL_COLUMNS,
                MovieDbContract.MoviesEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(movieId)},
                null);
        boolean isFavorite = (c != null && c.moveToFirst());
        if (c != null) c.close();
        return isFavorite;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void startRetrievingTask(LoadTrailerAndReviewsDetails multiLoad, long movieId) {
        if (multiLoad != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                multiLoad.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        movieId);
            else
                multiLoad.execute(movieId);
        }
    }

    public class LoadTrailerAndReviewsDetails extends AsyncTask<Long, Void, Void> {
        private final String LOG_TAG = LoadTrailerAndReviewsDetails.class.getSimpleName();
        String videosJSON;
        String reviewJSON;

        @Override
        protected void onPreExecute() {
            mReviewsProgressBar.setVisibility(View.VISIBLE);
            mVideosProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Long... params) {

            long movieId = params[0];
            try {
                Uri buildVideosUri = Uri.parse(MovieConstant.MOVIES_BASE_URL + movieId + "/videos?").buildUpon()
                        .appendQueryParameter(MovieConstant.API_KEY_PARAM, BuildConfig.MOVIES_DB_API_KEY)
                        .build();
                Uri buildReviewsUri = Uri.parse(MovieConstant.MOVIES_BASE_URL + movieId + "/reviews?").buildUpon()
                        .appendQueryParameter(MovieConstant.API_KEY_PARAM, BuildConfig.MOVIES_DB_API_KEY)
                        .build();
                URL videosUrl = new URL(buildVideosUri.toString());
                URL reviewsUrl = new URL(buildReviewsUri.toString());
                videosJSON = Utility.getJsonFromAPI(videosUrl);
                reviewJSON = Utility.getJsonFromAPI(reviewsUrl);

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(isAdded()) {
                mReviewsProgressBar.setVisibility(View.GONE);
                mVideosProgressBar.setVisibility(View.GONE);
                if (videosJSON != null)
                    parseVideosJson(videosJSON);
                if (reviewJSON != null)
                    parseReviewsJson(reviewJSON);
            }
        }

        private void parseVideosJson(String videos) {
            try {
                JSONObject response = new JSONObject(videos);
                JSONArray videoUrlArray = response.getJSONArray("results");
                YoutubeLinks links;
                if (videoUrlArray.length() == 0) {
                    mTxtVideosEmptyPlaceHolder.setText(getResources().getString(R.string.no_trailers));
                    mTxtVideosEmptyPlaceHolder.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < videoUrlArray.length(); i++) {
                    JSONObject trailerJson = videoUrlArray.getJSONObject(i);
                    links = new YoutubeLinks();
                    links.setKey(trailerJson.getString("key"));
                    links.setName(trailerJson.getString("name"));
                    mVideosAdapter.add(links);
                    if (trailerJson.getString("type").equalsIgnoreCase("Trailer")
                            && (youtubeKey == null || youtubeKey.equalsIgnoreCase("")))
                        youtubeKey = links.getName() + "_" + links.getKey();
                }
                if (mRequestVideosUrlInsert) {
                    ContentValues value = new ContentValues();
                    value.put(MovieDbContract.MoviesEntry.COLUMN_TRAILER_1, youtubeKey);
                    getActivity().getContentResolver().update(MovieDbContract.MoviesEntry.CONTENT_URI,
                            value, MOVIE_ID_WHERE, new String[]{String.valueOf(movieId)});
                }

                mShareContent = String.format(getResources().getString(R.string.share_content),
                        movie.getTitle(),
                        (mVideosList.size() != 0)
                                ? MovieConstant.TRAILER_BASE_URL + mVideosAdapter.getItem(0).getKey()
                                : "<NOT AVAILABLE>" );
                if(mShareActionProvider != null)
                    mShareActionProvider.setShareIntent(createActionShare());
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
        }

        private void parseReviewsJson(String reviews) {
            try {
                JSONObject response = new JSONObject(reviews);
                JSONArray reviewsArray = response.getJSONArray("results");
                Reviews review;
                Log.v(LOG_TAG, "length: " + reviewsArray.length());
                if (reviewsArray.length() == 0) {
                    mTxtReviewsEmptyPlaceHolder.setText(getResources().getString(R.string.no_reviews));
                    mTxtReviewsEmptyPlaceHolder.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject reviewJson = reviewsArray.getJSONObject(i);
                    review = new Reviews();
                    review.setAuthor(reviewJson.getString("author"));
                    review.setContent(reviewJson.getString("content"));
                    mReviewsAdapter.add(review);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
        }
    }

}
