package com.example.ozefet.udacitymovies.Main.Models;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ozefet.udacitymovies.Main.Activities.DetailsActivity;
import com.example.ozefet.udacitymovies.Main.Interfaces.MovieAPI;
import com.example.ozefet.udacitymovies.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ozefet on 04/08/16.
 */


public  class MovieJsonDeserializer {
    public void JsonDeserializerMovie(final int operationid, final View view, final int movieid, String url){
        final List<MovieItem> movieItemList=new ArrayList<MovieItem>();

            Gson gson = new GsonBuilder().create();
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
            // prepare call in Retrofit 2.0
            MovieAPI movieAPI = retrofit.create(MovieAPI.class);
            MovieAPI apiService = retrofit.create(MovieAPI.class);
            Call<MovieResults> call = apiService.loadmovies(url);
            //asynchronous call
            call.enqueue( new Callback<MovieResults>(){

                @Override
                public void onResponse(Call<MovieResults> call, final Response<MovieResults> response) {
                    LinearLayout layout=null; List<String> mThumbIds=new ArrayList<String>();
                    if (operationid==1){layout = (LinearLayout) view.findViewById(R.id.similar_posters);}
                    for(int i=0;i< response.body().movie_item_results.size();i++) {
                        if (operationid==0){movieItemList.add(response.body().movie_item_results.get(i)); mThumbIds.add("http://image.tmdb.org/t/p/w185"+response.body().movie_item_results.get(i).poster_url);}
                        else if(operationid==1){
                        if(response.body().movie_item_results.get(i).id!=movieid) {
                            movieItemList.add(response.body().movie_item_results.get(i));
                            ImageView imageView = new ImageView(view.getContext());
                            imageView.setId(i);
                            imageView.setPadding(5, 5, 5, 5);
                            Picasso.with(view.getContext()).load("http://image.tmdb.org/t/p/w185/" + response.body().movie_item_results.get(i).poster_url).resize(250, 350).into(imageView);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            layout.addView(imageView);
                            final int finalI = i;
                            imageView.setOnClickListener(new View.OnClickListener() {

                                                             @Override
                                                             public void onClick(View v1) {
                                                                 Intent myintent = new Intent(view.getContext().getApplicationContext(), DetailsActivity.class);
                                                                 myintent.putExtra("movie_details", movieItemList.get(finalI));
                                                                 view.getContext().startActivity(myintent);

                                                             }

                                                         }
                            );
                        }else {movieItemList.add(null);} }}
                    if(operationid==0){GridView movieposters = (GridView) view.findViewById(R.id.movieposters_gridview);
                        movieposters.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent myintent=new Intent(view.getContext().getApplicationContext(),DetailsActivity.class);
                                myintent.putExtra("movie_details", movieItemList.get(i));
                                view.getContext().startActivity(myintent);
                            }
                        });
                        ImageAdapter imageAdapter= new ImageAdapter(view.getContext());
                        movieposters.setAdapter(imageAdapter);
                        imageAdapter.updateList(mThumbIds,"0");
                    }
                }

                @Override
                public void onFailure(Call<MovieResults> call, Throwable t) {
                    String a="";
                }
            });
    }

}
