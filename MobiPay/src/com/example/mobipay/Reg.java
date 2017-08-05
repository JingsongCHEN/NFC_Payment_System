package com.example.mobipay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Reg extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);  
		final Context context = this;
		//����ؼ�
		final Button Register=(Button)findViewById(R.id.register);
	    final EditText Uname=(EditText)findViewById(R.id.uname);
		final EditText Ppword=(EditText)findViewById(R.id.Ppword);
		final EditText Lpword=(EditText)findViewById(R.id.Lpword);
		//ע�ᰴ���ĵ����Ӧ
        Register.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
	        {
    			//��ȡ�û�������û�������¼�����֧������
				Account.username = Uname.getText().toString();
				Account.loginPassword = Lpword.getText().toString();
				Account.payPassword = Ppword.getText().toString();
				int flag = -1;
				//�ж��û����Ƿ�Ϊ��
				if(!Account.username.equals("") && Account.username.matches("^[a-z0-9A-Z]+$") && 
						!Account.loginPassword.equals("") && Account.loginPassword.matches("^[a-z0-9A-Z]+$") && 
						!Account.payPassword.equals("") && Account.payPassword.matches("^[a-z0-9A-Z]+$"))
				{
					try {
						//����ע�᷽��
						flag = Account.register();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//���ע��ɹ�
				if(flag == 0)
				{
					//��������������
					Data.usrname = Account.username;
					Data.mode = 0;
					Intent intent = new Intent(context, Operate.class);
					startActivity(intent);
				}
	        }
        });
	}
}
