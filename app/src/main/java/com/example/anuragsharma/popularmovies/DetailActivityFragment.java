package com.example.anuragsharma.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuragsharma.popularmovies.data.MovieContract;
import com.example.anuragsharma.popularmovies.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anuragsharma on 19/04/16.
 */
public class DetailActivityFragment extends Fragment implements TrailerAsyncTask.Event,ReviewAsyncTask.Event{

    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;
    TextView movie_title, release, rating, overview;
    ImageView poster,bg;
    MovieDetails mDetails;
    FloatingActionButton fav_button;
    MovieDbHelper movieDbHelper;
    boolean isFav;
    public DetailActivityFragment() {
    }

    public static DetailActivityFragment newInstance(Bundle bundle){
        DetailActivityFragment fragment = new DetailActivityFragment();
        fragment.setArguments(bundle);
        Log.v(DetailActivityFragment.class.getSimpleName(), "argument set");
        return fragment;
    }
    View rootview;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

    }
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_details, container, false);

        Intent intent = getActivity().getIntent();
        mDetails = (MovieDetails) intent.getParcelableExtra("movies_details");
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

        String backurl= Back.backg(mDetails);
        Picasso.with(getActivity()).load(backurl).into(bg);

        Cursor c = getActivity().getContentResolver().query(MovieContract.FavoriteEntry.buildFavoriteMoviesWithMovieIdUri(mDetails.getId()),new String[]{
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
                MovieContract.FavoriteEntry.COLUMN_POSTER
        }, "movie_id =?",new String[]{mDetails.getId()+""},null);
        if(c.getCount() >0)
        {
            isFav = true;
        }
        else
        isFav = false;
        //MovieDetails movie = getActivity().getIntent().getParcelableExtra("movies_details");
        //movieDbHelper = new MovieDbHelper(getActivity());
         fav_button = (FloatingActionButton) rootview.findViewById(R.id.favourite_button);
        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isFav) {
                   // movieDbHelper.insertMovie(mDetails);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE,mDetails.getOriginal_title());
                    contentValues.put(MovieContract.FavoriteEntry.COLUMN_POSTER,mDetails.getPoster_path());
                    contentValues.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW,mDetails.getOverview());
                    contentValues.put(MovieContract.FavoriteEntry.COLUMN_RATING,mDetails.getVote_average());
                    contentValues.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE,mDetails.getRelease_date());
                    contentValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,mDetails.getId());
                    getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI,contentValues);
                    Toast.makeText(getActivity(), "Added to favorite", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //movieDbHelper.deleteMovie(mDetails);
                    getActivity().getContentResolver().delete(MovieContract.FavoriteEntry.buildFavoriteMoviesWithMovieIdUri(mDetails.getId()),null,null);
                    Toast.makeText(getActivity(), "Deleted from favorite", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new TrailerAsyncTask(this).execute(mDetails.getId());
        new ReviewAsyncTask(this).execute(mDetails.getId());

        return rootview;


    }

    @Override
    public void onTrailerLoaded(ArrayList<String> trailer) {

        LinearLayout ll = (LinearLayout) rootview.findViewById(R.id.trailer);
        int count = 1;
        if(trailer.size()!=0){
            setShareIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.get(0))));
        }

        for(String t:trailer){
            try {
                Button btn = new Button(getActivity());
                btn.setText("Trailer " + count);
                btn.setTag(R.string.trailerUrl, t);
                //btn.setBackgroundColor(Color.MAGENTA);
                //btn.setTextColor(Color.WHITE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10,0,10,0);
                btn.setLayoutParams(params);
                count++;

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String t = (String)  v.getTag(R.string.trailerUrl);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + t)));
                    }
                });
                ll.addView(btn);
            }
            catch (Exception e){
                Log.e(LOG_TAG,"error in trailer button");
            }

        }

    }
    @Override
    public void onReviewsLoaded(ArrayList<String> reviews) {
        LinearLayout ll = (LinearLayout) rootview.findViewById(R.id.trailer);
        int i=1;
        try {
            TextView head = new TextView(getActivity());
            head.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            head.setText("REVIEWS");
            head.setPadding(0, 10, 0, 10);
            head.setTextColor(Color.BLACK);
            head.setTextSize(18);
            head.setGravity(0x11);
            ll.addView(head);
            for(String r:reviews){
                TextView tv = new TextView(getActivity());
                tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tv.setPadding(0, 10, 0, 10);
                tv.setText(i+". "+r);
                tv.setTextColor(Color.BLACK);
                ll.addView(tv);
                i++;
            }
        }
        catch (Exception e){
            Log.e(LOG_TAG,"ERROR in REVIEW textview");
        }

    }
}
