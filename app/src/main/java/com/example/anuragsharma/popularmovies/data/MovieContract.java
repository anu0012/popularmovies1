package com.example.anuragsharma.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.anuragsharma.popularmovies.data.MovieContract.FavoriteEntry.TABLE_NAME;

/**
 * Created by Anurag on 27/2/2017.
 */
public class MovieContract {
    public MovieContract(){}

    public static final String DATABASE_NAME = "fav_movies.db";
    public static final int DATABASE_VERSION = 1;
    public static final String CONTENT_AUTHORITY = "com.example.anuragsharma.popularmovies.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_TITLE= "title";
        public static final String COLUMN_POSTER = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING= "rating";
        public static final String COLUMN_RELEASE_DATE= "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static Uri buildFavoriteMoviesWithMovieIdUri(long movieId){
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
    }
}
