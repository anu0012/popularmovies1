package com.example.anuragsharma.popularmovies;



/**
 * Created by anuragsharma on 20/04/16.
 */
public class Back {

    public static String backg(MovieDetails m){
        String path = m.getPoster_path();
        final String IMAGE_BASE = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w500/";
        final String IMAGE_URL = path;

        String url = IMAGE_BASE+IMAGE_SIZE+IMAGE_URL;
        return url;
    }
}
