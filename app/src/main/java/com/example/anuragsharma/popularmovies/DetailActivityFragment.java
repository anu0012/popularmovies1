package com.example.anuragsharma.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by anuragsharma on 19/04/16.
 */
public class DetailActivityFragment extends Fragment{

    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    TextView movie_title, release, rating, overview;
    ImageView poster,bg;
    MovieDetails mDetails;
    public DetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_details, container, false);

        Intent intent = getActivity().getIntent();
        mDetails = (MovieDetails) intent.getSerializableExtra("movies_details");
        Log.v(LOG_TAG, mDetails.getOriginal_title());
        movie_title = (TextView)rootview.findViewById(R.id.movie_title_text_view);

        release = (TextView)rootview.findViewById(R.id.release_date_text_view);
        rating = (TextView)rootview.findViewById(R.id.rating_text_view);
        overview = (TextView)rootview.findViewById(R.id.overview_text_view);
        poster = (ImageView)rootview.findViewById(R.id.poster_image_view);
        bg=(ImageView)rootview.findViewById(R.id.movie_poster);

        movie_title.setText(mDetails.getOriginal_title());
        String temp[] = mDetails.getRelease_date().split("-");
        release.setText(temp[0]);
        rating.setText(mDetails.getVote_average()+"/10");



        overview.setText(mDetails.getOverview());
        String path = mDetails.getPoster_path();
        final String IMAGE_BASE = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w185/";
        final String IMAGE_URL = path;
        Log.v(LOG_TAG, "Image Url" + IMAGE_URL);
        String url = IMAGE_BASE+IMAGE_SIZE+IMAGE_URL;
        Picasso.with(getActivity()).load(url).into(poster);

        String backurl=back.backg(mDetails);
        Picasso.with(getActivity()).load(backurl).into(bg);


        return rootview;


    }
}
