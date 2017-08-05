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
	private NfcAdapter mNfcAdapter;//nfc������
	private TextView payloadTV;
	private PendingIntent  mPendingIntent;
	private Context mcontext;
	String NFCString = "";//���ܵõ����ַ�������
	String merchant_id ="";
	String detail = "";
	String amount = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_receive);
		mcontext = this;
		payloadTV = (TextView) findViewById(R.id.payload);
		checkNfcFunction();//���nfc����
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
		resolveIntent(getIntent());//����õ���nfcIntent
		
		final EditText Password = new EditText(this);
		final Dialog alertDialog = new AlertDialog.Builder(this).setTitle("������").setIcon(
			     android.R.drawable.ic_dialog_info).setView(
			     Password).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
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
	             						.setTitle("��ʾ") .setMessage("���׳ɹ�")
	             						.setPositiveButton("ȷ��", null)
	             						.show();
	                         }
	                         else if(flag == -1)
	                         {
	                        	 new AlertDialog.Builder(NFC_receive.this)
									.setTitle("����") .setMessage("�ֻ�ʱ�����������ƥ��")
									.setPositiveButton("ȷ��", null)
									.show();
	                         }
	                         else if(flag == -2)
	                         {
	                        	 new AlertDialog.Builder(NFC_receive.this)
									.setTitle("����") .setMessage("����")
									.setPositiveButton("ȷ��", null)
									.show();
	                         }
	                         else if(flag <= -3)
	                         {
	                        	 int chance = -flag-2;
	                        	 new AlertDialog.Builder(NFC_receive.this)
									.setTitle("����") .setMessage("�������ʣ�ೢ�Դ�����"+chance+"�Σ�")
									.setPositiveButton("ȷ��", null)
									.show();
	                         }
	                         else if(flag > 0)
	                         {
	                        	 int time = flag;
	                        	 new AlertDialog.Builder(NFC_receive.this)
									.setTitle("����") .setMessage("�˻���������ʣ��ʱ�䣺"+time+"�룡")
									.setPositiveButton("ȷ��", null)
									.show();
	                         }
	                    }  
	                })
			     .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
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
			//��֧��nfc����
			Toast.makeText(mcontext, "����豸��֧��nfc",Toast.LENGTH_LONG);
			finish();
		}else{
			if(!mNfcAdapter.isEnabled()){
				//�豸δ����
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				Toast.makeText(mcontext, "���nfcδ����",Toast.LENGTH_LONG);
				startActivity(intent);
				
				return ;
			}else if(!mNfcAdapter.isNdefPushEnabled()){
				Toast.makeText(mcontext, "������������޷�ʵ��",Toast.LENGTH_LONG);
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
		resolveIntent(intent);//������յ���intent
	}
	
	private void resolveIntent(Intent intent) {//����intent����
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){//�˶Եõ���intent����������Ҫ�������Ƿ���ͬ
			NdefMessage[] messages = null;
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);//��ñ�ǩ
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if(rawMsgs != null){
				messages = new NdefMessage[rawMsgs.length];
				for(int i=0;i<rawMsgs.length;i++){
					messages[i] = (NdefMessage)rawMsgs[i];//�ӱ�ǩ�л����Ϣ
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
		//����ndef��Ϣ
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
		//������¼�е���Ϣ
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
		payloadTV.setText(merchant_id+"\n"+"�˵����飺"+detail+"\n"+"��"+amount);
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
