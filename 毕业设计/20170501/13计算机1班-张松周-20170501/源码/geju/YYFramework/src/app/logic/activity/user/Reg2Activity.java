//package app.logic.activity.user;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.QLConstant;
//import org.ql.activity.customtitle.ActActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import app.logic.activity.ActTitleHandler;
//import app.logic.controller.UserManagerController;
//import app.logic.pojo.UserInfo;
//import app.utils.common.Listener;
//import app.utils.helpers.SharepreferencesUtils;
//import app.yy.geju.R;
//import cn.jpush.android.api.JPushInterface;
//
//import com.hyphenate.EMCallBack;
//import com.hyphenate.chat.EMClient;
//import com.hyphenate.exceptions.HyphenateException;
//
//
//public class Reg2Activity extends ActActivity {
//
//	private String phoneNum, checkNum;
//	private EditText reg2_loginpw, reg2_loginpw_ensure, reg2_invitecode;
//	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//	private SharepreferencesUtils utils;
//	private Handler mhanler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 000:
//				Toast.makeText(Reg2Activity.this, "注册成功", 0).show();
//				//跳转到完善个人信息页面
//				Intent intent = new Intent(Reg2Activity.this,UserInfoActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//				intent.putExtra(UserInfoActivity.kFROM_REGEDIT, true);
//				startActivity(intent);
//				Reg2Activity.this.finish();
//				break;
//			case 111:
//				findViewById(R.id.loadingView).setVisibility(View.GONE);
//			default:
//				break;
//			}
//
//		};
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		ActTitleHandler titleHandler = new ActTitleHandler();
//		setAbsHandler(titleHandler);
//		setContentView(R.layout.activity_reg2);
//		setTitle("验证账号信息");
//		// 获取传过来的手机号码和验证码
//		utils = new SharepreferencesUtils(Reg2Activity.this);
//		phoneNum = getIntent().getStringExtra("phone");
//		initview();
//	}
//
//	private void initview() {
//		reg2_loginpw = (EditText) findViewById(R.id.reg2_loginpw);
//		reg2_loginpw_ensure = (EditText) findViewById(R.id.reg2_loginpw_ensure);
//		onClick onclick = new onClick();
//		findViewById(R.id.reg2_complete).setOnClickListener(onclick);
//	}
//
//	class onClick implements OnClickListener {
//
//		@Override
//		public void onClick(View view) {
//			final String loginpw = reg2_loginpw.getText().toString();
//			String loginpw_ensure = reg2_loginpw_ensure.getText().toString();
//			switch (view.getId()) {
//			case R.id.reg2_complete:
//				if (!loginpw.equals("") && !loginpw_ensure.equals("")) {
//					if (loginpw.equals(loginpw_ensure)) {
//						if (loginpw.length() >= 6) {
//							findViewById(R.id.loadingView).setVisibility(View.VISIBLE);
//							UserManagerController.Register(Reg2Activity.this,phoneNum, loginpw,"",
//									new Listener<Integer, List<UserInfo>>() {
//										@Override
//										public void onCallBack(Integer status,
//												List<UserInfo> reply) {
//											findViewById(R.id.loadingView).setVisibility(View.GONE);
//											if (status == 1) {
//												QLConstant.client_id = reply.get(0).getWp_member_info_id();
//												JPushInterface.setAlias(Reg2Activity.this, reply.get(0).getWp_member_info_id(), null);  //极光推送设置别名
//
//												utils.setUserName(phoneNum);
//												utils.setPassword("");
//												utils.setRemenber(false);
//												createIMAccount(phoneNum);
//											} else if (status == -1) {
//												Toast.makeText(Reg2Activity.this,"注册失败,请稍后再试", 0).show();
//											} else if (status == -2) {
//												Toast.makeText(Reg2Activity.this,"该用户已注册", 0).show();
//											}
//										}
//									});
//						} else {
//							Toast.makeText(Reg2Activity.this, "密码至少由6位数组成", 0).show();
//						}
//					} else {
//						Toast.makeText(Reg2Activity.this, "两次输入的密码不一致", 0).show();
//					}
//				} else {
//					Toast.makeText(Reg2Activity.this, "密码不能为空", 0).show();
//				}
//
//				break;
//			default:
//				break;
//			}
//		}
//	}
//
//	private void createIMAccount(final String phoneNum){
//		final String psw = "wudi#"+phoneNum;
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
////					EMChatManager.getInstance().createAccountOnServer(phoneNum, psw);
//					EMClient.getInstance().createAccount(phoneNum, psw);//同步方法
//				} catch (HyphenateException e) {
//					e.printStackTrace();
//				}
//				// im聊天登录
//				EMClient.getInstance().login(phoneNum,psw,new EMCallBack() {// 回调
//									@Override
//									public void onSuccess() {runOnUiThread(new Runnable() {
//											public void run() {
//												Log.v("hhhh","im登陆成功");
////												EMGroupManager.getInstance().loadAllGroups();
////												EMChatManager.getInstance().loadAllConversations();
//												EMClient.getInstance().groupManager().getAllGroups();
//												EMClient.getInstance().chatManager().loadAllConversations();
//
//												mhanler.sendEmptyMessage(000);
//											}
//										});
//									}
//									@Override
//									public void onProgress(int progress,String status) {
//
//									}
//									@Override
//									public void onError(int code,String message) {
//										mhanler.sendEmptyMessage(111);
//										Log.v("hhhh","登陆聊天服务器失败！");
//									}
//								});
//			}
//		}).start();
//	}
//
//}
