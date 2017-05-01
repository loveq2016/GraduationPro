package app.logic.activity.werben;

import org.ql.activity.customtitle.ActActivity;
/*
 * GZYY    2016-8-3  上午10:14:03
 */

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.yy.geju.R;

public class WerbenActivity extends ActActivity {

	private EditText text_ed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActTitleHandler handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.activity_werben);

		handler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
		TextView tv = (TextView) handler.getLeftLayout().findViewById(R.id.left_tv);
		tv.setText("取消");
		setTitle("发公告");
		initView();
	}

	private void initView() {
		
		text_ed = (EditText)findViewById(R.id.text_et);
		
		text_ed.setText("dsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgsdsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgsdsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgsdsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgsdsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgsdsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgsdsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgsdsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgsdsaasdaldm;d,;a,dl;sd;lsmfndlfnsdlgnnnklnslfkn;h;gl;dfngkldnglkdngkdnglkdnglkdngdkngkdlgn;g'wkfrkfsjflilksjfgs");
	}

}
