package com.mkd.popular.movies.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mkdin on 11-03-2016.
 * title, release date, movie poster, vote average, and plot synopsis.
 */
public class Movies implements Parcelable {
    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
    private String posterURL;
    private String title;
    private String date;
    private double voteAverage;
    private String plotSynopsis;
    private long movieId;

    public Movies() {

    }

    private Movies(Parcel in) {
        posterURL = in.readString();
        title = in.readString();
        date = in.readString();
        voteAverage = in.readDouble();
        plotSynopsis = in.readString();
        movieId = in.readLong();
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterURL);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeDouble(voteAverage);
        dest.writeString(plotSynopsis);
        dest.writeLong(movieId);
    }
}
