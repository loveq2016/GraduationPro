package com.hyphenate.easeui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;

import org.ql.utils.QLToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.DepartmentInfo;
import app.utils.common.Listener;
import app.utils.image.QLImageHelper;
import app.view.YYListView;
import app.yy.geju.R;

import static com.hyphenate.easeui.EaseConstant.CHATTYPE_GROUP;

/*
 * GZYY    2016-8-25  上午10:34:41
 */

public class EaseDialogView extends Dialog implements OnItemClickListener {

	private Context context;
	private List<String> listDpm;
	private LayoutInflater inflater;
	private YYListView listView;
	private EMMessage emMessage;
	private CallBack successListener;
	private int chatType;
	private String toChatUsername;

	public EaseDialogView(Context context, int themeResId, List<String> listDpm, EMMessage emMessage,int chatType,String toChatUsername) {
		super(context, themeResId);
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.listDpm = listDpm;
		this.emMessage = emMessage;
		this.chatType = chatType;
		this.toChatUsername = toChatUsername;
	}

	public EaseDialogView(Context context, int themeResId) {
		super(context, themeResId);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_set_dpm);

		// 解决dialog窗口背景黑色问题
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		listView = (YYListView) findViewById(R.id.dpm_listView);

		adapter.setDatas(listDpm);

		if (listDpm.size() > 5) {
			WindowManager manager = ((Activity) context).getWindowManager();
			Display display = manager.getDefaultDisplay();
			WindowManager.LayoutParams params = this.getWindow().getAttributes();
			params.width = (int) ((display.getWidth()) * 0.8);
			params.height = (int) ((display.getHeight()) * 0.45);
			this.getWindow().setAttributes(params);
		}

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(false, false);
	}

	private YYBaseListAdapter<String> adapter = new YYBaseListAdapter<String>(context) {

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_dialog_select_dpm, null);
				saveView("dpm_tv", R.id.dpm_tv, convertView);
			}
			String info = adapter.getItem(position);
			if (info != null) {
				setTextToViewText(info, "dpm_tv", convertView);
			}

			return convertView;

		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		DepartmentInfo info = adapter.getItem(position - 1);
//		if (info != null) {
//			// QLToastUtils.showToast(context, info.getDpm_name() +
//			// info.getDpm_id() + "组织" + org_id);
//			OrganizationController.addMemberToDPM(context, org_id, info.getDpm_id(), user_id, new Listener<Integer, String>() {
//				@Override
//				public void onCallBack(Integer status, String reply) {
//					successListener.onSuccess();
//					if (status != 1) {
//						String msg = reply == null ? "添加失败" : reply;
//						QLToastUtils.showToast(context, msg);
//						return;
//					}
//
//				}
//			});
//
//		}
		String menu = adapter.getItem(position - 1);
		if (!TextUtils.isEmpty(menu)){
			if (menu.equals("复制")){
				ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				// 将文本内容放到系统剪贴板里。
				cm.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
			}else if(menu.equals("撤回")){
				EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
				// 如果是群聊，设置chattype，默认是单聊
				if (emMessage.getChatType() == EMMessage.ChatType.GroupChat){
					cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
				}
				String action="REVOKE_FLAG";
				EMCmdMessageBody cmdBody=new EMCmdMessageBody(action);
				// 设置消息body
				cmdMsg.addBody(cmdBody);
				// 设置要发给谁，用户username或者群聊groupid
				cmdMsg.setTo(toChatUsername);
				// 通过扩展字段添加要撤回消息的id
				cmdMsg.setAttribute("msgid",emMessage.getMsgId());

				EMClient.getInstance().chatManager().sendMessage(cmdMsg);
				if (successListener !=null)
					successListener.onSuccess(0);
			}else if(menu.equals("删除")){
				if (successListener !=null)
					successListener.onSuccess(1);
			}else if(menu.equals("保存")){
				EMImageMessageBody imgBody = (EMImageMessageBody) emMessage.getBody();
				if (!TextUtils.isEmpty(imgBody.getLocalUrl())){
					String string = QLImageHelper.getPhotoDir() +"/"+ imgBody.getFileName();
					copyFile(imgBody.getLocalUrl(),string);
				}
			}
		}
		this.dismiss();
	}

	public void setCallBack(CallBack listener) {
		this.successListener = listener;
	}

	// 回调移动成功
	public interface CallBack {
		void onSuccess(int messageType);//0撤回 1删除
	}

	/**
	 * 复制单个文件
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				QLToastUtils.showToast(context,"文件保存在"+newPath);
			}else
				QLToastUtils.showToast(context,"请先查看图片下载");

		}
		catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}
}
