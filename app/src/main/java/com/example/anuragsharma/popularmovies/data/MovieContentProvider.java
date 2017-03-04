package com.example.anuragsharma.popularmovies.data;

/**
 * Created by anuragsharma on 28/02/17.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MovieContentProvider extends ContentProvider{
    private static final int FAVORITE_MOVIES = 100;
    private static final int FAVORITE_MOVIES_WITH_ID = 101;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private MovieDbHelper MovieDbHelper;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.FavoriteEntry.TABLE_NAME, FAVORITE_MOVIES);
        uriMatcher.addURI(authority, MovieContract.FavoriteEntry.TABLE_NAME + "/#", FAVORITE_MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        MovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        switch(URI_MATCHER.match(uri)){
            case FAVORITE_MOVIES:
                if(false){

                }
                    else{
                        final SQLiteDatabase database = MovieDbHelper.getReadableDatabase();
                        return database.query(MovieContract.FavoriteEntry.TABLE_NAME, projections, null, null, null, null, null);
                    }
            case FAVORITE_MOVIES_WITH_ID:
                if(selection != null && selectionArgs != null && sortOrder != null){
                    throw new UnsupportedOperationException("only projections are allowed for " + uri);
                }else{
                    final SQLiteDatabase database = MovieDbHelper.getReadableDatabase();
                    return database.query(MovieContract.FavoriteEntry.TABLE_NAME, projections, MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "= ?", new String[]{String.valueOf(ContentUris.parseId(uri))}, null, null, null);
                }
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch(URI_MATCHER.match(uri)){
            case FAVORITE_MOVIES:
                final SQLiteDatabase database = MovieDbHelper.getWritableDatabase();
                long id = database.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                    database.close();
                    return MovieContract.FavoriteEntry.buildFavoriteMoviesWithMovieIdUri(id);
                }else{
                    throw new SQLException("Failed to insert row for " + uri);
                }
            case FAVORITE_MOVIES_WITH_ID:
                throw new UnsupportedOperationException("insert is not allowed for " + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if(selection != null || selectionArgs != null){
            throw new UnsupportedOperationException("selection arguments are not allowed for deletion on " + uri);
        }else{
            switch (URI_MATCHER.match(uri)){
                case FAVORITE_MOVIES:
                    throw new UnsupportedOperationException("deletion is not allowed for " + uri);
                case FAVORITE_MOVIES_WITH_ID:
                    final SQLiteDatabase database = MovieDbHelper.getWritableDatabase();
                    int answer = database.delete(MovieContract.FavoriteEntry.TABLE_NAME, MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                    getContext().getContentResolver().notifyChange(uri, null);
                    database.close();
                    return answer;
            }
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException("update is not allowed for " + uri);
    }
}