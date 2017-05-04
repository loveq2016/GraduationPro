package app.logic.activity.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.yy.geju.R;

/**
 * Created by pexcn on 2017-03-30.
 */
public class ImagesGridAdpter extends BaseAdapter {
    private ArrayList<String> mUrls;
    private Context mContext;
    private GridView mGridView;

    public ImagesGridAdpter(ArrayList<String> urls, Context context, GridView gridView) {
        mUrls = urls;
        mContext = context;
        mGridView = gridView;
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return mUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_notice_image, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext).load(mUrls.get(position)).fit().centerCrop().into(holder.image);

        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
    }

    public void add(String path) {
        mUrls.add(path);
        fixGridViewHeight(mGridView);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mUrls.remove(position);
        if (mUrls.size() == 0 || !mUrls.get(mUrls.size() - 1).equals("")) {
            mUrls.add("");
        }
        fixGridViewHeight(mGridView);
        notifyDataSetChanged();
    }

    public void fixGridViewHeight(GridView gridView) {
        int col = 3;
        int totalHeight = 0;
        for (int i = 0; i < getCount(); i += col) {
            View item = getView(i, null, gridView);
            item.measure(0, 0);
            totalHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        gridView.setLayoutParams(params);
    }
}
