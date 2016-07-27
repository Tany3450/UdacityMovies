package com.example.ozefet.udacitymovies.Main;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ozefet.udacitymovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by ozefet on 26/07/16.
 */
public class DetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // initUserInterface(container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(getFragmentLayoutId(), container, false);
    }

    @LayoutRes
    private int getFragmentLayoutId() {
        return R.layout.details_fragment;
    }

//    private void initUserInterface(@NonNull  View view, @Nullable Bundle savedInstanceState) {
//        savedInstanceState.getChar("");
//    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        TextView mtext = (TextView) view.findViewById(R.id.movie_title_text_view);
        Movie selected_movie = getActivity().getIntent().getExtras().getParcelable("movie_details");
        mtext.setText("  "+selected_movie.title);
        mtext = (TextView) view.findViewById(R.id.year_text);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(selected_movie.releasedate);
        mtext.setText(String.valueOf(cal.get(java.util.Calendar.YEAR)));
        mtext = (TextView) view.findViewById(R.id.vote_text);
        mtext.setText(String.valueOf(selected_movie.voteaverage)+"/10");
        mtext = (TextView) view.findViewById(R.id.summary_text);
        mtext.setText("Summary: "+selected_movie.overview);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/"+selected_movie.poster).into((ImageView)view.findViewById(R.id.poster_image));
    }

    public static DetailsFragment newInstance() {
        Bundle args = new Bundle();
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}