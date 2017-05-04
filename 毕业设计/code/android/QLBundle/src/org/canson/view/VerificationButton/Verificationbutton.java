package org.canson.view.VerificationButton;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.ql.bundle.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Verificationbutton extends Button {

	private long lenght = 60 * 1000;// 倒计时长度,默认60秒
	private String textafter = "秒";
	private String textbefore = "点击获取验证码";
	private final String TIME = "time";
	private final String CTIME = "ctime";
	private OnClickListener mOnclickListener;
	private Timer t;
	private TimerTask tt;
	private long time;
	Map<String, Long> map = new HashMap<String, Long>();

	public Verificationbutton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// setOnClickListener(this);
	}

	@SuppressLint("HandlerLeak")
	Handler han = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Verificationbutton.this.setText(time / 1000 + textafter);
			time -= 1000;
			if (time < 0) {   //60秒倒计时结束
				Verificationbutton.this.setEnabled(true);  //按钮重新设置为可用
				Verificationbutton.this.setText("重新获取");
				Verificationbutton.this.setBackgroundResource(R.drawable.shape_default_btn);
				clearTimer();
			}
		}
	};

	/**
	 * 初始化时间 和开启计时任务
	 */
	private void initTimer() {
		time = lenght;
		t = new Timer();
		tt = new TimerTask() {
			@Override
			public void run() {
				Log.e("yung", time / 1000 + "");
				han.sendEmptyMessage(0x01);
			}
		};
	}


	/*
	 * @Override public void setOnClickListener(OnClickListener l) { if (l
	 * instanceof Verificationbutton) { super.setOnClickListener(l); } else
	 * this.mOnclickListener = l; }
	 */

	/**
	 * 开启计时任务
	 */
	public void startTimer() {
		/*
		 * if (mOnclickListener != null) mOnclickListener.onClick(v);
		 */
		initTimer();
		this.setText(time / 1000 + textafter);
		this.setEnabled(false);  //开启计时后（60秒内），按钮变为不可用
		this.setBackgroundResource(R.drawable.getcheckcode_down);
		this.setTextColor(Color.WHITE);
		t.schedule(tt, 0, 1000);  //启动计时
		// t.scheduleAtFixedRate(task, delay, period);
	}

	/**
	 * 解除计时任务（必须做，否则报错）
	 */
	private void clearTimer() {
		if (tt != null) {
			tt.cancel();
			tt = null;
		}
		if (t != null)
			t.cancel();
		t = null;
	}

	/**
	 * 和activity的onCreate()方法同步
	 */
	public void onCreate(Bundle bundle) {
		Log.e("yung", App.map + "");
		if (App.map == null)
			return;
		if (App.map.size() <= 0)// 这里表示没有上次未完成的计时
			return;
		long time = System.currentTimeMillis() - App.map.get(CTIME) - App.map.get(TIME);
		App.map.clear();
		if (time > 0)    //表示上一次请求到现在（当前时刻）的时间差已经超过60秒的时长
			return;
		else {           //否则要计算上上一次的时间（超过60秒时长才可以第二次发起获取验证码求情）
			initTimer();
			this.time = Math.abs(time);  //初始化时间（不是 60s了）
			t.schedule(tt, 0, 1000);     //开始计时
			this.setBackgroundResource(R.drawable.getcheckcode_down);
			this.setText(time + textafter);
			this.setEnabled(false);  //60秒没有结束按钮还是不可用
		}
	}

	/**
	 * 和activity的onDestroy()方法同步
	 */
	public void onDestroy() {
		if (App.map == null)
			App.map = new HashMap<String, Long>();
		App.map.put(TIME, time);
		App.map.put(CTIME, System.currentTimeMillis());
		clearTimer(); //关闭计时任务
		Log.e("yung", "onDestroy");
	}

	/** * 设置计时时候显示的文本 */
	public Verificationbutton setTextAfter(String text1) {
		this.textafter = text1;
		return this;
	}

	/** * 设置点击之前的文本 */
	public Verificationbutton setTextBefore(String text0) {
		this.textbefore = text0;
		this.setText(textbefore);
		return this;
	}

	/**
	 * 设置到计时长度
	 * 
	 * @param lenght
	 *            时间 默认毫秒
	 * @return
	 */
	public Verificationbutton setLenght(long lenght) {
		this.lenght = lenght;
		return this;
	}
}