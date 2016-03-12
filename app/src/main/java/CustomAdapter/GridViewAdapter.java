package CustomAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mkd.popular.movies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import MovieBean.Movies;

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


    /** Log.v(LOG_TAG, "mGridData Size:" + mGridData.size());
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<Movies> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_poster_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Movies item = mGridData.get(position);
        Picasso.with(mContext).load(item.getPosterURL()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
