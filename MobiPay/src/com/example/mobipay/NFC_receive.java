package com.example.mobipay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class NFC_receive extends Activity {
	private NfcAdapter mNfcAdapter;//nfc适配器
	private TextView payloadTV;
	private PendingIntent  mPendingIntent;
	private Context mcontext;
	String NFCString = "";//接受得到的字符串内容
	String merchant_id ="";
	String detail = "";
	String amount = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_receive);
		mcontext = this;
		payloadTV = (TextView) findViewById(R.id.payload);
		checkNfcFunction();//检查nfc功能
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
		resolveIntent(getIntent());//处理得到的nfcIntent
		
		final EditText Password = new EditText(this);
		final Dialog alertDialog = new AlertDialog.Builder(this).setTitle("请输入").setIcon(
			     android.R.drawable.ic_dialog_info).setView(
			     Password).setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			    	 @Override  
	                    public void onClick(DialogInterface dialog, int which) {  
	                    	 Account.payPassword = "";
	                    	 Account.payPassword = Password.getText().toString();
	                         Password.setText("");
	                         int flag = -2;			                        	
	                         try {
	                 				flag = Account.payWithPassword(merchant_id, amount, detail);
	                 			} catch (Exception e) {
	                 				// TODO Auto-generated catch block
	                 				e.printStackTrace();
	                 			}                         	                 							
	                         if(flag==0)
	                         {
	                        	 new AlertDialog.Builder(NFC_receive.this)
	             						.setTitle("提示") .setMessage("交易成功")
	             						.setPositiveButton("确定", null)
	             						.show();
	                         }
	                         else if(flag == -1)
	                         {
	                        	 new AlertDialog.Builder(NFC_receive.this)
									.setTitle("错误") .setMessage("手机时间与服务器不匹配")
									.setPositiveButton("确定", null)
									.show();
	                         }
	                         else if(flag == -2)
	                         {
	                        	 new AlertDialog.Builder(NFC_receive.this)
									.setTitle("错误") .setMessage("余额不足")
									.setPositiveButton("确定", null)
									.show();
	                         }
	                         else if(flag <= -3)
	                         {
	                        	 int chance = -flag-2;
	                        	 new AlertDialog.Builder(NFC_receive.this)
									.setTitle("错误") .setMessage("密码错误，剩余尝试次数："+chance+"次！")
									.setPositiveButton("确定", null)
									.show();
	                         }
	                         else if(flag > 0)
	                         {
	                        	 int time = flag;
	                        	 new AlertDialog.Builder(NFC_receive.this)
									.setTitle("错误") .setMessage("账户已锁定，剩余时间："+time+"秒！")
									.setPositiveButton("确定", null)
									.show();
	                         }
	                    }  
	                })
			     .setNegativeButton("取消", new DialogInterface.OnClickListener() {  
	                    @Override  
	                    public void onClick(DialogInterface dialog, int which) {  
	                        // TODO Auto-generated method stub   
	                    }  
	                }).create();
		
		final Button button1=(Button)findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick(View v)
			{
	    		alertDialog.show();
			}
	    });
		
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
			}else if(!mNfcAdapter.isNdefPushEnabled()){
				Toast.makeText(mcontext, "你的数据推送无法实现",Toast.LENGTH_LONG);
				finish();
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		/*if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
			resolveIntent(intent);
		}*/
		resolveIntent(intent);//处理接收到的intent
	}
	
	private void resolveIntent(Intent intent) {//处理intent内容
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){//核对得到的intent类型与所需要的类型是否相同
			NdefMessage[] messages = null;
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);//获得标签
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if(rawMsgs != null){
				messages = new NdefMessage[rawMsgs.length];
				for(int i=0;i<rawMsgs.length;i++){
					messages[i] = (NdefMessage)rawMsgs[i];//从标签中获得信息
				}
			}else{
				byte[] empty = new byte[]{};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,empty,empty,empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
				messages = new NdefMessage[]{msg};
			}
			processNDEFMsg(messages);
		}else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){			
		}else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
		}else{
		}
	}

	private void processNDEFMsg(NdefMessage[] messages) {
		// TODO Auto-generated method stub
		//处理ndef信息
		if(messages == null || messages.length == 0){
			return ;
		}
		for(int i=0;i<messages.length;i++){
			int length = messages[i].getRecords().length;
			NdefRecord[] records = messages[i].getRecords();
			for(int j=0;j<length;j++){
				for(NdefRecord record:records){
					parseRTDUriRecord(record);
				}
			}
		}
	}
	
	private void parseRTDUriRecord(NdefRecord record) {
		//解析记录中的信息
		// TODO Auto-generated method stub
		Uri uri = MyNFCRecordParse.parseWellKnownUriRecord(record);
		NFCString = uri.toString();
		
		try { 
			JSONObject jsonObject;
			jsonObject = new JSONObject(NFCString);	                         
			merchant_id = jsonObject.getString("merchant_id");
			amount = jsonObject.getString("amount");
			detail = jsonObject.getString("detail");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		payloadTV.setText(merchant_id+"\n"+"账单详情："+detail+"\n"+"金额："+amount);
	}
	
	private void enableForegroudDispatch(){
		if(mNfcAdapter!=null){
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
	}
	
	private void disableForegroudDispatch(){
		if(mNfcAdapter!=null){
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		enableForegroudDispatch();
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		disableForegroudDispatch();
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
}
