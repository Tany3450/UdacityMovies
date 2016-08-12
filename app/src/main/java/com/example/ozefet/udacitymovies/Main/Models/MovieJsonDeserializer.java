package com.example.ozefet.udacitymovies.Main.Models;

import com.example.ozefet.udacitymovies.Main.Interfaces.MovieAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ozefet on 04/08/16.
 */


public class MovieJsonDeserializer {

    public interface Listener {
        void onCompleteMovieList(List<MovieItem> movieItems);
    }

    private Listener listener;

    public MovieJsonDeserializer(Listener listener) {
        this.listener = listener;
    }

    public void JsonDeserializerMovie(String url) {

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return df.parse(json.getAsString());
                } catch (ParseException e) {
                    return null;
                }
            }
        }).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        MovieAPI apiService = retrofit.create(MovieAPI.class);
        Call<MovieResults> call = apiService.loadmovies(url);
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, final Response<MovieResults> response) {

                listener.onCompleteMovieList(response.body().movie_item_results);
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                listener.onCompleteMovieList(null);
            }
        });
    }
}
