package com.example.ozefet.udacitymovies.Main.Models;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ozefet.udacitymovies.Main.Interfaces.TrailerAPI;
import com.example.ozefet.udacitymovies.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ozefet on 05/08/16.
 */
public class TrailerJsonDeserializer {

    public void JsonDeserializerTrailer(final View view, String url){
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.movie_info_layout);
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // prepare call in Retrofit 2.0
        TrailerAPI trailerAPI = retrofit.create(TrailerAPI.class);
        TrailerAPI apiService = retrofit.create(TrailerAPI.class);
        Call<TrailerResults> call = apiService.loadtrailers(url);
        //asynchronous call
        call.enqueue( new Callback<TrailerResults>(){

            @Override
            public void onResponse(Call<TrailerResults> call, final Response<TrailerResults> response) {
                for(int i=0;i< response.body().trailer_item_results.size();i++) {
                    Button myButton = new Button(view.getContext());
                    myButton.setText((response.body().trailer_item_results.get(i).name));
                    myButton.setBackgroundColor(Color.TRANSPARENT);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    Drawable img = view.getContext().getResources().getDrawable(android.R.drawable.ic_media_play);
                    myButton.setCompoundDrawablesWithIntrinsicBounds( null, img, null, null);
                    layout.addView(myButton, lp);
                    final int finalI = i;
                    myButton.setOnClickListener(new View.OnClickListener() {

                                                         @Override
                                                         public void onClick(View v1) {
                                                             view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+response.body().trailer_item_results.get(finalI).key)));

                                                         }

                                                     }
                        );
                    }
            }


            @Override
            public void onFailure(Call<TrailerResults> call, Throwable t) {
                String a="";
            }
        });
    }
}
