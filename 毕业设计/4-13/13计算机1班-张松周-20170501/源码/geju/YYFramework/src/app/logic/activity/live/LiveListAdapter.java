package app.logic.activity.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import org.ql.utils.QLToastUtils;

import java.util.ArrayList;

import app.logic.activity.main.HomeActivity;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class LiveListAdapter extends BaseAdapter{

    private ArrayList<ItmeGridViewAdpter> datas = new ArrayList<>();
    private Context context;
    private ItmeGridViewAdpter itmeGridViewAdpter = new ItmeGridViewAdpter( ) ;

    /**
     * 构造方法
     */
    public  LiveListAdapter( Activity activity ){
        this.context = activity ;
        datas.add( itmeGridViewAdpter );
    }
    /**
     * 设置数据源
     */
    public void setDatas(ArrayList<IsOnLiveOrgInfo> list){
        datas.add( itmeGridViewAdpter );
        notifyDataSetChanged();
        itmeGridViewAdpter.setDatas( list );
        itmeGridViewAdpter.notifyDataSetChanged();
    }

    /**
     * 获取数据源
     * @return
     */
    public ArrayList<ItmeGridViewAdpter> getDatas(){
        return datas ;
    }

    @Override
    public int getCount() {
        return datas != null ?datas.size():0;
    }
    @Override
    public ItmeGridViewAdpter getItem(int position) {
        return datas.get( position );
    }
    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from( parent.getContext()).inflate( R.layout.live_list_itme , parent , false);
        LiveItmeGridView itmeGridViewAdpter = (LiveItmeGridView) convertView.findViewById( R.id.live_itme_gv);
        itmeGridViewAdpter.setAdapter( getItem(position) );
        itmeGridViewAdpter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IsOnLiveOrgInfo isOnLiveOrgInfo = (IsOnLiveOrgInfo) parent.getItemAtPosition( position );
//                if(isOnLiveOrgInfo.getStatus() == 1 ){
//                    QLToastUtils.showToast(context , "主播为开播");
//                    return;
//                }
                Intent intent = new Intent();
                intent.putExtra(LiveDetailsActivity.PLUG,isOnLiveOrgInfo.getPlug_id()); //直播Id
                intent.putExtra(LiveDetailsActivity.ORG_ID , isOnLiveOrgInfo.getOrg_id() );
                intent.putExtra(LiveDetailsActivity.ORG_NAME , isOnLiveOrgInfo.getOrg_name() );
                intent.putExtra(LiveDetailsActivity.ORG_BUIDER_NAME , isOnLiveOrgInfo.getOrg_builder_name());
                intent.setClass( context , LiveDetailsActivity.class);
                context.startActivity( intent );
            }
        });
        return convertView;
    }
}


//    private List<Integer> list;
//    private Context context ;
//    private static final int TYPE_ITEM = 0;
//    private static final int TYPE_FOOTER = 1;
//
//    public List<Integer> getList() {
//        return list;
//    }
//
//    public LiveListAdapter( Context context ,ArrayList<Integer> arrayList ) {
//        this.context = context ;
//        this.list = arrayList ;
//    }
//
//    // RecyclerView的count设置为数据总条数+ 1（footerView）
//    @Override
//    public int getItemCount() {
//        return list.size() + 1;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // 最后一个item设置为footerView
//        if (position + 1 == getItemCount()) {
//            return TYPE_FOOTER;
//        } else {
//            return TYPE_ITEM;
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        if (holder instanceof ItemViewHolder) {
//            ((ItemViewHolder) holder).orgNameTv.setText(String.valueOf(list.get(position)));
//            ((ItemViewHolder) holder).anchorNameTv.setText(String.valueOf(list.get(position)));
//            Picasso.with(context).load("").error( R.drawable.ic_launcher).centerCrop().into(((ItemViewHolder) holder).orgLogo);
//        }
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_ITEM) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_list_itme, null);
//            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
//            return new ItemViewHolder(view);
//        }
//        // type == TYPE_FOOTER 返回footerView
//        else if (viewType == TYPE_FOOTER) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_list_footer, null);
//            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
//            return new FooterViewHolder(view);
//        }
//        return null;
//    }
//
///**
// *  尾布局 ViewHolder
// */
//class FooterViewHolder extends RecyclerView.ViewHolder {
//
//    public FooterViewHolder(View view) {
//        super(view);
//    }
//}
//
///**
// * 每一项的 ViewHolder
// */
//class ItemViewHolder extends RecyclerView.ViewHolder {
//    TextView orgNameTv ;
//    TextView anchorNameTv ;
//    ImageView orgLogo ;
//    public ItemViewHolder(View view) {
//        super(view);
//        orgNameTv = (TextView) view.findViewById(R.id.org_name);
//        anchorNameTv = (TextView) view.findViewById(R.id.anchor_name_tv);
//        orgLogo = (ImageView) view.findViewById( R.id.org_log);
//    }
//}
