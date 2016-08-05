package com.example.ozefet.udacitymovies.Main.Interfaces;

import com.example.ozefet.udacitymovies.Main.Models.MovieResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by ozefet on 04/08/16.
 */
public interface MovieAPI {
   @GET
   Call<MovieResults> loadmovies(@Url String url);
}