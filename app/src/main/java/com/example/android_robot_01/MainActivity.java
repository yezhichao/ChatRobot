package com.example.android_robot_01;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.coderqi.publicutil.voice.VoiceToWord;
import com.example.android_robot_01.bean.ChatMessage;
import com.example.android_robot_01.bean.ChatMessage.Type;
//import com.example.voicetoword.MainActivity;
import com.yzc.utils.HttpUtils;

public class MainActivity extends Activity
{
	/**
	 * 展示消息的listview
	 */
	private ListView mChatView;
	/**
	 * 文本域
	 */
	public static EditText mMsg;
	/**
	 * 存储聊天消息
	 */
	private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
	/**
	 * 适配器
	 */
	private ChatMessageAdapter mAdapter;
	/**
	 * 语音消息的广播接收器
	 */
	private VoiceReceiver voiceReceiver;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			ChatMessage from = (ChatMessage) msg.obj;
			mDatas.add(from);
			mAdapter.notifyDataSetChanged();
			mChatView.setSelection(mDatas.size() - 1);
		};
	};

	/**
	 * 命令观察者，用于检测发送的信息是否有打开应用的意图
	 */
	private CommandObserver mCommandObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_chatting);
		
		initView();
		
		mAdapter = new ChatMessageAdapter(this, mDatas);
		mChatView.setAdapter(mAdapter);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("yzc.robot.VOICE_MESSAGE");
		voiceReceiver = new VoiceReceiver();
		registerReceiver(voiceReceiver,intentFilter);

		mCommandObserver = new CommandObserver();

	}

	private void initView()
	{
		mChatView = (ListView) findViewById(R.id.id_chat_listView);
		mMsg = (EditText) findViewById(R.id.id_chat_msg);
		mDatas.add(new ChatMessage(Type.INPUT, "Hello~我是小白。"));
	}

	public void sendMessage(View view)
	{
		final String msg = mMsg.getText().toString();
		if (TextUtils.isEmpty(msg))
		{
			Toast.makeText(this, "您还没有填写信息呢...", Toast.LENGTH_SHORT).show();
			return;
		}

		ChatMessage to = new ChatMessage(Type.OUTPUT, msg);
		to.setDate(new Date());
		mDatas.add(to);

		mAdapter.notifyDataSetChanged();
		mChatView.setSelection(mDatas.size() - 1);

		mMsg.setText("");

		// 关闭软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive())
		{
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
			// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
		}

		new Thread()
		{
			public void run()
			{
				ChatMessage from = null;
				try
				{
					from = HttpUtils.sendMsg(msg);
				} catch (Exception e)
				{
					from = new ChatMessage(Type.INPUT, "服务器挂了呢...");
				}
				Message message = Message.obtain();
				message.obj = from;
				mHandler.sendMessage(message);
			};
		}.start();

	}

	public void sendVoiceMessage(View view)
	{
		final String msg = mMsg.getText().toString();
		if (TextUtils.isEmpty(msg))
		{
			Toast.makeText(this, "您还没有填写信息呢...", Toast.LENGTH_SHORT).show();
			return;
		}

		ChatMessage to = new ChatMessage(Type.OUTPUT, msg);
		to.setDate(new Date());
		mDatas.add(to);

		mAdapter.notifyDataSetChanged();
		mChatView.setSelection(mDatas.size() - 1);

		mMsg.setText("");

		if (!mCommandObserver.onCommand(MainActivity.this,msg)) {
			new Thread() {
				public void run() {
					ChatMessage from = null;
					try {
						from = HttpUtils.sendMsg(msg);
					} catch (Exception e) {
						from = new ChatMessage(Type.INPUT, "服务器挂了呢...");
					}
					Message message = Message.obtain();
					message.obj = from;
					mHandler.sendMessage(message);
				}

				;
			}.start();
		}

	}
	
	public void sendSpeech(View view) {
		VoiceToWord voice = new VoiceToWord(MainActivity.this,"534e3fe2");
		voice.GetWordFromVoice();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(voiceReceiver);
	}

	class VoiceReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String text = intent.getStringExtra("VoiceMsg");
				MainActivity.mMsg.setText(MainActivity.mMsg.getText().toString() + text);
				sendVoiceMessage(mMsg);
		}
	}

}
