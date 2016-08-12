package com.example.ozefet.udacitymovies.Main.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ozefet.udacitymovies.Main.Activities.DetailsActivity;
import com.example.ozefet.udacitymovies.Main.Models.MainPagerAdapter;
import com.example.ozefet.udacitymovies.Main.Models.MovieItem;
import com.example.ozefet.udacitymovies.Main.Models.RecyclerViewAdapter;
import com.example.ozefet.udacitymovies.Main.Models.TrailerItem;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieContract;
import com.example.ozefet.udacitymovies.Main.MovieDB.MovieDbHelper;
import com.example.ozefet.udacitymovies.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ozefet on 26/07/16.
 */
public class DetailsFragment extends Fragment {
    Boolean isFavorited = false;
    SQLiteDatabase db;
    private MovieItem selected_movieItem;
    MenuItem mitem;
    ImageView imageView;
    String[] localgenreids = {"28", "12", "16", "35", "80", "99", "18", "10751", "14", "10769", "36", "27", "10402", "9648", "10749", "878", "10770", "53", "10752", "37"};
    String[] localgenrenames = {"Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Foreign", "History", "Horror", "Music", "Mystery", "Romance", "Science Fiction", "TV Movie", "Thriller", "War", "Western"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(getFragmentLayoutId(), container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mitem = (MenuItem) menu.findItem(R.id.favorite_button);
        mitem.setVisible(true);
        if (isFavorited) {
            mitem.setTitle(getResources().getString(R.string.favorited));
            mitem.setIcon(R.drawable.favorited);
        } else {
            mitem.setTitle(getResources().getString(R.string.mark_as_favorite));
            mitem.setIcon(R.drawable.mark_as_favorite);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @LayoutRes
    private int getFragmentLayoutId() {
        return R.layout.details_fragment;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        selected_movieItem = getArguments().getParcelable("selectedMovie");
        imageView = (ImageView) view.findViewById(R.id.poster_image);
        if (selected_movieItem.backdrop_url != null) {
            imageView = (ImageView) view.findViewById(R.id.poster_image);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500/" + selected_movieItem.backdrop_url).into(imageView, new Callback() {

                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    imageView.setVisibility(View.GONE);
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }
        TextView mtext = (TextView) view.findViewById(R.id.genre_text);
        if (getArguments().getBoolean("isFavorited")) {
            isFavorited = true;
        }

        SimilarMoviesUI(view);
        if (selected_movieItem.genres.size() != 0) {
            for (int i = 0; i < selected_movieItem.genres.size(); i++) {
                mtext.setText(mtext.getText() + ", " + localgenrenames[Arrays.asList(localgenreids).indexOf(selected_movieItem.genres.get(i))]);
            }
            mtext.setText(mtext.getText().toString().substring(2, mtext.getText().length()));
        }

        ((DetailsActivity) getActivity()).setActionBarTitle(selected_movieItem.title);
        mtext = (TextView) view.findViewById(R.id.year_text);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        if (selected_movieItem.releasedate != null) {
            cal.setTime(selected_movieItem.releasedate);
            mtext.setText(String.valueOf(cal.get(java.util.Calendar.YEAR)));
        } else {
            mtext.setText("????");
        }

        mtext = (TextView) view.findViewById(R.id.vote_text);
        int lenght = String.valueOf(selected_movieItem.voteaverage).length();
        final SpannableStringBuilder sb = new SpannableStringBuilder(String.valueOf(selected_movieItem.voteaverage) + "/10");
        final ForegroundColorSpan fcs;
        if (selected_movieItem.voteaverage > 8) {
            fcs = new ForegroundColorSpan(Color.rgb(0, 153, 0));
        } else if (selected_movieItem.voteaverage > 6) {
            fcs = new ForegroundColorSpan(Color.rgb(255, 204, 0));
        } else if (selected_movieItem.voteaverage > 4) {
            fcs = new ForegroundColorSpan(Color.rgb(255, 102, 0));
        } else {
            fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
        }
        sb.setSpan(fcs, 0, lenght, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mtext.setText(sb);
        mtext = (TextView) view.findViewById(R.id.summary_text);
        mtext.setText(selected_movieItem.overview);
        final List<TrailerItem> trailerItems = getArguments().getParcelableArrayList("trailerItems");
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.movie_info_layout);
        if (trailerItems == null || trailerItems.size() == 0) {
            final TextView title_text = (TextView) view.findViewById(R.id.videos_title);
            title_text.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < trailerItems.size(); i++) {
                Button myButton = new Button(view.getContext());
                myButton.setText((trailerItems.get(i).name));
                myButton.setBackgroundColor(Color.TRANSPARENT);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                Drawable img = view.getContext().getResources().getDrawable(android.R.drawable.ic_media_play);
                myButton.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                layout.addView(myButton, lp);
                final int finalI = i;
                myButton.setOnClickListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v1) {
                                                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerItems.get(finalI).key)));

                                                }

                                            }
                );
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_button:
                if (!isFavorited) {
                    MovieDbHelper mDbHelper = new MovieDbHelper(getContext());
                    db = mDbHelper.getWritableDatabase();
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(selected_movieItem.releasedate);
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry.COLUMN_ID, selected_movieItem.id);
                    values.put(MovieContract.MovieEntry.COLUMN_TITLE, selected_movieItem.title);
                    values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, selected_movieItem.overview);
                    values.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, String.valueOf(cal.get(java.util.Calendar.YEAR)));
                    values.put(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE, selected_movieItem.voteaverage);
                    values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, selected_movieItem.backdrop_url);
                    values.put(MovieContract.MovieEntry.COLUMN_GENRE, TextUtils.join(",", selected_movieItem.genres));
                    db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                    ContextWrapper cw = new ContextWrapper(getActivity());
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    File myPath = new File(directory.getAbsolutePath(), selected_movieItem.id + ".png");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(myPath);
                        final FileOutputStream finalFos = fos;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    downloadImage("http://image.tmdb.org/t/p/w185" + selected_movieItem.poster_url, finalFos);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mitem.setTitle(getResources().getString(R.string.favorited));
                    isFavorited = true;
                    mitem.setIcon(R.drawable.favorited);
                } else {
                    mitem.setIcon(R.drawable.mark_as_favorite);
                    isFavorited = false;
                    mitem.setTitle(getResources().getString(R.string.mark_as_favorite));
                    String selection = MovieContract.MovieEntry.COLUMN_ID + "='" + selected_movieItem.id + "'";
                    MovieDbHelper mDbHelper = new MovieDbHelper(getContext());
                    db = mDbHelper.getWritableDatabase();
                    db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, null);
                    ContextWrapper cw = new ContextWrapper(getActivity());
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    new File(directory.getAbsolutePath(), selected_movieItem.id + ".png").delete();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static DetailsFragment newInstance() {
        Bundle args = new Bundle();
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void downloadImage(String downloadUrl, FileOutputStream fos) {
        try {
            URL url = new URL(downloadUrl);

            URLConnection ucon = url.openConnection();
            InputStream inputStream = null;
            HttpURLConnection httpConn = (HttpURLConnection) ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            inputStream = httpConn.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
            }

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void SimilarMoviesUI(View view) {
        final List<MovieItem> similarMovieItems = getArguments().getParcelableArrayList("similarMovies");
        if (similarMovieItems == null || similarMovieItems.size() <= 1) {
            final TextView title_text = (TextView) getActivity().findViewById(R.id.similar_posters_title);
            title_text.setVisibility(View.GONE);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view_similar);
            recyclerView.setVisibility(View.GONE);

        } else {
            List<String> mThumbIds = new ArrayList<String>();

            for (int i = 0; i < similarMovieItems.size(); i++) {

                if (similarMovieItems.get(i).id != selected_movieItem.id) {
                    mThumbIds.add("http://image.tmdb.org/t/p/w185/" + similarMovieItems.get(i).poster_url);
                } else {
                    similarMovieItems.remove(i);
                    i--;
                }
            }

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view_similar);

            RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(mThumbIds, view, 3);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerViewAdapter.RecyclerTouchListener(view.getContext(), recyclerView, new RecyclerViewAdapter.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent myintent = new Intent(getContext().getApplicationContext(), DetailsActivity.class);
                    myintent.putExtra("movie_details", similarMovieItems.get(position));
                    myintent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    getContext().startActivity(myintent);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));


        }


    }


}