package app.utils.dialog;

import org.ql.app.alert.AlertDialog;

import android.R.integer;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import app.yy.geju.R;

/*
 * GZYY    2016-11-3  上午11:30:50
 */

public abstract class YYDialogEditAndText extends AlertDialog implements android.view.View.OnClickListener {

	public static final int TEXTVIEWMODEL = 0;
	public static final int EDITTEXTMODEL = 1;

	// 0是默认，1是TextView,2是EditText
	private int selectModel = 0;
	private Context context;

	private String title;
	private String message;
	private String sureBtnText;
	private String cancelBtnText;

	private View contentView;
	private TextView titleTv, messageTv;
	private EditText messageEd;
	private Button sureBtn, cancelBtn;
	private View lineView;

	public abstract void SureOnClick();

	public abstract void CancelOnClick();

	public void setSelectModel(int model) {
		this.selectModel = model;
	}

	public YYDialogEditAndText(Context context) {
		super(context);
		this.context = context;
	}

	public YYDialogEditAndText(Context context, int model, String title, String message, String sure, String cancel) {
		super(context);
		this.context = context;
		this.selectModel = model;
		this.title = title;
		this.message = message;
		this.sureBtnText = sure;
		this.cancelBtnText = cancel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		setData();
		switch (selectModel) {
		case 0:
			messageEd.setVisibility(View.GONE);
			lineView.setVisibility(View.GONE);
			break;
		case 1:
			messageTv.setVisibility(View.GONE);
			break;
		case 2:
			break;
		}
	}

	private void initView() {
		contentView = LayoutInflater.from(context).inflate(R.layout.dialog_yyview, null);
		titleTv = (TextView) contentView.findViewById(R.id.dialog_title);
		messageTv = (TextView) contentView.findViewById(R.id.dialog_message);
		messageEd = (EditText) contentView.findViewById(R.id.dialog_editText);
		sureBtn = (Button) contentView.findViewById(R.id.sure_btn);
		cancelBtn = (Button) contentView.findViewById(R.id.cancel_btn);
		lineView = contentView.findViewById(R.id.line_view);

		sureBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
	}

	private void setData() {
		if (title != null) {
			this.setTitle(title);
		}

		if (message != null) {
			messageTv.setText(message);
			messageEd.setText(message);
			messageEd.setSelection(messageEd.getText().toString().length());
		}

		if (sureBtnText != null) {
			sureBtn.setText(sureBtnText);
		}
		if (cancelBtnText != null) {
			cancelBtn.setText(cancelBtnText);
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.sure_btn) {
			SureOnClick();
			dismiss();
		} else if (id == R.id.cancel_btn) {
			CancelOnClick();
			dismiss();
		}

	}

	public void setSureBtnText(String sure) {
		this.sureBtnText = sure;
	}

	public void setCancelBtnText(String cancel) {
		this.cancelBtnText = cancel;
	}

	public Button getSureBtn() {
		return sureBtn;
	}

	public Button getCancelBtn() {
		return cancelBtn;
	}

}
