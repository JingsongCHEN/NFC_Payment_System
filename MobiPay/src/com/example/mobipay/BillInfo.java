package com.example.mobipay;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class BillInfo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_info);
		final TextView BillInfo=(TextView)findViewById(R.id.BillInfo);
		BillInfo.setText("’Àµ•œÍ«È£∫"+"\n"+Data.Bill);
	}
}
