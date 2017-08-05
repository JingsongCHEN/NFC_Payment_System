package com.example.mobipay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class Operate extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context context = this;    
		//创建标签
		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.activity_operate, tabHost.getTabContentView(),true);		
		//定义两个标签
	    tabHost.addTab(tabHost.newTabSpec("tab01").setIndicator("支付").setContent(R.id.tab01));
		tabHost.addTab(tabHost.newTabSpec("tab02").setIndicator("用户中心").setContent(R.id.tab02));		
		//tab01的控件
		final Button button1=(Button)findViewById(R.id.button1);
		final Button button2=(Button)findViewById(R.id.button2);
		final EditText Password = new EditText(this);	
		final TextView textmode=(TextView)findViewById(R.id.mode);
		textmode.setTextSize(30);
		if(Data.mode==0)
		{
			textmode.setText("模式：确认支付");
		}
		else
		{
			textmode.setText("模式：免密支付");
		}
		//输入密码的对话框
		final Dialog alertDialog = new AlertDialog.Builder(this).setTitle("请输入").setIcon(
			     android.R.drawable.ic_dialog_info).setView(
			     Password).setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			    	 @Override  
			    	 //点击确认键的事件
	                    public void onClick(DialogInterface dialog, int which) {  
	                    	 Account.payPassword = "";
	                    	 //获取用户输入的支付密码
	                    	 Account.payPassword = Password.getText().toString();
	                         Password.setText("");
	                         int flag = -2;
	                         try {
	                        	 //调用切换免密支付的方法
								 flag = Account.turnToPasswordFreeMode();
	                         } catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
	                         }
	                         //如果登录成功
	                         if(flag==0)
	                         {
	         	    			try {
	         	    				//切换模式
	        						Data.mode=1;
	        						textmode.setText("模式：免密支付");
	        					} catch (Exception e) {
	        						// TODO Auto-generated catch block
	        						e.printStackTrace();
	        					}	         					
	                         }
	                         //密码错误
	                         else if(flag < 0)
	                         {
	                        	 int chance = -flag;
	                        	 new AlertDialog.Builder(Operate.this)
									.setTitle("错误") .setMessage("密码错误，可尝试次数："+chance+"次！")
									.setPositiveButton("确定", null)
									.show();
	                         }
	                         //账户被锁定
	                         else if(flag > 0)
	                         {
	                        	 int time = flag;
	                        	 new AlertDialog.Builder(Operate.this)
									.setTitle("错误") .setMessage("账户已锁定，剩余时间："+time+"秒！")
									.setPositiveButton("确定", null)
									.show();
	                         }
	                    }  
	                })
			     .setNegativeButton("取消", null).create();
		
		//tab02的控件
		final TextView text=(TextView)findViewById(R.id.userdata);
		text.setTextSize(30);
		text.setText("用户名："+Data.usrname);				
		final TextView text1=(TextView)findViewById(R.id.userdata1);
		text1.setTextSize(30);
		text1.setText("账户余额："+Data.balance);
		final Button exit = (Button)findViewById(R.id.button3);
		final ListView BillList = (ListView)findViewById(R.id.BillList);
		//列表的点击响应
		BillList.setOnItemClickListener(new OnItemClickListener(){			 
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
            	//如果列表不为空
                if(Data.bill.size() != 0)
                {
                	//读取账单详情
	                Data.Bill = Data.bill.get(arg2-1).getDetail();
	                //进入账单详情界面
	                Intent intent = new Intent(context, BillInfo.class);
	                startActivity(intent);
                }
            }	             
	     });
		//标签的点击事件
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){    
            @Override  
            public void onTabChanged(String tabId) {  
                if (tabId.equals("tab02")) {   
                	//点击第二个标签  
                	text.setText("用户名："+Data.usrname);
                	//刷新账户余额
                	try{
						Data.balance = Account.getBalance().replace("\"", "");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                	text1.setText("账户余额："+Data.balance);
                	//刷新账单列表
                	try {
                		Account.getBills();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	String[] ListData;
                	if(Data.bill.size() > 0)
                	{
	                	ListData = new String[Data.bill.size()+1];
	                	ListData[0] = "时间"+"                 "+"商家"+"    "+"金额";
	                	for(int i=0;i<Data.bill.size();i++)
	                	{
	                		ListData[i+1] = Data.bill.get(i).getDate().substring(0,10)+"  "+Data.bill.get(i).getMerchant_id() + "     "+Data.bill.get(i).getAmount();
	                	}	                	
                	}
                	//如果没有账单
                	else 
                	{
                		ListData = new String[1];
                		ListData[0]="没有账单";
                	}
                	BillList.setAdapter(new ArrayAdapter<String>(Operate.this, android.R.layout.simple_expandable_list_item_1,ListData));
                }  
            }              
        });   
		//支付按键的响应事件	
	    button1.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick(View v)
			{
	    		//如果是确认支付就进入确认支付界面
	    		if(Data.mode==0)
	    		{
	    			Intent intent = new Intent(context, NFC_receive.class);
		      		startActivity(intent);
	    		}
	    		//如果是免密支付就进入确认免密界面
	    		else
	    		{
	    			Intent intent = new Intent(context, NFC_send.class);
		      		startActivity(intent);
	    		}
			}
	    });
	    //切换模式的响应事件
	    button2.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick(View v)
			{
	    		//如果要切换到免密支付
	    		if(Data.mode==0)
	    		{
	    			alertDialog.show();
	    		}
	    		else
	    		{
	    			//调用方法
	    			try {
						Account.exitPasswordFreeMode();
						Data.mode=0;
						textmode.setText("模式：确认支付");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
			}
	    });
	    //注销按键的响应事件
	    exit.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick(View v)
			{
	    		//进入登录界面
	    		Intent intent = new Intent(context, MainActivity.class);
	      		startActivity(intent);
			}
	    });
	}
}
