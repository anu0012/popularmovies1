package com.example.anuragsharma.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.anuragsharma.popularmovies.MovieDetails;

import java.util.ArrayList;

/**
 * Created by Anurag on 27/2/17.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " LONG";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE_ACTIVITIES = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME +
            " (" + MovieContract.FavoriteEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP
            + MovieContract.FavoriteEntry.COLUMN_POSTER + TEXT_TYPE + COMMA_SEP
            + MovieContract.FavoriteEntry.COLUMN_OVERVIEW + TEXT_TYPE + COMMA_SEP
            + MovieContract.FavoriteEntry.COLUMN_RATING + TEXT_TYPE + COMMA_SEP
            + MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE + TEXT_TYPE + COMMA_SEP
            + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + INTEGER_TYPE + ")";

    public MovieDbHelper(Context context) {
        super(context, MovieContract.DATABASE_NAME, null, MovieContract.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ACTIVITIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }


    public void insertMovie(MovieDetails movieDetails){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MovieContract.FavoriteEntry.COLUMN_TITLE,movieDetails.getOriginal_title());
        values.put(MovieContract.FavoriteEntry.COLUMN_POSTER,movieDetails.getPoster_path());
        values.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW,movieDetails.getOverview());
        values.put(MovieContract.FavoriteEntry.COLUMN_RATING,movieDetails.getVote_average());
        values.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE,movieDetails.getRelease_date());
        values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,movieDetails.getId());
        db.insert(MovieContract.FavoriteEntry.TABLE_NAME,null,values);
        db.close();
    }

    public void deleteMovie(MovieDetails movieDetails){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MovieContract.FavoriteEntry.TABLE_NAME, MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " =?",
                new String[]{String.valueOf(movieDetails.getId())});
        db.close();
    }

    public ArrayList<MovieDetails> queryAllMoviesEntries() {
        ArrayList<MovieDetails> movieArrayList = new ArrayList<>();
        final String SQL_QUERY_ALL_MOVIES = "SELECT * FROM " + MovieContract.FavoriteEntry.TABLE_NAME + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_QUERY_ALL_MOVIES, null);

        if (cursor.moveToFirst()) {
            do {
                MovieDetails movieDetails = new MovieDetails(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getLong(5));
                movieArrayList.add(movieDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return movieArrayList;
    }

    public boolean querySingleMoviesEntry(Long id) {
        //Boolean available = false;
        final String SQL_QUERY_MOVIE = "SELECT * FROM " + MovieContract.FavoriteEntry.TABLE_NAME +
                " WHERE " + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = '" + id + "';";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_QUERY_MOVIE, null);

        if (cursor.moveToFirst()) {
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

}
