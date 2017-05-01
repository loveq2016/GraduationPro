package app.logic.activity.announce;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import app.yy.geju.R;

/**
 * Created by pexcn on 2017-03-30.
 */
public class AddImagesGridAdpter extends BaseAdapter {
    private ArrayList<String> mPaths;
    private Activity mActivity;
    private GridView mGridView;

    public AddImagesGridAdpter(ArrayList<String> paths, Activity activity, GridView gridView) {
        mPaths = paths;
        mActivity = activity;
        mGridView = gridView;
        mPaths.add("");
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return mPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announce_image, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image_view_pick);
            holder.del = (ImageView) convertView.findViewById(R.id.image_view_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        fixGridViewHeight();

        if (position >= 9) {
            holder.image.setVisibility(View.GONE);
            holder.del.setVisibility(View.GONE);
            return convertView;
        }

//        Picasso.with(mActivity).load(R.drawable.btn_add_pic).fit().centerCrop().into(holder.image);
//        if (mPaths != null) {
//            if (position == mPaths.size() - 1 && TextUtils.isEmpty(mPaths.get(position))) {
//                holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
//                holder.del.setVisibility(View.GONE);
//            } else {
//                holder.del.setVisibility(View.VISIBLE);
//                holder.del.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        remove(position);
//                        fixGridViewHeight();
//                    }
//                });
//                if (!TextUtils.isEmpty(mPaths.get(position))) {
//                    File imgFile = new File(mPaths.get(position));
//                    Picasso.with(mActivity).load(imgFile).fit().centerCrop().into(holder.image);
//                }
//            }
//        }

        if (mPaths.get(position).equals("")) {
            Picasso.with(holder.image.getContext()).load(R.drawable.btn_add_pic).fit().centerCrop().into(holder.image);
            holder.del.setVisibility(View.GONE);
        } else {
            File imgFile = new File(mPaths.get(position));
            Picasso.with(holder.image.getContext()).load(imgFile).fit().centerCrop().into(holder.image);
            holder.del.setVisibility(View.VISIBLE);
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(position);
//                    fixGridViewHeight();
                }
            });
        }

        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
        public ImageView del;
    }

    public void add(String path) {
        mPaths.add(path);
        mPaths.add("");
        fixGridViewHeight(mGridView);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mPaths.remove(position);
        if (mPaths.size() == 0 || !mPaths.get(mPaths.size() - 1).equals("")) {
            mPaths.add("");
        }
        fixGridViewHeight(mGridView);
        notifyDataSetChanged();
    }

//    private void fixGridViewHeight() {
//        ViewGroup.LayoutParams params = mGridView.getLayoutParams();
//        if (mPaths.size() <= 4) {
//            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        } else if (mPaths.size() > 4 && mPaths.size() <= 8) {
//            params.height = YYUtils.dp2px(190, mActivity);
//        } else if (mPaths.size() >= 9) {
//            params.height = YYUtils.dp2px(280, mActivity);
//        }
//        mGridView.setLayoutParams(params);
//    }

    public void fixGridViewHeight(GridView gridView) {
        int col = 4;
        int totalHeight = 0;
        for (int i = 0; i < getCount(); i += col) {
            View item = getView(i, null, gridView);
            item.measure(0, 0);
            totalHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        ((ViewGroup.MarginLayoutParams) params).setMargins(15, 15, 15, 15);
        gridView.setLayoutParams(params);
    }
}
