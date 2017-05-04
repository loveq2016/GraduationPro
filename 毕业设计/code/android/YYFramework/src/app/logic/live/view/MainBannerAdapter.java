package app.logic.live.view;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.live.CarouselImgInfo;
import app.logic.view.web.WebBrowserActivity;
import app.utils.common.FrescoImageShowThumb;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class MainBannerAdapter extends BaseAdapter {

    private Context context;
    public List<CarouselImgInfo> datas  = new ArrayList<>();

    public MainBannerAdapter(Context context){
        this.context = context;
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
        }else{
            viewHoder = (ViewHoder)convertView.getTag();
        }
        viewHoder = (ViewHoder) convertView.getTag();
        String url =  HttpConfig.getUrl(getItem(position).getImage());
        FrescoImageShowThumb.showThrumb(Uri.parse(url),viewHoder.simpleDraweeView);

        final String address = getItem(position).getAddress();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(address)){
                    Intent intent = new Intent(context, WebBrowserActivity.class);
                    intent.putExtra(WebBrowserActivity.KBROWSER_HOME_URL,address);
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    class ViewHoder{
        SimpleDraweeView simpleDraweeView ;
    }
}
