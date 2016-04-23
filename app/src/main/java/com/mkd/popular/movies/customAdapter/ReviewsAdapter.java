package com.mkd.popular.movies.customAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mkd.popular.movies.R;
import com.mkd.popular.movies.bean.Reviews;

import java.util.ArrayList;

/**
 * Created by mkdin on 22-04-2016.
 */
public class ReviewsAdapter extends ArrayAdapter<Reviews> {

    Context mContext;

    public ReviewsAdapter(Activity context, int layoutResourceId, ArrayList<Reviews> mGridData) {
        super(context, layoutResourceId, mGridData);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reviews review = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.reviews_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtAuthor.setText(String.format(
                mContext.getString(R.string.comment_author), review.getAuthor()));
        viewHolder.txtContent.setText(review.getContent());
        return convertView;
    }

    public static class ViewHolder {
        TextView txtAuthor;
        TextView txtContent;

        public ViewHolder(View view) {
            txtAuthor = (TextView) view.findViewById(R.id.author_text_view);
            txtContent = (TextView) view.findViewById(R.id.review_content_text_view);

        }
    }


}
