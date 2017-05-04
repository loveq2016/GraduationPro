/*
 * Copyright (C) 2012 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ql.utils;

import org.ql.bundle.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**     
 * 类名称：QLToastUtils   
 * 类描述：自定义Toast
 * 创建人：anan   
 * 创建时间：2012-12-22 下午6:11:39   
 * 修改人：sim
 * 修改时间：2014-03-20  
 * 修改备注：   
 * @version        
 * */
public class QLToastUtils {
	public static void showToast(Context ctx, int resID) {
		showToast(ctx, Toast.LENGTH_SHORT, resID);
	}

	public static void showToast(Context ctx, String text) {
		showToast(ctx, Toast.LENGTH_SHORT, text);
	}

	public static void showToast(Context ctx, int duration, int resID) {
		showToast(ctx, duration, ctx.getString(resID));
	}

	public static void showToast(Context ctx, int duration, String text) {
		if(null == ctx || TextUtils.isEmpty(text))
			return;
		if (ctx instanceof Activity && !((Activity) ctx).isFinishing()) {
			Toast toast = Toast.makeText(ctx, text, duration);
			View mNextView = toast.getView();
			if (mNextView != null)
				mNextView.setBackgroundResource(R.drawable.toast_frame);
			TextView tv = (TextView) mNextView.findViewById(android.R.id.message);
			tv.setTextColor(Color.WHITE);
			toast.show();
		}
	}
}
