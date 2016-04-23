package com.mkd.popular.movies.customAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mkd.popular.movies.R;
import com.mkd.popular.movies.bean.Reviews;
import com.mkd.popular.movies.bean.YoutubeLinks;

import java.util.ArrayList;

/**
 * Created by mkdin on 22-04-2016.
 */
public class VideosAdapter extends ArrayAdapter<YoutubeLinks>{
    public VideosAdapter(Activity mContext, int layoutResourceId, ArrayList<YoutubeLinks> mGridData) {
        super(mContext, layoutResourceId, mGridData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YoutubeLinks trailer = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.trailers_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtType.setText(trailer.getName());
        return convertView;
    }

    public static class ViewHolder {
        TextView txtType;

        public ViewHolder(View view) {
            txtType = (TextView)view.findViewById(R.id.trailer_name);

        }
    }
}
