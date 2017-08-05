package com.example.mobipay;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class NFC_send extends Activity implements CreateNdefMessageCallback{
	private NfcAdapter mNfcAdapter;//nfc适配器
	private Context mcontext;
	private String sendContent="";//发送内容
	private TextView textView;
	private Handler handler=null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_send);
		textView = (TextView) findViewById(R.id.textview);
		mcontext = this;
		handler=new Handler();  
		checkNfcFunction();//检测nfc功能硬件状态
		mNfcAdapter.setNdefPushMessageCallback(this,this);//nfc信息发送回掉函数
	}

	private void checkNfcFunction() {
		// TODO Auto-generated method stub
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if(mNfcAdapter == null){
			//不支持nfc功能，强行停止应用
			Toast.makeText(mcontext, "你的设备不支持nfc",Toast.LENGTH_LONG);
			finish();
		}else{
			if(!mNfcAdapter.isEnabled()){
				//设备未开启，转到开启nfc的设置页面
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				Toast.makeText(mcontext, "你的nfc未开启",Toast.LENGTH_LONG);
				startActivity(intent);

				return ;
			}else if(!mNfcAdapter.isNdefPushEnabled()){
				//nfc适配器未被激活，则强行停止应用
				Toast.makeText(mcontext, "你的数据推送无法实现",Toast.LENGTH_LONG);
				finish();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// TODO Auto-generated method stub	
		//通过字符串内容生成可发送的ndefMessage
		try {
			sendContent = Account.encapsulateMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte id = 0x00;
		//调用生成ndefMessage的函数
		NdefMessage msg = MyNDEFMsgGet.getNdefMsg_RTD_URI(sendContent, id);
		handler.post(runnableUi);
					
		return msg;
	}
	
	Runnable   runnableUi=new  Runnable(){  
        @Override  
        public void run() {  
            //更新界面  
            textView.setText(sendContent);  
        }  
          
    }; 
}
