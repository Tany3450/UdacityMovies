package com.example.ozefet.udacitymovies.Main.Models;

import com.example.ozefet.udacitymovies.Main.Interfaces.TrailerAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ozefet on 05/08/16.
 */
public class TrailerJsonDeserializer {

    public interface Listener {
        void onCompleteTrailer(List<TrailerItem> trailerItems);
    }

    private Listener listener;

    public TrailerJsonDeserializer(Listener listener) {
        this.listener = listener;
    }

    public void JsonDeserializerTrailer(String url) {
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        TrailerAPI apiService = retrofit.create(TrailerAPI.class);
        Call<TrailerResults> call = apiService.loadtrailers(url);
        call.enqueue(new Callback<TrailerResults>() {

            @Override
            public void onResponse(Call<TrailerResults> call, final Response<TrailerResults> response) {

                listener.onCompleteTrailer(response.body().trailer_item_results);
            }


            @Override
            public void onFailure(Call<TrailerResults> call, Throwable t) {

            }
        });
    }
}
