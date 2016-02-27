package com.coderqi.publicutil.voice;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.example.android_robot_01.MainActivity;
//import com.example.voicetoword.MainActivity;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;

/**
 * 识锟斤拷氐锟斤拷锟斤拷锟斤拷锟�
 */
public class MyRecognizerDialogLister implements RecognizerDialogListener{
	private Context context;
	private String resultStr = "";
	public MyRecognizerDialogLister(Context context)
	{
		this.context = context;
	}
	//锟皆讹拷锟斤拷慕锟斤拷锟截碉拷锟斤拷锟斤拷锟斤拷锟缴癸拷执锟叫碉拷一锟斤拷锟斤拷锟斤拷锟斤拷失锟斤拷执锟叫第讹拷锟斤拷锟斤拷锟斤拷
	@Override
	public void onResult(RecognizerResult results, boolean isLast) {
		// TODO Auto-generated method stub
		String text = JsonParser.parseIatResult(results.getResultString());
		resultStr += text;
		  if (isLast) {
			System.out.println(resultStr);
			Toast.makeText(context, resultStr, Toast.LENGTH_LONG).show();
			Intent intent = new Intent("yzc.robot.VOICE_MESSAGE").putExtra("VoiceMsg", resultStr);
			context.sendBroadcast(intent);
			resultStr = "";
		}else {

		}
//		MainActivity.mMsg.setText(MainActivity.mMsg.getText().toString()+text);
	}
	/**
	 * 识锟斤拷氐锟斤拷锟斤拷锟�.
	 */
	@Override
	public void onError(SpeechError error) {
		// TODO Auto-generated method stub
		int errorCoder = error.getErrorCode();
		switch (errorCoder) {
		case 10118:
			System.out.println("user don't speak anything");
			break;
		case 10204:
			System.out.println("can't connect to internet");
			break;
		default:
			break;
		}
	}

	

}
