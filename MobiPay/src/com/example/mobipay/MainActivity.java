package com.example.mobipay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
		//�������߳�����UI
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		final Context context = this;
		//����ؼ�
		final Button login=(Button)findViewById(R.id.login);
        final Button register=(Button)findViewById(R.id.register);
        final EditText Uname=(EditText)findViewById(R.id.uname);
		final EditText Pword=(EditText)findViewById(R.id.pword);
		final Dialog alertDialog = new AlertDialog.Builder(this)
													.setTitle("����") .setMessage("�û������������")
													.setPositiveButton("ȷ��", null)
													.create();
		//��¼�����ĵ����Ӧ�¼�
        login.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
	        {  	//��ȡ�û�������û���������		
				Data.usrname = Uname.getText().toString();
				Account.loginPassword = Pword.getText().toString();
				Account.username = Data.usrname;
				int flag = -2;
				//�ж�������û����Ƿ�Ϊ��
				if(!Account.username.equals("") && Account.username.matches("^[a-z0-9A-Z]+$") && 
						!Account.loginPassword.equals("") && Account.loginPassword.matches("^[a-z0-9A-Z]+$"))
				{
    				try {
    					//���õ�¼����
						flag = Account.login();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//�����¼�ɹ�
				if(flag == 0)
	      		{
					try {
						//��ȡ��ǰ����ģʽ
						Data.mode = Account.getMode();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try{
						//��ȡ��ǰ���
						Data.balance = Account.getBalance().replace("\"", "");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//�����¼ʱ�û�ģʽΪ����֧����������Ϊȷ��֧��
					if(Data.mode==1)
					{
						try {
							//���÷���
							Account.exitPasswordFreeMode();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Data.mode=0;
					}
					//������������
					Intent intent = new Intent(context, Operate.class);
					startActivity(intent);
	      		}
				else if(flag==-1)
				{
					//��ʾ��Ϣ����
					alertDialog.show();
				}
	        }
        });
        //ע�ᰴ���ĵ����Ӧ
        register.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
			{
        		//��ת��ע�����
			    Intent intent = new Intent(context, Reg.class);
			    startActivity(intent);
			}
        });          
	}
}
