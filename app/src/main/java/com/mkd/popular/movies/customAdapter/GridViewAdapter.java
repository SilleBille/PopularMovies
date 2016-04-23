package com.mkd.popular.movies.customAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mkd.popular.movies.R;
import com.mkd.popular.movies.bean.Movies;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mkdin on 11-03-2016.
 */
public class GridViewAdapter extends ArrayAdapter<Movies> {

    private Activity mContext;
    private int layoutResourceId;
    private ArrayList<Movies> mGridData = new ArrayList<>();

    public GridViewAdapter(Activity mContext, int layoutResourceId, ArrayList<Movies> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Log.v(LOG_TAG, "mGridData Size:" + mGridData.size());
     * Updates grid data and refresh grid items.
     *
     * @param mGridData
     */
    public void setGridData(ArrayList<Movies> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_poster_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Movies item = mGridData.get(position);
        Picasso.with(mContext)
                .load(item.getPosterURL())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(mContext)
                                .load(item.getPosterURL())
                                .placeholder(R.drawable.ic_image_place_holder)
                                .error(R.drawable.ic_error_place_holder)
                                .into(holder.imageView);
                    }
                });
        return row;
    }

    static class ViewHolder {
        ImageView imageView;
    }


}
