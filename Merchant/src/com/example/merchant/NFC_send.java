package com.example.merchant;

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
	private NfcAdapter mNfcAdapter;
	private Context mcontext;
	private String sendContent = "";
	private TextView textView;
	private Handler handler=null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("onCreate", "1");
		super.onCreate(savedInstanceState);
		Log.i("onCreate", "2");
		setContentView(R.layout.activity_nfc_send);
		Log.i("onCreate", "3");
		textView = (TextView) findViewById(R.id.textview);
		Log.i("onCreate", "4");
		mcontext = this;
		handler=new Handler();  
		Log.i("onCreate", "5");
		checkNfcFunction();
		Log.i("onCreate", "6");
		mNfcAdapter.setNdefPushMessageCallback(this,this);
		Log.i("onCreate", "7");
	}

	private void checkNfcFunction() {
		// TODO Auto-generated method stub
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if(mNfcAdapter == null){
			//不支持nfc功能
			Toast.makeText(mcontext, "你的设备不支持nfc",Toast.LENGTH_LONG);
			finish();
		}else{
			if(!mNfcAdapter.isEnabled()){
				//设备未开启
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				Toast.makeText(mcontext, "你的nfc未开启",Toast.LENGTH_LONG);
				startActivity(intent);
				
				return ;
				//Toast.makeText(mcontext, "你的nfc未开启",Toast.LENGTH_LONG);
				//finish();
			}else if(!mNfcAdapter.isNdefPushEnabled()){
				//......
				//Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
				//startActivity(intent);
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
		Log.i("createNdefMessage", "1");
		
		try {
			sendContent = Merchant.encapsulateMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("createNdefMessage", "2");
		Log.i("createNdefMessage", "3");
		byte id = 0x00;
		Log.i("createNdefMessage", "4");
		NdefMessage msg = MyNDEFMsgGet.getNdefMsg_RTD_URI(sendContent, id);
		Log.i("createNdefMessage", "5");
		handler.post(runnableUi);
		Log.i("createNdefMessage", "5");
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
