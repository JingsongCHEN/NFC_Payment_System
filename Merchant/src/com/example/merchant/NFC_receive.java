package com.example.merchant;

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
	private NfcAdapter mNfcAdapter;
	private TextView titleTV,payloadTV;
	private PendingIntent  mPendingIntent;
	private Context mcontext;
	String NFCString = "";
	String username ="";
	String cyphertextPayPassword = "";
	String cyphertextDate = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("onCreate", "1");
		setContentView(R.layout.activity_nfc_receive);
		Log.i("onCreate", "2");
		mcontext = this;
		Log.i("onCreate", "3");
		titleTV=(TextView) findViewById(R.id.title);
		Log.i("onCreate", "4");
		payloadTV = (TextView) findViewById(R.id.payload);
		Log.i("onCreate", "5");
		checkNfcFunction();
		Log.i("onCreate", "6");
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
		Log.i("onCreate", "7");
		resolveIntent(getIntent());
		Log.i("onCreate", "8");
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
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("onNewIntent", "1");
		super.onNewIntent(intent);
		Log.i("onNewIntent", "2");
		Log.i("onNewIntent", "3");
		/*if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
			resolveIntent(intent);
		}*/
		resolveIntent(intent);
		Log.i("onNewIntent", "4");
	}
	
	private void resolveIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("resolveIntent", "1");
		String action = intent.getAction();
		Log.i("resolveIntent", "2");
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
			Log.i("resolveIntent", "3");
			NdefMessage[] messages = null;
			Log.i("resolveIntent", "4");
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Log.i("resolveIntent", "5");
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			Log.i("resolveIntent", "6");
			if(rawMsgs != null){
				Log.i("resolveIntent", "7");
				messages = new NdefMessage[rawMsgs.length];
				Log.i("resolveIntent", "8");
				for(int i=0;i<rawMsgs.length;i++){
					Log.i("resolveIntent", "9");
					messages[i] = (NdefMessage)rawMsgs[i];
				}
			}else{
				Log.i("resolveIntent", "10");
				byte[] empty = new byte[]{};
				Log.i("resolveIntent", "11");
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,empty,empty,empty);
				Log.i("resolveIntent", "12");
				NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
				Log.i("resolveIntent", "13");
				messages = new NdefMessage[]{msg};
			}
			Log.i("resolveIntent", "14");
			titleTV.setText("SCAN a Tag");
			Log.i("resolveIntent", "14");
			processNDEFMsg(messages);
		}else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){			
			Log.i("resolveIntent", "16");
		}else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
			Log.i("resolveIntent", "17");
		}else{
			Log.i("resolveIntent", "18");
		}
	}

	private void processNDEFMsg(NdefMessage[] messages) {
		// TODO Auto-generated method stub
		Log.i("processNDEFMsg", "1");
		Log.i("processNDEFMsg", "2");
		if(messages == null || messages.length == 0){
			Log.i("processNDEFMsg", "3");
			return ;
		}
		Log.i("processNDEFMsg", "4");
		for(int i=0;i<messages.length;i++){
			Log.i("processNDEFMsg", "5");
			int length = messages[i].getRecords().length;
			Log.i("processNDEFMsg", "6");
			NdefRecord[] records = messages[i].getRecords();
			Log.i("processNDEFMsg", "7");
			for(int j=0;j<length;j++){
				Log.i("processNDEFMsg", "8");
				for(NdefRecord record:records){
					Log.i("processNDEFMsg", "9");
					parseRTDUriRecord(record);
					Log.i("processNDEFMsg", "10");
					Log.i("processNDEFMsg", "11");
				}
			}
		}
		Log.i("processNDEFMsg", "12");
	}
	
	private void parseRTDUriRecord(NdefRecord record) {
		// TODO Auto-generated method stub
		Log.i("parseRTDUriRecord", "1");
		Uri uri = MyNFCRecordParse.parseWellKnownUriRecord(record);
		try { 
			NFCString = uri.toString();
			JSONObject jsonObject;
			jsonObject = new JSONObject(NFCString);	                         
			username = jsonObject.getString("username");
			cyphertextPayPassword = jsonObject.getString("cyphertextPayPassword");
			cyphertextDate = jsonObject.getString("cyphertextDate");			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		try {
			int responseCode = Merchant.payWithoutPassword(username, cyphertextPayPassword, 
					cyphertextDate, Data.merchantID, Data.amount, Data.detail);
			Log.i("responseCode = ", String.valueOf(responseCode));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("parseRTDUriRecord", "2");
		Log.i("parseRTDUriRecord", "3");
		
	}
	
	private void enableForegroudDispatch(){
		Log.i("enableForegroudDispatch", "1");
		if(mNfcAdapter!=null){
			Log.i("enableForegroudDispatch", "2");
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
		Log.i("enableForegroudDispatch", "3");
	}
	
	private void disableForegroudDispatch(){
		Log.i("disableForegroudDispatch", "1");
		if(mNfcAdapter!=null){
			Log.i("disableForegroudDispatch", "2");
			mNfcAdapter.disableForegroundDispatch(this);
		}
		Log.i("disableForegroudDispatch", "3");
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("onResume", "1");
		enableForegroudDispatch();
		Log.i("onResume", "2");
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.i("onPause", "1");
		super.onPause();
		Log.i("onPause", "2");
		disableForegroudDispatch();
		Log.i("onPause", "3");
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
