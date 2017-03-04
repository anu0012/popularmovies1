package com.example.anuragsharma.popularmovies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.example.anuragsharma.popularmovies.data.MovieContract;
import com.example.anuragsharma.popularmovies.data.MovieDbHelper;

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
    static GridViewAdapter gridViewAdapter;
    static int index=0;
    private GridView gridView;
    private ProgressDialog pDialog;
    private static final String KEY = "movie_list";
    private boolean isRetained = false;
    private ArrayList<MovieDetails> movieList;
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
        setRetainInstance(true);
        if(savedInstanceState == null || !savedInstanceState.containsKey(KEY)) {
            //Create a new ArrayList based on the array of parcelable MovieHolder object.
            movieList = new ArrayList<>();
        } else {
            //Restore the movie list if it was saved.
            movieList = savedInstanceState.getParcelableArrayList(KEY);
            isRetained = true;
        }
    }

    @Override
    public void onPause() {
        index = gridView.getFirstVisiblePosition();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        gridView.setSelection(index);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelableArrayList(KEY, movieList);
        super.onSaveInstanceState(state);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        final ArrayList<MovieDetails> mArr = new ArrayList<>();
        gridViewAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item, mArr);
        gridView = (GridView) rootview.findViewById(R.id.movies_grid);

        int orientation=this.getResources().getConfiguration().orientation;
        if(orientation== Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(4);
            for (MovieDetails md:movieList){
                gridViewAdapter.add(md);
            }
        }
        //updateMovies(MainActivity.s);
        gridView.setAdapter(gridViewAdapter);

        //Toast.makeText(getActivity(), index+"", Toast.LENGTH_SHORT).show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!isTablet(getActivity()))
                {
                    Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("movies_details", mArr.get(position));
                    startActivity(intent);
                }
                else{
                    Bundle bundle = new Bundle();

                    bundle.putParcelable("movies_details",mArr.get(position));
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    DetailActivityFragment fragment = DetailActivityFragment.newInstance(bundle);
                    fragmentTransaction.add(R.id.detail_activity_fragment, fragment).commit();
                }

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

        if (id == R.id.action_fav) {
            MainActivity.s="favorite";
            //MovieDbHelper movieDbHelper = new MovieDbHelper(getActivity());
            gridViewAdapter.clear();
            //ArrayList<MovieDetails> movieDetails = movieDbHelper.queryAllMoviesEntries();
            Cursor c = getActivity().getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,new String[]{
                    MovieContract.FavoriteEntry.COLUMN_TITLE,
                    MovieContract.FavoriteEntry.COLUMN_POSTER,
                    MovieContract.FavoriteEntry.COLUMN_OVERVIEW,
                    MovieContract.FavoriteEntry.COLUMN_RATING,
                    MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE,
                    MovieContract.FavoriteEntry.COLUMN_MOVIE_ID
            },null,null,null);

            if(c.getCount() > 0)
            {
                ArrayList<MovieDetails> movieDetails = new ArrayList<>();
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    MovieDetails m = new MovieDetails(c.getString(0),c.getString(1),c.getString(2),c.getString(3),
                            c.getString(4),c.getLong(5));
                    movieDetails.add(m);
                    c.moveToNext();
                }
                new FetchMovie().onPostExecute(movieDetails);
            }


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
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
        }

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
                        .appendQueryParameter(APPID_PARAM, "bf7cbd994b59a5faeb281c7d0f07029d")     //put api here
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
            final String ID = "id";


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
                        single_movie.getString(RELEASE_DATE),
                        single_movie.getLong(ID)
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
            pDialog.dismiss();
        }
    }


}

