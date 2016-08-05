package com.example.ozefet.udacitymovies.Main.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by ozefet on 21/07/16.
 */
public class MovieItem implements Parcelable {
    @SerializedName("adult")
    public Boolean isadult;

    @SerializedName("poster_path")
    public String poster_url;

    @SerializedName("backdrop_path")
    public String backdrop_url;

    @SerializedName("overview")
    public String overview;

    @SerializedName("release_date")
    public Date releasedate;

    @SerializedName("id")
    public int id;

    @SerializedName("original_title")
    public String title;

    @SerializedName("popularity")
    public double popularity;

    @SerializedName("vote_count")
    public int votecount;

    @SerializedName("vote_average")
    public double voteaverage;

    @SerializedName("genre_ids")
    public List<String> genres;

    public MovieItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isadult);
        dest.writeString(this.poster_url);
        dest.writeString(this.backdrop_url);
        dest.writeString(this.overview);
        dest.writeLong(this.releasedate != null ? this.releasedate.getTime() : -1);
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeDouble(this.popularity);
        dest.writeInt(this.votecount);
        dest.writeDouble(this.voteaverage);
        dest.writeStringList(this.genres);
    }

    protected MovieItem(Parcel in) {
        this.isadult = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.poster_url = in.readString();
        this.backdrop_url = in.readString();
        this.overview = in.readString();
        long tmpReleasedate = in.readLong();
        this.releasedate = tmpReleasedate == -1 ? null : new Date(tmpReleasedate);
        this.id = in.readInt();
        this.title = in.readString();
        this.popularity = in.readDouble();
        this.votecount = in.readInt();
        this.voteaverage = in.readDouble();
        this.genres = in.createStringArrayList();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}