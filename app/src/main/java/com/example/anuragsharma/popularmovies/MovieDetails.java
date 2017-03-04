package com.example.anuragsharma.popularmovies;

/**
 * Created by anuragsharma on 18/04/16.
 */
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class MovieDetails implements Parcelable{
    private String original_title;
    private String poster_path;
    private String overview;
    private String vote_average;
    private String release_date;
    private long id;

    public MovieDetails(String original_title, String poster_path, String overview, String vote_average, String release_date,long id) {
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public long getId(){
        return id;
    }

    public MovieDetails(Parcel p){
        original_title = p.readString();
        poster_path = p.readString();
        overview = p.readString();
        vote_average = p.readString();
        release_date = p.readString();
        id = p.readLong();
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>() {
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original_title);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(vote_average);
        dest.writeString(release_date);
        dest.writeLong(id);

    }
}