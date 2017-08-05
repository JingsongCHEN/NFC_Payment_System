package com.example.merchant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);  
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		final Context context = this;
		final Button Send=(Button)findViewById(R.id.send);
		final Button Receive=(Button)findViewById(R.id.receive);
	    final EditText Merchant_ID=(EditText)findViewById(R.id.merchantID);
		final EditText Detail=(EditText)findViewById(R.id.Detail);
		final EditText Amount=(EditText)findViewById(R.id.amount);
		
        Send.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
		        {
    				Data.merchantID = Merchant_ID.getText().toString();
    				Data.detail = Detail.getText().toString();
    				Data.amount = Amount.getText().toString();
					Intent intent = new Intent(context, NFC_send.class);
					startActivity(intent);
		        }
        });
        Receive.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
		        {
					Data.merchantID = Merchant_ID.getText().toString();
					Data.detail = Detail.getText().toString();
					Data.amount = Amount.getText().toString();
					Intent intent = new Intent(context, NFC_receive.class);
					startActivity(intent);
		        }
        });
	}

}
