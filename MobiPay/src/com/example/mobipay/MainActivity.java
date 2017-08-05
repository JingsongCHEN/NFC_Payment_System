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
		//允许主线程阻塞UI
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		final Context context = this;
		//定义控件
		final Button login=(Button)findViewById(R.id.login);
        final Button register=(Button)findViewById(R.id.register);
        final EditText Uname=(EditText)findViewById(R.id.uname);
		final EditText Pword=(EditText)findViewById(R.id.pword);
		final Dialog alertDialog = new AlertDialog.Builder(this)
													.setTitle("错误") .setMessage("用户名或密码错误")
													.setPositiveButton("确定", null)
													.create();
		//登录按键的点击响应事件
        login.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
	        {  	//读取用户输入的用户名和密码		
				Data.usrname = Uname.getText().toString();
				Account.loginPassword = Pword.getText().toString();
				Account.username = Data.usrname;
				int flag = -2;
				//判断输入的用户名是否为空
				if(!Account.username.equals("") && Account.username.matches("^[a-z0-9A-Z]+$") && 
						!Account.loginPassword.equals("") && Account.loginPassword.matches("^[a-z0-9A-Z]+$"))
				{
    				try {
    					//调用登录界面
						flag = Account.login();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//如果登录成功
				if(flag == 0)
	      		{
					try {
						//获取当前交易模式
						Data.mode = Account.getMode();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try{
						//获取当前余额
						Data.balance = Account.getBalance().replace("\"", "");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//如果登录时用户模式为免密支付，即更改为确认支付
					if(Data.mode==1)
					{
						try {
							//调用方法
							Account.exitPasswordFreeMode();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Data.mode=0;
					}
					//打开主操作界面
					Intent intent = new Intent(context, Operate.class);
					startActivity(intent);
	      		}
				else if(flag==-1)
				{
					//显示消息提醒
					alertDialog.show();
				}
	        }
        });
        //注册按键的点击响应
        register.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
			{
        		//跳转到注册界面
			    Intent intent = new Intent(context, Reg.class);
			    startActivity(intent);
			}
        });          
	}
}
