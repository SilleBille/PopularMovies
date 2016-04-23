package com.mkd.popular.movies.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mkdin on 22-04-2016.
 */
public class YoutubeLinks implements Parcelable {
    public static final Parcelable.Creator<YoutubeLinks> CREATOR = new Parcelable.Creator<YoutubeLinks>() {
        public YoutubeLinks createFromParcel(Parcel in) {
            return new YoutubeLinks(in);
        }

        public YoutubeLinks[] newArray(int size) {
            return new YoutubeLinks[size];
        }
    };
    private String key;
    private String name;

    public YoutubeLinks() {

    }

    private YoutubeLinks(Parcel in) {
        key = in.readString();
        name = in.readString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
    }
}
