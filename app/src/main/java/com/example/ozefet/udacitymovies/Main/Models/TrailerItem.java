package com.example.ozefet.udacitymovies.Main.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ozefet on 04/08/16.
 */
public class TrailerItem implements Parcelable {
    @SerializedName("key")
    public String key;
    @SerializedName("name")
    public String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.name);
    }

    protected TrailerItem(Parcel in) {
        this.key = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<TrailerItem> CREATOR = new Parcelable.Creator<TrailerItem>() {
        @Override
        public TrailerItem createFromParcel(Parcel source) {
            return new TrailerItem(source);
        }

        @Override
        public TrailerItem[] newArray(int size) {
            return new TrailerItem[size];
        }
    };
}
