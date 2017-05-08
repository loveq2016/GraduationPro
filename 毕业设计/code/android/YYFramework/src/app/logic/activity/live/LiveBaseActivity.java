package app.logic.activity.live;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easemob.livestream.utils.Utils;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import org.QLConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.controller.LivestreamController;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.live.view.BarrageLayout;
import app.logic.live.view.LiveLeftGiftView;
import app.logic.live.view.LiveView;
import app.logic.live.view.PeriscopeLayout;
import app.logic.live.view.RoomMessagesView;
import app.logic.live.view.RoomUserDetailsDialog;
import app.logic.pojo.LiveMemberInfo;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.yy.geju.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wei on 2016/6/12.
 */
public abstract class LiveBaseActivity extends BaseActivity {
  protected static final String TAG = "LiveActivity";
  @BindView(R.id.left_gift_view1)
  LiveLeftGiftView leftGiftView;   //礼物栏
  @BindView(R.id.left_gift_view2)
  LiveLeftGiftView leftGiftView2;  //礼物栏
  @BindView(R.id.message_view)
  RoomMessagesView messageView;    //信息栏 和 发送信息栏一起的
  @BindView(R.id.periscope_layout)
  PeriscopeLayout periscopeLayout; //爱心栏
  @BindView(R.id.bottom_bar)
  View bottomBar;                  //三个图片按钮栏
  @BindView(R.id.barrage_layout)
  BarrageLayout barrageLayout;
  @BindView(R.id.horizontal_recycle_view)
  RecyclerView horizontalRecyclerView;  //当前在房间里成员的头像
  @BindView(R.id.audience_num)
  TextView audienceNumView;             //当前在房间里的人数
  @BindView(R.id.new_messages_warn)
  ImageView newMsgNotifyImage;         //有 新消息 提示 view
  protected EMGroup group ;
  protected Handler handler = new Handler();
  /**
   * 环信聊天室id
   */
  protected String chatroomId = "";
  /**
   * 直播流id
   */
  protected String liveStreamId = "";
  /**
   * 直播id
   */
  protected String liveId = "";
  protected String uesrLiveId;
  /**
   * 主播名字
   */
  protected String anchorId;
  public static String urseName ="";
  //消息列表初始化标志
  protected boolean isMessageListInited;
  //聊天室 变化监听器
  protected EMChatRoomChangeListener chatRoomChangeListener;
  //聊天室对象
  protected EMChatRoom chatroom;
  protected AvatarAdapter memderAdapter ;
   //礼物是否显示
  volatile boolean isGiftShowing = false;
  volatile boolean isGift2Showing = false;
  List<String> toShowList = Collections.synchronizedList( new LinkedList<String>());
  //直播室成员列表
  List<LiveMemberInfo> memberList = new ArrayList<>();
  List<String> memberList2 = new ArrayList<>();
  protected long watchNumber ;
  //当前组织的成员列表
  protected HashMap< String , OrgRequestMemberInfo > orgMemberList = new HashMap<>() ;

  //消息监听器
  EMMessageListener msgListener = new EMMessageListener() {
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
      for (EMMessage message : messages) {
        String username = null;
        if ( message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
          username = message.getTo();  // 群组消息
        } else {
          username = message.getFrom(); // 单聊消息
        }
        if(((EMTextMessageBody)message.getBody()).getMessage().equals("进入直播") ){
          messageView.refreshSelectLast();
          try {
            group =  EMClient.getInstance().groupManager().getGroupFromServer(chatroomId);
            List< String > list = group.getMembers();
            if( list != null && list.size() > watchNumber ){
              watchNumber = watchNumber + (list.size() - watchNumber);
            }
//            memberList.clear();
//            memberList.addAll(list);//获取群成员
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
//                memderAdapter.notifyDataSetChanged();
                getMemberListInfo();
              }
            });

          } catch (HyphenateException e) {
            e.printStackTrace();
          }
        }
        if(((EMTextMessageBody)message.getBody()).getMessage().equals("离开直播")){
          messageView.refreshSelectLast();
          try {
            group =  EMClient.getInstance().groupManager().getGroupFromServer(chatroomId);
            List< String > list = group.getMembers();
            if( list != null && list.size() > watchNumber ){
              watchNumber = watchNumber + (list.size() - watchNumber);
            }
//            memberList.clear();
//            memberList.addAll(list );//获取群成员
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
//                memderAdapter.notifyDataSetChanged();
                getMemberListInfo();
              }
            });
          } catch (HyphenateException e) {
            e.printStackTrace();
          }
        }
        // 如果是当前会话的消息，刷新聊天页面
        if ( username.equals(chatroomId) ) {
            //消息弹窗
            //if (message.getBooleanAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, false)) {
              //barrageLayout.addBarrage(((EMTextMessageBody) message.getBody()).getMessage(),message.getFrom());
            //}
          messageView.refreshSelectLast();
          //EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
        } else {
          if(message.getChatType() == EMMessage.ChatType.Chat && message.getTo().equals(EMClient.getInstance().getCurrentUser())){
            runOnUiThread( new Runnable() {
              @Override
              public void run() {
                newMsgNotifyImage.setVisibility(View.INVISIBLE);
              }
            });
          }
        }
      }
    }

    //接收透传消息
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {
      if( isMessageListInited ){
        messageView.refresh();
      }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {
      if( isMessageListInited ){
        messageView.refresh();
      }
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
      if (isMessageListInited) {
        messageView.refresh();
      }
    }
  };

  private void getMemberListInfo(){
    LivestreamController.getLiveMemberList(this, uesrLiveId, new Listener<String, List<LiveMemberInfo>>() {
      @Override
      public void onCallBack(String s, List<LiveMemberInfo> reply) {
        if (reply !=null && reply.size()>0){
          memberList.clear();
          memberList.addAll(reply);//获取群成员
          memderAdapter.notifyDataSetChanged();
          watchNumber = reply.size();
          //直播室成员的 人数
          audienceNumView.setText(String.valueOf(memberList.size()));
        }
      }
    });
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    onActivityCreate(savedInstanceState);//调用重写的方法
  }

  //抽象方法，子类重写
  protected abstract void onActivityCreate(@Nullable Bundle savedInstanceState);


  //=============== 显示礼物的逻辑处理 =================

  /**
   * 显示礼物的方法
   * @param name
   */
  protected synchronized void showLeftGiftVeiw(String name) {
    if (!isGift2Showing) {
      showGift2Derect(name);
    } else if (!isGiftShowing) {
      showGift1Derect(name);
    } else {
      toShowList.add(name);
    }
  }

  /**
   * 显示礼物的方法
   * @param name
   */
  private void showGift1Derect(final String name) {
    isGiftShowing = true;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        leftGiftView.setVisibility(View.VISIBLE);
        leftGiftView.setName(name);
        leftGiftView.setTranslationY(0);
        ViewAnimator.animate(leftGiftView)
            .alpha(0, 1)
            .translationX(-leftGiftView.getWidth(), 0)
            .duration(600)
            .thenAnimate(leftGiftView)
            .alpha(1, 0)
            .translationY(-1.5f * leftGiftView.getHeight())
            .duration(800)
            .onStop(new AnimationListener.Stop() {
              @Override
              public void onStop() {
                String pollName = null;
                try {
                  pollName = toShowList.remove(0);
                } catch (Exception e) {

                }
                if (pollName != null) {
                  showGift1Derect(pollName);
                } else {
                  isGiftShowing = false;
                }
              }
            })
            .startDelay(2000)
            .start();
        ViewAnimator.animate(leftGiftView.getGiftImageView())
            .translationX(-leftGiftView.getGiftImageView().getX(), 0)
            .duration(1100)
            .start();
      }
    });
  }

  /**
   * 显示礼物的方法
   * @param name
   */
  private void showGift2Derect(final String name) {
    isGift2Showing = true;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        leftGiftView2.setVisibility(View.VISIBLE);
        leftGiftView2.setName(name);
        leftGiftView2.setTranslationY(0);
        ViewAnimator.animate(leftGiftView2)
            .alpha(0, 1)
            .translationX(-leftGiftView2.getWidth(), 0)
            .duration(600)
            .thenAnimate(leftGiftView2)
            .alpha(1, 0)
            .translationY(-1.5f * leftGiftView2.getHeight())
            .duration(800)
            .onStop(new AnimationListener.Stop() {
              @Override
              public void onStop() {
                String pollName = null;
                try {
                  pollName = toShowList.remove(0);
                } catch (Exception e) {

                }
                if (pollName != null) {
                  showGift2Derect(pollName);
                } else {
                  isGift2Showing = false;
                }
              }
            })
            .startDelay(2000)
            .start();
        ViewAnimator.animate(leftGiftView2.getGiftImageView())
            .translationX(-leftGiftView2.getGiftImageView().getX(), 0)
            .duration(1100)
            .start();
      }
    });
  }

  /**
   * 注册聊天室监听
   */
  protected void addChatRoomChangeListenr() {
    chatRoomChangeListener = new EMChatRoomChangeListener() {
      //聊天室解散
      @Override
      public void onChatRoomDestroyed(String roomId, String roomName) {
        if (roomId.equals(chatroomId)) {
          EMLog.e(TAG, " room : " + roomId + " with room name : " + roomName + " was destroyed");
        }
      }
      //成员进入聊天室时
      @Override
      public void onMemberJoined(String roomId , String participant) {
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        //message.setReceipt(chatroomId);
        message.setFrom(participant);
        EMTextMessageBody textMessageBody = new EMTextMessageBody("来了");
        message.addBody(textMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().saveMessage(message);
        messageView.refreshSelectLast();
        //System.out.println(" 那个谁来了 ");
//        onRoomMemberAdded(participant);
        getMemberListInfo();
        //聊天室成员加一
        watchNumber++ ;
      }

      //聊天室成员退出聊天室时
      @Override
      public void onMemberExited(String roomId, String roomName, String participant) {
        onRoomMemberExited(participant);
      }
      //聊天室成员被移除时 调用
      @Override
      public void onRemovedFromChatRoom(String s, String s1, String s2) {

      }
    };
    //注册聊天室监听 ， 来监听成员被踢和聊天室被删除
    EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
  }

  /**
   * 初始化信息列表栏
   */
  protected void onMessageListInit() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        //初始消息列表
        messageView.init(chatroomId);
        //注册消息监听器
        messageView.setMessageViewListener(new RoomMessagesView.MessageViewListener() {
          @Override
          public void onMessageSend(String content) {
            EMMessage message = EMMessage.createTxtSendMessage(content , chatroomId);
            if (messageView.isBarrageShow) {
              //消息 弹窗效果
              //message.setAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, true);
              //barrageLayout.addBarrage(content, EMClient.getInstance().getCurrentUser());
            }
            message.setChatType(EMMessage.ChatType.GroupChat);          //消息类型
            //message.setAttribute( "em_ignore_notification" , true);     //取消推送通知
            EMClient.getInstance().chatManager().sendMessage(message);  //发送消息内容
            //String urseName = UserManagerController.getCurrUserInfo().getNickName() ;
            if( !TextUtils.isEmpty(urseName))message.setFrom( urseName );
            message.setMessageStatusCallback(new EMCallBack() {         //接口回调
              @Override
              public void onSuccess() {
                //（消息发送成功）刷新消息列表
                messageView.refreshSelectLast();
                LiveBaseActivity.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    messageView.listview.setVisibility( View.VISIBLE );
                  }
                });
              }

              @Override
              public void onError(int i, String s) {
                showToast("消息发送失败！");
              }

              @Override
              public void onProgress(int i, String s) {

              }
            });
          }

          @Override
          public void onItemClickListener(final EMMessage message) {
            //if(message.getFrom().equals(EMClient.getInstance().getCurrentUser())){
            //    return;
            //}
            String clickUsername = message.getFrom();
            //showUserDetailsDialog(clickUsername);
          }

          @Override
          public void onHiderBottomBar() {
            bottomBar.setVisibility(View.VISIBLE);
          }
        });
        messageView.setVisibility(View.VISIBLE );
        bottomBar.setVisibility(View.INVISIBLE );
        //消息列表初始化标志
        isMessageListInited = true;
        //更新消息 view
        //updateUnreadMsgView();
        //显示 聊天室成员列表（ 初始化 ）
        showMemberList();
      }
    });
  }

  /**
   *更新未读信息的 view
   */
  protected void updateUnreadMsgView(){
    if(isMessageListInited) {
      for (EMConversation conversation : EMClient.getInstance()
          .chatManager()
          .getAllConversations()
          .values()) {
        if (conversation.getType() == EMConversation.EMConversationType.Chat && conversation.getUnreadMsgCount() > 0) {
          newMsgNotifyImage.setVisibility(View.VISIBLE);
          return;
        }
      }
      newMsgNotifyImage.setVisibility(View.INVISIBLE);
    }
  }

  /**
   * 显示用户详细信息
   * @param username
   */
  private void showUserDetailsDialog(String username) {
    final RoomUserDetailsDialog dialog = RoomUserDetailsDialog.newInstance(username);
    dialog.setUserDetailsDialogListener(
        new RoomUserDetailsDialog.UserDetailsDialogListener() {
          @Override
          public void onMentionClick(String username) {
            dialog.dismiss();
            messageView.getInputView().setText("@" + username + " ");
            showInputView();
          }
        });
    dialog.show(getSupportFragmentManager(), "RoomUserDetailsDialog");
  }

  /**
   *  隐藏 三个图标栏 显示 发送信息工具栏( 输入栏 )
   */
  private void showInputView() {
    bottomBar.setVisibility(View.INVISIBLE);
    messageView.setShowInputView(true);
    messageView.getInputView().requestFocus();
    messageView.getInputView().requestFocusFromTouch();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Utils.showKeyboard( messageView.getInputView());
      }
    }, 200);
  }

  //=============================直播室成员================================

  /**
   * 显示直播室成员列表
   */
  void showMemberList() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(LiveBaseActivity.this);
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    memderAdapter = new AvatarAdapter(LiveBaseActivity.this);
    horizontalRecyclerView.setAdapter(memderAdapter);
    horizontalRecyclerView.setLayoutManager(layoutManager);
//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        try {
//          //根据直播室Id 获取直播室对象
//          group =  EMClient.getInstance().groupManager().getGroupFromServer(chatroomId);
//          //获取直播室的成员添加到列表集合中
//          watchNumber = group.getMembers().size();
//          memberList.addAll( group.getMembers() );
//        } catch (HyphenateException e) {
//          e.printStackTrace();
//        }
//        runOnUiThread(new Runnable() {
//          @Override
//            public void run() {
//            //直播室成员的 人数
//            audienceNumView.setText(String.valueOf(memberList.size()));
//            //适配器刷新数据
//            memderAdapter.notifyDataSetChanged();
//          }
//        });
//      }
//    }).start();

    getMemberListInfo();
  }

  /**
   * 直播室成员 增加
   * @param name
   */
//  private void onRoomMemberAdded(String name) {
//    if (!memberList.contains(name)) {
//      memberList.add(name);   //添加成员
//    }
//
//    runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
//        audienceNumView.setText(String.valueOf(memberList.size()));
//        horizontalRecyclerView.getAdapter().notifyDataSetChanged();
//      }
//    });
//  }

  /**
   * 直播室成员 减少
   */
  private void onRoomMemberExited(String name) {
    memberList.remove(name); //集合移除指定对象
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        audienceNumView.setText(String.valueOf(memberList.size()));
        horizontalRecyclerView.getAdapter().notifyDataSetChanged();
      }
    });
  }

  //================== 爱心 发送信息工具栏 礼物 截屏 ================

  /**
   * 当看直播界面的 父view（ 相当于屏幕 ） 被点击是，添加爱心
   */
  @OnClick(R.id.root_layout)
  void onRootLayoutClick() {
    //隐藏键盘
//    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//    imm.hideSoftInputFromWindow(messageView.getInputView().getWindowToken(),0);
    periscopeLayout.addHeart();
  }

  /**
   * 右边 图标按钮 显示发送信息工具栏
   */
  @OnClick(R.id.comment_image)
  void onCommentImageClick() {
    showInputView();
  }

  /**
   *  中间 发送礼物
   */
  @OnClick(R.id.present_image)
  void onPresentImageClick() {
    EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
//    message.setReceipt(chatroomId);
//    EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(DemoConstants.CMD_GIFT);
//    message.addBody(cmdMessageBody);
    message.setChatType(EMMessage.ChatType.ChatRoom);
    EMClient.getInstance().chatManager().sendMessage(message);
    showLeftGiftVeiw(EMClient.getInstance().getCurrentUser());
  }


  /**
   *  左边图标 显示信息
   */
  @OnClick(R.id.chat_image)
  void onChatImageClick() {
//    ConversationListFragment fragment = ConversationListFragment.newInstance(anchorId, false);
//    getSupportFragmentManager().beginTransaction()
//        .replace(R.id.message_container, fragment)
//        .commit();
  }

  /**
   * 截屏 按钮
   */
  @OnClick(R.id.screenshot_image)
  void onScreenshotImageClick(){
    Bitmap bitmap = screenshot();
    if (bitmap != null) {
//      ScreenshotDialog dialog = new ScreenshotDialog(this, bitmap);
//      dialog.show();
    }
  }

  /**
   * 截图
   * @return
   */
  private Bitmap screenshot() {
    // 获取屏幕
    View dView = getWindow().getDecorView();
    dView.setDrawingCacheEnabled(true);
    dView.buildDrawingCache();
    Bitmap bmp = dView.getDrawingCache();
    return bmp;
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  //==============直播室 成员列表 适配器================

  /**
   * RecyclerView 适配器 当前房间人 的 头像
   */
  protected class AvatarAdapter extends RecyclerView.Adapter<AvatarViewHolder> {
    //List<String> namelist;
    Context context;
    TestAvatarRepository avatarRepository;

    public AvatarAdapter(Context context, List<String> namelist) {
      //this.namelist = namelist;
      this.context = context;
      avatarRepository = new TestAvatarRepository();
    }

    public AvatarAdapter(Context context) {
      this.context = context;
      avatarRepository = new TestAvatarRepository();
    }

    @Override
    public AvatarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new AvatarViewHolder(
          LayoutInflater.from(context).inflate(R.layout.avatar_list_item, parent , false));
    }

    @Override
    public void onBindViewHolder(AvatarViewHolder holder, final int position) {
      audienceNumView.setText(String.valueOf(memberList.size()));  //显示房间的成员人数
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //showUserDetailsDialog(memberList.get(position));
        }
      });
       String headUrl = HttpConfig.getUrl(memberList.get(position).getPicture_url());//getUserHeadImgUrlByHXAccount( HttpConfig.getUrl(memberList.get(position).getPicture_url()) );
      Glide.with(context).load( headUrl ).placeholder(R.drawable.ease_default_avatar).into(holder.Avatar);
    }

    @Override
    public int getItemCount() {
      if( memberList !=null){
        if( memberList.size() > 9 ){
          return 9 ;  //显示一行
        }else{
          return memberList.size();
        }
      }else{
        return 0;
      }
    }
  }

  /**
   *  viewHolder
   */
  static class AvatarViewHolder extends RecyclerView.ViewHolder {
    @BindView( R.id.avatar )
    ImageView Avatar;
    public AvatarViewHolder( View itemView ) {
      super(itemView);
      ButterKnife.bind( this, itemView );
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
    OrgRequestMemberInfo info = orgMemberList.get( phone );
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
