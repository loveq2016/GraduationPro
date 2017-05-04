package app.logic.live.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.pojo.UserInfo;
import app.yy.geju.R;

/**
 * Created by wei on 2017/2/15. 发送信息的类
 */
public class RoomMessagesView extends RelativeLayout {
    private EMConversation conversation;
    public HashMap< String ,OrgRequestMemberInfo> orgMemberList = new HashMap<>();
    ListAdapter adapter;
    public RecyclerView listview;
    EditText editview;
    Button sendBtn;
    View sendContainer;  //发送信息栏
    ImageView closeView;
    RelativeLayout messgLL;
    public boolean isBarrageShow = false;

    public RoomMessagesView(Context context) {
        super(context);
        init(context, null);
    }

    public RoomMessagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomMessagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.widget_room_messages, this);
        messgLL = (RelativeLayout) findViewById( R.id.messgLL);
        listview = (RecyclerView) findViewById(R.id.listview);
        editview = (EditText) findViewById(R.id.edit_text);
        sendBtn = (Button) findViewById(R.id.btn_send);
        closeView = (ImageView) findViewById(R.id.close_image);
        sendContainer = findViewById(R.id.container_send);
        sendContainer.setVisibility( View.VISIBLE );
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageViewListener != null){
                    if(TextUtils.isEmpty(editview.getText())){
                        Toast.makeText(getContext(), "文字内容不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //调用信息发送监听接口 发送信息的方法
                    messageViewListener.onMessageSend(editview.getText().toString());
                    editview.setText("");
                }
            }
        });

        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setShowInputView(false);
                if(messageViewListener != null){
                    //隐藏 bottomBar
                    messageViewListener.onHiderBottomBar();
                }
            }
        });
        //danmuImage = (ImageView) findViewById(R.id.danmu_image); container_send
        editview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( s.toString().length() > 255 ){
                    s = s.subSequence(0,255);
                    editview.setText( s );
                    editview.setSelection(255);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 获取编辑框 对象
     * @return
     */
    public EditText getInputView(){
        return editview;
    }

    /**
     * 初始化信息栏 和 给发送信息按钮添加监听器
     * @param chatroomId
     */
    public void init( String chatroomId ){

        conversation = EMClient.getInstance().chatManager().getConversation(chatroomId, EMConversation.EMConversationType.GroupChat, true);
        adapter = new ListAdapter( getContext() , conversation );
        listview.setLayoutManager( new LinearLayoutManager(getContext()) );
        listview.setAdapter(adapter);
//        sendBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(messageViewListener != null){
//                    if(TextUtils.isEmpty(editview.getText())){
//                        Toast.makeText(getContext(), "文字内容不能为空！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    //调用信息发送监听接口 发送信息的方法
//                    messageViewListener.onMessageSend(editview.getText().toString());
//                    editview.setText("");
//                }
//            }
//        });
//
//        closeView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setShowInputView(false);
//                if(messageViewListener != null){
//                    //隐藏 bottomBar
//                    messageViewListener.onHiderBottomBar();
//                }
//            }
//        });
//        danmuImage.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(danmuImage.isSelected()){
//                    danmuImage.setSelected(false);
//                    isBarrageShow = false;
//                }else {
//                    danmuImage.setSelected(true);
//                    isBarrageShow = true;
//                }
//            }
//        });

    }

    /**
     * 设置 发送信息工具栏的 显示和隐藏
     * @param showInputView
     */
    public void setShowInputView( boolean showInputView ){
        if( showInputView ){
            sendContainer.setVisibility(View.VISIBLE);
        }else{
            sendContainer.setVisibility(View.INVISIBLE);
        }
    }

    //监听器对象
    private MessageViewListener messageViewListener ;
    //接口
    public interface MessageViewListener{
        void onMessageSend(String content);              //信息发送
        void onItemClickListener(EMMessage message);     //itme点击
        void onHiderBottomBar();                         //隐藏bottomBar栏
    }

    //设置发送信息监听器
    public void setMessageViewListener(MessageViewListener messageViewListener){
        this.messageViewListener = messageViewListener;
    }

    //适配器刷新数据
    public void refresh(){
        if(adapter != null){
            adapter.refresh();
        }
    }

    //适配器刷新数据
    public void refreshSelectLast(){
        if(adapter != null){
            adapter.refresh();
            //显示的位置
            if( adapter.getItemCount() >= 1 ){
                listview.smoothScrollToPosition(adapter.getItemCount()-1);
            }
        }
    }

    /**
     * RecyclerView信息 (  消息栏 )适配器
     */
    private class ListAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private final Context context;
        EMMessage[] messages;
        public ListAdapter(Context context, EMConversation conversation){
            this.context = context;
            messages = conversation.getAllMessages().toArray(new EMMessage[0]);
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_room_msgs_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final EMMessage message = messages[position];
            //System.out.println("RoomMessagesView onBindViewHolder");
            String phon = message.getFrom();
            //String name = getUserNickNameByHXAccount( phon );
            holder.name.setText(phon+":");                                   //消息来自谁message.getFrom()
            holder.content.setText(((EMTextMessageBody)message.getBody()).getMessage());  //消息的内容
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(messageViewListener != null){
                        //调用itme点击方法
                        messageViewListener.onItemClickListener( message );
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return messages!=null?messages.length:0;
        }
        //数据刷新
        public void refresh(){
            messages = conversation.getAllMessages().toArray(new EMMessage[0]);
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * ViewHolder
     */
    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView content;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }

    /**
     * 根据手机号码查找名字
     * @param phon
     * @return
     */
    private String getUserNickNameByHXAccount(String phon) {
        if (phon == null || orgMemberList == null) {
            return "";
        }
        OrgRequestMemberInfo info = orgMemberList.get(phon);
        if (info != null) {
            return info.getNickName();
        }
        return phon ;
    }

    /**
     * 根据手机号获取发送消息的头像Url
     * @param phone
     * @return
     */
    private String getUserHeadImgUrlByHXAccount(String phone) {
        if (phone == null || orgMemberList == null) {
            return "";
        }
        OrgRequestMemberInfo info = orgMemberList.get(phone);
        if (info != null) {
            String urlString = HttpConfig.getUrl(info.getPicture_url());
            if (urlString == null || TextUtils.isEmpty(urlString)) {
                return HttpConfig.getUrl(info.getMy_picture_url());
            }
            return HttpConfig.getUrl(info.getPicture_url());
        }
        return "";
    }
}
