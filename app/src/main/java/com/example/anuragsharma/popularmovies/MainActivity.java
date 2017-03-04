package com.example.anuragsharma.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.anuragsharma.popularmovies.data.MovieDbHelper;

import java.util.ArrayList;

import static com.example.anuragsharma.popularmovies.MainActivityFragment.gridViewAdapter;


public class MainActivity extends AppCompatActivity {


    public static String s="popular";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(s.equals("favorite"))
        {
            MovieDbHelper movieDbHelper = new MovieDbHelper(this);
            if(gridViewAdapter != null)
            gridViewAdapter.clear();
            if(movieDbHelper !=null) {
                ArrayList<MovieDetails> movieDetails = movieDbHelper.queryAllMoviesEntries();
                if(movieDetails!=null){

                    gridViewAdapter.clear();
                    for (MovieDetails md:movieDetails){
                        gridViewAdapter.add(md);
                    }
                }
            }
        }
    }
}
