package com.example.ozefet.udacitymovies.Main;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by ozefet on 21/07/16.
 */
public class Movie implements Parcelable {
    Boolean isadult;
    String poster;
    String overview;
    Date releasedate;
    int id;
    String title;
    double popularity;
    int votecount;
    double voteaverage;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isadult);
        dest.writeString(this.poster);
        dest.writeString(this.overview);
        dest.writeSerializable(this.releasedate);
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeDouble(this.popularity);
        dest.writeInt(this.votecount);
        dest.writeDouble(this.voteaverage);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.isadult = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.poster = in.readString();
        this.overview = in.readString();
        this.releasedate = (Date) in.readSerializable();
        this.id = in.readInt();
        this.title = in.readString();
        this.popularity = in.readDouble();
        this.votecount = in.readInt();
        this.voteaverage = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}