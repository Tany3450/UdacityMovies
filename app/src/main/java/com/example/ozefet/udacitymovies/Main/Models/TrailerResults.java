package com.example.ozefet.udacitymovies.Main.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ozefet on 04/08/16.
 */
public class TrailerResults {
    @SerializedName("results")
    List<TrailerItem> trailer_item_results;
}