package app.logic.singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import cn.jpush.android.data.r;

import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore.Video;
import android.text.GetChars;
import android.view.WindowManager;
import app.logic.pojo.CountUnreadInfo;
import app.logic.pojo.NoticeInfo;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.ProviceInfo;
import app.utils.file.YYFileManager;

/*
 * GZYY    2016-8-11  下午6:23:40
 */

public class ZSZSingleton {
	// 本人专属的输出方式
	public static final String OTATO = "－－－－－－－－－－－－OTATO ！！！";

	private ZSZSingleton() {
	}

	private static ZSZSingleton zszSingleton;

	public static ZSZSingleton getZSZSingleton() {
		if (zszSingleton == null) {
			zszSingleton = new ZSZSingleton();
		}
		return zszSingleton;
	}

	private Date selectDate;
	public void setDate(Date date){
		selectDate = date;
	}

	public Date getDate(){
		return selectDate;
	}

	// 获取地区滚轮信息
	private static Map<String, List<String>> map = null;

	public static Map<String, List<String>> getProviceInfo(String string) {
		if (map == null) {
			map = new HashMap<String, List<String>>();
			List<ProviceInfo> list = null;
			Gson gson = new Gson();
			try {
				list = gson.fromJson(string, new TypeToken<List<ProviceInfo>>() {
				}.getType());

			} catch (JsonSyntaxException e) {
				// TODO: handle exception
			}
			if (list != null) {
				for (ProviceInfo info : list) {
					List<String> cities = new ArrayList<String>();

					for (int i = 0; i < info.getCities().length; i++) {
						cities.add(info.getCities()[i]);
					}
					map.put(info.getName(), cities);
				}
			}
		}
		return map;
	}

	// 更改数据库返回时间格式显示,返回yy-dd的形式
	public static String getTimeStyle(String time) {
		if (time.equals("")) {
			return null;
		}
		String[] times = time.split(" ")[0].split("-");

		return times[1] + "月" + times[2] + "日";
	}

	// 筛选出通过的组织
	public static List<OrganizationInfo> getHavePassOrg(List<OrganizationInfo> reply) {
		List<OrganizationInfo> tempInfos = new ArrayList<OrganizationInfo>();
		for (OrganizationInfo info : reply) {
			if (info.getOrg_status() == 10) {
				tempInfos.add(info);
			}
		}
		if (tempInfos.size() < 1) {
			return reply;
		}
		return tempInfos;
	}

	// 定义flagdate集合数据
	private List<CalendarDay> flagDays;

	public void setFlagDays(List<CalendarDay> dates) {
		this.flagDays = dates;
	}

	public List<CalendarDay> getFlagDays() {
		return flagDays;
	}
	
	private CalendarDay today = new CalendarDay();
	
	public CalendarDay getToday() {
		return today;
	}

	public void setToday(CalendarDay today) {
		this.today = today;
	}

	// 设置popupwindow背景显示变暗
	public void backgroundAlpha(Activity activity, float bgAlpha) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = bgAlpha;
		activity.getWindow().setAttributes(lp);
	}

	//定义刷新未读公告的接口
	private OrgInfoInterface infoInterface;

	public interface OrgInfoInterface {
		void getOrgInfo(OrganizationInfo info);
	}

	public void setOrgInfoListener(OrgInfoInterface infoInterface) {
		this.infoInterface = infoInterface;
	}

	public OrgInfoInterface getOrgInfoInterface() {
		return infoInterface;
	}

	// 定义刷新已读公告接口
	private OrgInfoInterfaceEd orgInfoInterfaceEd;

	public interface OrgInfoInterfaceEd {
		void getOrgInfoEd(OrganizationInfo info);
	}

	public void setOrgInfoListenerEd(OrgInfoInterfaceEd ed) {
		this.orgInfoInterfaceEd = ed;
	}

	public OrgInfoInterfaceEd getOrgInfoInterfaceEd() {
		return orgInfoInterfaceEd;
	}

	private OrgUnreadNumberInfo organizationInfo;

	public void setOrganizationInfo(OrgUnreadNumberInfo info) {
		this.organizationInfo = info;
	}

	public OrgUnreadNumberInfo getOrganizationInfo() {
		return organizationInfo;
	}

	// 接口监听开始下载

	private DownloadListener downloadListener;

	public DownloadListener getDownloadListener() {
		return downloadListener;
	}

	public void setDownloadListener(DownloadListener listener) {
		this.downloadListener = listener;
	}

	public interface DownloadListener {
		void downloadStart();
	}

	// 定义添加好友显示点的接口
	private ShowPointImageListener showPointImage;

	public interface ShowPointImageListener {
		void callbackPoint(boolean status);
	}

	public void setShowPointImage(ShowPointImageListener image) {
		this.showPointImage = image;
	}

	public ShowPointImageListener getShowPointImageListener() {
		return showPointImage;
	}

	// 定义获取最新消息提醒接口
	private ShowPointMessageListener messageListener;

	public ShowPointMessageListener getShowPointMessageListener() {
		return messageListener;
	}

	public void setShowPointMessageListener(ShowPointMessageListener listener) {
		this.messageListener = listener;
	}

	public interface ShowPointMessageListener {
		void callbackPoint(boolean status);
	}

	// 定义读完后，通知接口

	private StatusMessageListener statusMessageListener;

	public void setStatusMessageListener(StatusMessageListener statusMessageListener) {
		this.statusMessageListener = statusMessageListener;
	}

	public StatusMessageListener getStatusMessageListener() {
		return statusMessageListener;
	}

	public interface StatusMessageListener {
		void callbackStatusUpdata(int unreadCount);
	}

	// 定义下载完成的接口回调
	private int haveComplete = 0;

	public int getHaveComplete() {
		return haveComplete;
	}

	public void setHaveComplete(int i) {
		this.haveComplete = i;
	}

	private StatusDownloadFileCompleteListener mStatusDownloadFileCompleteListener;

	public void setStatusDownloadFileCompleteListener(StatusDownloadFileCompleteListener listener) {
		this.mStatusDownloadFileCompleteListener = listener;
	}

	public StatusDownloadFileCompleteListener getStatusDownloadFileCompleteListener() {
		return mStatusDownloadFileCompleteListener;
	}

	public interface StatusDownloadFileCompleteListener {
		void onCallBack(String url);
	}

	// 定义下载更新的进度
	private UpdataDownloadProgressListener updataDownloadProgressListener;

	public void setUpdataDownloadProgressListener(UpdataDownloadProgressListener listener) {
		this.updataDownloadProgressListener = listener;
	}

	public UpdataDownloadProgressListener getUpdataDownloadProgressListener() {
		return updataDownloadProgressListener;
	}

	public interface UpdataDownloadProgressListener {
		void onCallBack(int plan);
	}

	// （推送）更新公告未读统计接口(homeActivity)
	private UpdataNoticeCountListener countListener;

	public void setUpdataNoticeCountListener(UpdataNoticeCountListener listener) {
		this.countListener = listener;
	}

	public UpdataNoticeCountListener getUpdataNoticeCountListener() {
		return countListener;
	}

	public interface UpdataNoticeCountListener {
		void onCallBack();
	}

	// 推送）更新公告未读统计接口(FragmentUnread)
	private NotiToFragmentUnreadListener notiToFragmentUnreadListener;

	public interface NotiToFragmentUnreadListener {
		void onCallBack();
	}

	public NotiToFragmentUnreadListener getNotiToFragmentUnreadListener() {
		return notiToFragmentUnreadListener;
	}

	public void setNotiToFragmentUnreadListener(NotiToFragmentUnreadListener listener) {
		notiToFragmentUnreadListener = listener;
	}

	// 用户点击未读公告后的
	private HaveReadToRefreshListener haveReadToRefreshListener;

	public void setHaveReadToRefreshListener(HaveReadToRefreshListener listener) {
		this.haveReadToRefreshListener = listener;
	}

	public HaveReadToRefreshListener getHaveReadToRefreshListener() {
		return haveReadToRefreshListener;
	}

	public interface HaveReadToRefreshListener {
		void onCallBack(int unReadCount);
		void onCallBackUnreanList(List<CountUnreadInfo> list);
	}

	// 发了公告后回调
	private SendNoticeUpdataUnreadListener sendNoticeUpdataUnreadListener;

	public void setSendNoticeUpdataUnreadListener(SendNoticeUpdataUnreadListener listener) {
		this.sendNoticeUpdataUnreadListener = listener;
	}

	public SendNoticeUpdataUnreadListener getSendNoticeUpdataUnreadListener() {
		return sendNoticeUpdataUnreadListener;
	}

	public interface SendNoticeUpdataUnreadListener {
		void onCallBack();
	}

	// 触发日程显示蓝色接口
	private changeOrgToBlUListener blUListener;

	public void setchangeOrgToBlUListener(changeOrgToBlUListener listener) {
		this.blUListener = listener;
	}

	public changeOrgToBlUListener getchangeOrgToBlUListener() {
		return blUListener;
	}

	public interface changeOrgToBlUListener {
		void onCallBack(CalendarDay day);
	}

}
