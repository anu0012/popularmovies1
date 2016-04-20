package com.example.anuragsharma.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by anuragsharma on 15/04/16.
 */
public class MainActivityFragment extends Fragment {
    GridViewAdapter gridViewAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        updateMovies(MainActivity.s);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        final ArrayList<MovieDetails> mArr = new ArrayList<>();
        gridViewAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item, mArr);
        GridView gridView = (GridView) rootview.findViewById(R.id.movies_grid);
        int orientation=this.getResources().getConfiguration().orientation;
        if(orientation== Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(4);
        }
        updateMovies(MainActivity.s);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("movies_details", mArr.get(position));
                ;
                startActivity(intent);

            }
        });

    return rootview;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_popular) {
            MainActivity.s="popular";
            FetchMovie fetchMovie = new FetchMovie();
            fetchMovie.execute("popular");
            return true;
        }

        if (id == R.id.action_rated) {
            MainActivity.s="top_rated";
            FetchMovie fetchMovie = new FetchMovie();

            fetchMovie.execute("top_rated");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    public void updateMovies(String str){
        FetchMovie fetchMovie=new FetchMovie();

        fetchMovie.execute(str);
    }

    class FetchMovie extends AsyncTask<String, Void, ArrayList<MovieDetails>>{
        final String LOG=FetchMovie.class.getSimpleName();

        @Override
        protected ArrayList<MovieDetails> doInBackground(String... params) {

            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String movieJson = null;

            try{
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String SORT_ORDER = params[0];
                final String APPID_PARAM= "api_key";

                Uri buildUri= Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(SORT_ORDER)
                        .appendQueryParameter(APPID_PARAM, "API_KEY")     //put api here
                        .build();


                URL url=null;
                try{
                    url=new URL(buildUri.toString());
                    Log.v(LOG, url.toString());
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }

                urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream is=urlConnection.getInputStream();
                StringBuffer sb=new StringBuffer();

                if(is==null)
                    return null;

                reader=new BufferedReader(new InputStreamReader(is));
                String s;
                while((s=reader.readLine())!=null){
                    sb.append(s+"\n");
                }

                if(sb.length()==0)
                    return null;

                movieJson=sb.toString();

            }catch (IOException e){
                e.printStackTrace();
                return null;
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getMovieData(movieJson);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        public ArrayList<MovieDetails> getMovieData(String movieJson) throws JSONException {

            final String RESULTS="results";
            final String ORIGINAL_TITLE="original_title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";


            JSONObject movie = new JSONObject(movieJson);
            JSONArray results=movie.getJSONArray(RESULTS);

            ArrayList<MovieDetails> moviearray =new ArrayList<>();

            for(int i=0;i<results.length();i++)
            {
                JSONObject single_movie=results.getJSONObject(i);
                MovieDetails mdetails =new MovieDetails(
                        single_movie.getString(ORIGINAL_TITLE),
                        single_movie.getString(POSTER_PATH),
                        single_movie.getString(OVERVIEW),
                        single_movie.getString(VOTE_AVERAGE),
                        single_movie.getString(RELEASE_DATE)
                );
                Log.v(LOG,mdetails.getPoster_path());
                moviearray.add(mdetails);
            }

            return moviearray;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieDetails> movieDetaisls){
            if(movieDetaisls!=null){

                gridViewAdapter.clear();
                for (MovieDetails md:movieDetaisls){
                    gridViewAdapter.add(md);
                }


            }
            super.onPostExecute(movieDetaisls);
        }
    }


}

