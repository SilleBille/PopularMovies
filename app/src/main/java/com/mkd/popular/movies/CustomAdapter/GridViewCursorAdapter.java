package com.mkd.popular.movies.customAdapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mkd.popular.movies.MainActivityFragment;
import com.mkd.popular.movies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by mkdin on 20-04-2016.
 */
public class GridViewCursorAdapter extends CursorAdapter {

    public GridViewCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewID = R.layout.poster_grid_layout;
        View view = LayoutInflater.from(context).inflate(viewID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String poster = cursor.getString(MainActivityFragment.COLUMN_MOVIE_POSTER);

        Picasso.with(context)
                .load("file://" + poster)
                .placeholder(R.drawable.ic_image_place_holder)
                .error(R.drawable.ic_error_place_holder)
                .into(viewHolder.imgPoster);
    }

    public static class ViewHolder {
        ImageView imgPoster;

        public ViewHolder(View view) {
            imgPoster = (ImageView) view.findViewById(R.id.grid_poster_image);
        }
    }
}
