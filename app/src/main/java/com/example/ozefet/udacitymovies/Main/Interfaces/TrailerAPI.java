package com.example.ozefet.udacitymovies.Main.Interfaces;

import com.example.ozefet.udacitymovies.Main.Models.TrailerResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by ozefet on 04/08/16.
 */
public interface TrailerAPI {
   // @GET("movie/278/videos?api_key=8843b049a06411f051b8cc5857095472")
   @GET
   Call<TrailerResults> loadtrailers(@Url String url);
}
