package com.example.anuragsharma.popularmovies;

/**
 * Created by anuragsharma on 18/04/16.
 */

import android.app.Activity;
import android.content.Context;

import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter {
     Context context;
    static final String LOG_TAG = GridViewAdapter.class.getSimpleName();

    int layoutid;

    private ArrayList<MovieDetails> data = new ArrayList<>();

    public GridViewAdapter(Context context,int layoutid, ArrayList data) {
        super(context, layoutid, data);

        this.context = context;
        this.data = data;
        this.layoutid=layoutid;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView=null;
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutid, parent, false);
        if (convertView == null) {
            imageView=new ImageView(context);


        }
        else
        imageView=(ImageView)convertView;

        final String IMAGE_BASE="http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE="w342/";
        final String IMAGE_URL=data.get(position).getPoster_path();
        Log.v(LOG_TAG,"Image Url"+IMAGE_URL);
        String url = IMAGE_BASE+IMAGE_SIZE+IMAGE_URL;
        Log.v(LOG_TAG, "url1" + url);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
            int orientation=((Activity) getContext()).getResources().getConfiguration().orientation;
            if(orientation== Configuration.ORIENTATION_PORTRAIT){
                Picasso.with(context).load(url).resize(width/2, 0).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e(LOG_TAG, "Success");
                    }

                    @Override
                    public void onError() {
                        Log.e(LOG_TAG, "Error");
                    }
                });
            }
            else{

                Picasso.with(context).load(url).resize(width/4,0).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e(LOG_TAG, "Success");
                    }

                    @Override
                    public void onError() {
                        Log.e(LOG_TAG, "Error");
                    }
                });
            }


        return imageView;
    }

}
