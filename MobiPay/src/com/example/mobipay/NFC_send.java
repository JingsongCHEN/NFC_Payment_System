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
	private NfcAdapter mNfcAdapter;//nfc������
	private Context mcontext;
	private String sendContent="";//��������
	private TextView textView;
	private Handler handler=null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_send);
		textView = (TextView) findViewById(R.id.textview);
		mcontext = this;
		handler=new Handler();  
		checkNfcFunction();//���nfc����Ӳ��״̬
		mNfcAdapter.setNdefPushMessageCallback(this,this);//nfc��Ϣ���ͻص�����
	}

	private void checkNfcFunction() {
		// TODO Auto-generated method stub
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if(mNfcAdapter == null){
			//��֧��nfc���ܣ�ǿ��ֹͣӦ��
			Toast.makeText(mcontext, "����豸��֧��nfc",Toast.LENGTH_LONG);
			finish();
		}else{
			if(!mNfcAdapter.isEnabled()){
				//�豸δ������ת������nfc������ҳ��
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				Toast.makeText(mcontext, "���nfcδ����",Toast.LENGTH_LONG);
				startActivity(intent);

				return ;
			}else if(!mNfcAdapter.isNdefPushEnabled()){
				//nfc������δ�������ǿ��ֹͣӦ��
				Toast.makeText(mcontext, "������������޷�ʵ��",Toast.LENGTH_LONG);
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
		//ͨ���ַ����������ɿɷ��͵�ndefMessage
		try {
			sendContent = Account.encapsulateMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte id = 0x00;
		//��������ndefMessage�ĺ���
		NdefMessage msg = MyNDEFMsgGet.getNdefMsg_RTD_URI(sendContent, id);
		handler.post(runnableUi);
					
		return msg;
	}
	
	Runnable   runnableUi=new  Runnable(){  
        @Override  
        public void run() {  
            //���½���  
            textView.setText(sendContent);  
        }  
          
    }; 
}
