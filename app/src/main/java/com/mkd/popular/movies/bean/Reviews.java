package com.mkd.popular.movies.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mkdin on 22-04-2016.
 */
public class Reviews implements Parcelable {
    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };
    private String author;
    private String content;

    public Reviews() {

    }

    private Reviews(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }
}
