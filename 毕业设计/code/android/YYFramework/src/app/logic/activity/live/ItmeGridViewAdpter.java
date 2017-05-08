package app.logic.activity.live;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import org.ql.utils.QLToastUtils;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.FrescoImageShowThumb;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class ItmeGridViewAdpter extends BaseAdapter {

    private Context context;
    //数据源
    private ArrayList<IsOnLiveOrgInfo> datas = new ArrayList<>();

    private ItemClickListener itemClickListener;

    public ItmeGridViewAdpter(){
    }
    /**
     * 构造方法
     */
    public ItmeGridViewAdpter(Context context ){
        this.context = context;
    }

    public interface ItemClickListener{
        public void itemClick(IsOnLiveOrgInfo orgInfo);
    }

    public void setItemClick(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    /**
     * 设置数据
     * @param datas
     */
    public void setDatas( ArrayList<IsOnLiveOrgInfo> datas ){
        this.datas.clear();
        this.datas.addAll( datas );
        notifyDataSetChanged();
    }

    /**
     * 获取适配器的数据源
     * @return
     */
    public ArrayList<IsOnLiveOrgInfo> getDatas() {
        return datas;
    }

    @Override
    public int getCount() {
        return datas != null ? (datas.size()%2==0? datas.size()/2:datas.size()/2+1):0;

    }

    @Override
    public IsOnLiveOrgInfo getItem(int position) {
        return datas.get( position  );
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if( convertView == null ){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.live_itme_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.orgNameTv = (TextView) convertView.findViewById( R.id.org_name) ;
            viewHolder.anchorNameTv = (TextView) convertView.findViewById(R.id.anchor_name_tv);
            viewHolder.orgLogoImg = (SimpleDraweeView) convertView.findViewById( R.id.org_logo);
            viewHolder.orgNameTv2 = (TextView) convertView.findViewById( R.id.org_name2) ;
            viewHolder.anchorNameTv2 = (TextView) convertView.findViewById(R.id.anchor_name_tv2);
            viewHolder.orgLogoImg2 = (SimpleDraweeView) convertView.findViewById( R.id.org_logo2);
            viewHolder.img_state1 = (ImageView) convertView.findViewById( R.id.img_state1);
            viewHolder.img_state2 = (ImageView) convertView.findViewById( R.id.img_state2);
            viewHolder.layout1 =  convertView.findViewById( R.id.live_layout1);
            viewHolder.layout2 =  convertView.findViewById( R.id.live_layout2);
            viewHolder.stateView1 = (TextView) convertView.findViewById( R.id.live_state1);
            viewHolder.stateView2 = (TextView) convertView.findViewById( R.id.live_state2);
            viewHolder.coverImg = (SimpleDraweeView) convertView.findViewById( R.id.org_log);
            viewHolder.coverImg2 = (SimpleDraweeView) convertView.findViewById( R.id.org_log2);
            viewHolder.liveTitle = (TextView) convertView.findViewById( R.id.live_title);
            viewHolder.liveTitle2 = (TextView) convertView.findViewById( R.id.live_title2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position*2<datas.size()){
            IsOnLiveOrgInfo orgInfo = datas.get( position * 2 );
            if( orgInfo != null ){
                viewHolder.orgNameTv.setText( orgInfo.getOrg_name() );
                if(orgInfo.getFriend_name()!=null&& !TextUtils.isEmpty(orgInfo.getFriend_name())){
                    viewHolder.anchorNameTv.setText( orgInfo.getFriend_name());
                }else{
                    viewHolder.anchorNameTv.setText( orgInfo.getLive_creator_name());
                }
                String url = HttpConfig.getUrl(orgInfo.getOrg_logo_url());
                FrescoImageShowThumb.showThrumb(Uri.parse(url),viewHolder.orgLogoImg);
                FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(orgInfo.getLive_cover())),viewHolder.coverImg);
                viewHolder.liveTitle.setText(orgInfo.getLive_title());
                viewHolder.layout1.setTag(orgInfo);
                viewHolder.layout1.setOnClickListener(listener);
                if(orgInfo.getStatus()==0){
                    viewHolder.stateView1.setText("直播中");
                    viewHolder.img_state1.setBackgroundResource( R.drawable.live_state_ed);
                }else{
                    viewHolder.stateView1.setText("休息中");
                    viewHolder.img_state1.setBackgroundResource( R.drawable.live_state_ing);
                }
            }

            if (position*2+1<datas.size()){
                IsOnLiveOrgInfo orgInfo1 = datas.get( position *2 +1 );
                if( orgInfo1 != null ){
                    viewHolder.layout2.setVisibility(View.VISIBLE);
                    viewHolder.orgNameTv2.setText( orgInfo1.getOrg_name() );
                    if(orgInfo1.getFriend_name()!=null&& !TextUtils.isEmpty(orgInfo1.getFriend_name())){
                        viewHolder.anchorNameTv2.setText( orgInfo1.getFriend_name());
                    }else{
                        viewHolder.anchorNameTv2.setText( orgInfo1.getLive_creator_name());
                    }
                    String url = HttpConfig.getUrl(orgInfo1.getOrg_logo_url());
                    FrescoImageShowThumb.showThrumb(Uri.parse(url),viewHolder.orgLogoImg2);
                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(orgInfo1.getLive_cover())),viewHolder.coverImg2);
                    viewHolder.liveTitle2.setText(orgInfo1.getLive_title());
                    viewHolder.layout2.setTag(orgInfo1);
                    viewHolder.layout2.setOnClickListener(listener);
                    if(orgInfo1.getStatus()==0){
                        viewHolder.stateView2.setText("直播中");
                        viewHolder.img_state2.setBackgroundResource( R.drawable.live_state_ed);
                    }else{
                        viewHolder.stateView2.setText("休息中");
                        viewHolder.img_state2.setBackgroundResource( R.drawable.live_state_ing);
                    }
                }
            }else{
                viewHolder.layout2.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    class ViewHolder{
        TextView orgNameTv,orgNameTv2 ;
        TextView anchorNameTv,anchorNameTv2 ;
        ImageView img_state1,img_state2  ;
        View layout1,layout2;
        TextView stateView1 , stateView2 ;
        SimpleDraweeView orgLogoImg ,orgLogoImg2,coverImg,coverImg2;
        TextView liveTitle,liveTitle2 ;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.live_layout1:
                case R.id.live_layout2:
                    if (itemClickListener !=null)
                        itemClickListener.itemClick((IsOnLiveOrgInfo) v.getTag());

//                    IsOnLiveOrgInfo isOnLiveOrgInfo = (IsOnLiveOrgInfo) v.getTag();
//                    Intent intent = new Intent();
//                    intent.putExtra(LiveDetailsActivity.PLUG,isOnLiveOrgInfo.getPlug_id()); //直播Id
//                    intent.putExtra(LiveDetailsActivity.ROOM_ID , isOnLiveOrgInfo.getRoom_id() );
//                    intent.putExtra(LiveDetailsActivity.ORG_ID , isOnLiveOrgInfo.getOrg_id() );
//                    intent.putExtra(LiveDetailsActivity.ORG_NAME , isOnLiveOrgInfo.getOrg_name() );
//                    intent.putExtra(LiveDetailsActivity.ORG_LOG_URL , isOnLiveOrgInfo.getOrg_logo_url() );
//                    intent.putExtra(LiveDetailsActivity.ORG_BUIDER_NAME , isOnLiveOrgInfo.getOrg_builder_name());
//                    intent.setClass( context , LiveDetailsActivity.class);
//                    context.startActivity( intent );
                    break;
            }
        }
    };
}
