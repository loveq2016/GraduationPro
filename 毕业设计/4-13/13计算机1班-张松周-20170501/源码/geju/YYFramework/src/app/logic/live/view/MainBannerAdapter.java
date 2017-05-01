package app.logic.live.view;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.live.CarouselImgInfo;
import app.utils.common.FrescoImageShowThumb;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class MainBannerAdapter extends BaseAdapter {

    public List<CarouselImgInfo> datas  = new ArrayList<>();

    public MainBannerAdapter(){
    }

    public void setData( List<CarouselImgInfo> list ){
        datas.clear();
        if( list!=null && list.size()>0 ){
            datas.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(datas.size() == 0 ){
            return  0;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public CarouselImgInfo getItem(int position) {
        if(position>=datas.size()){
           int i = position% datas.size() ;
            return datas.get( i );
        }
        return datas.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder = null;
        if(convertView == null ){
            viewHoder = new ViewHoder() ;
            convertView = LayoutInflater.from( parent.getContext()).inflate(R.layout.layout_livelist_hand , null);
            viewHoder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById( R.id.launch_iv);
            convertView.setTag(viewHoder);
        }
        viewHoder = (ViewHoder) convertView.getTag();
        String url =  HttpConfig.getUrl(getItem(position).getImage());
        FrescoImageShowThumb.showThrumb(Uri.parse(url),viewHoder.simpleDraweeView);
        return convertView;
    }

    class ViewHoder{
        SimpleDraweeView simpleDraweeView ;
    }
}
