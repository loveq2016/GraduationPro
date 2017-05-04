/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.logic.call.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;
import app.logic.videocall.Activity.VideoCallActivity;
import app.logic.voice.activity.VoiceCallActivity;

/**
 * 视频通话和语音通话的广播
 */
public class CallReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		if(!EMClient.getInstance().isLoggedInBefore())//看不懂这里的意思
		    return;

		//username
		String from = intent.getStringExtra("from");
		//call type
		String type = intent.getStringExtra("type");
		if("video".equals(type)){ //video call
			Intent intent1 = new Intent();
			intent1.setClass(context , VideoCallActivity.class);
			intent1.putExtra(VideoCallActivity.USERNAME , from);
			intent1.putExtra(VideoCallActivity.ISCOMINGCALL , true);
//			intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);

		}else{ //voice call
			Intent intent2 = new Intent();
			intent2.setClass(context , VoiceCallActivity.class);
			intent2.putExtra(VoiceCallActivity.USERNAME , from);
			intent2.putExtra(VoiceCallActivity.ISCOMINGCALL , true);
//			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
		}
		EMLog.d("CallReceiver", "app received a incoming call");
	}
}
