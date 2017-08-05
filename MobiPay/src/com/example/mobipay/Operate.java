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
		//������ǩ
		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.activity_operate, tabHost.getTabContentView(),true);		
		//����������ǩ
	    tabHost.addTab(tabHost.newTabSpec("tab01").setIndicator("֧��").setContent(R.id.tab01));
		tabHost.addTab(tabHost.newTabSpec("tab02").setIndicator("�û�����").setContent(R.id.tab02));		
		//tab01�Ŀؼ�
		final Button button1=(Button)findViewById(R.id.button1);
		final Button button2=(Button)findViewById(R.id.button2);
		final EditText Password = new EditText(this);	
		final TextView textmode=(TextView)findViewById(R.id.mode);
		textmode.setTextSize(30);
		if(Data.mode==0)
		{
			textmode.setText("ģʽ��ȷ��֧��");
		}
		else
		{
			textmode.setText("ģʽ������֧��");
		}
		//��������ĶԻ���
		final Dialog alertDialog = new AlertDialog.Builder(this).setTitle("������").setIcon(
			     android.R.drawable.ic_dialog_info).setView(
			     Password).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
			    	 @Override  
			    	 //���ȷ�ϼ����¼�
	                    public void onClick(DialogInterface dialog, int which) {  
	                    	 Account.payPassword = "";
	                    	 //��ȡ�û������֧������
	                    	 Account.payPassword = Password.getText().toString();
	                         Password.setText("");
	                         int flag = -2;
	                         try {
	                        	 //�����л�����֧���ķ���
								 flag = Account.turnToPasswordFreeMode();
	                         } catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
	                         }
	                         //�����¼�ɹ�
	                         if(flag==0)
	                         {
	         	    			try {
	         	    				//�л�ģʽ
	        						Data.mode=1;
	        						textmode.setText("ģʽ������֧��");
	        					} catch (Exception e) {
	        						// TODO Auto-generated catch block
	        						e.printStackTrace();
	        					}	         					
	                         }
	                         //�������
	                         else if(flag < 0)
	                         {
	                        	 int chance = -flag;
	                        	 new AlertDialog.Builder(Operate.this)
									.setTitle("����") .setMessage("������󣬿ɳ��Դ�����"+chance+"�Σ�")
									.setPositiveButton("ȷ��", null)
									.show();
	                         }
	                         //�˻�������
	                         else if(flag > 0)
	                         {
	                        	 int time = flag;
	                        	 new AlertDialog.Builder(Operate.this)
									.setTitle("����") .setMessage("�˻���������ʣ��ʱ�䣺"+time+"�룡")
									.setPositiveButton("ȷ��", null)
									.show();
	                         }
	                    }  
	                })
			     .setNegativeButton("ȡ��", null).create();
		
		//tab02�Ŀؼ�
		final TextView text=(TextView)findViewById(R.id.userdata);
		text.setTextSize(30);
		text.setText("�û�����"+Data.usrname);				
		final TextView text1=(TextView)findViewById(R.id.userdata1);
		text1.setTextSize(30);
		text1.setText("�˻���"+Data.balance);
		final Button exit = (Button)findViewById(R.id.button3);
		final ListView BillList = (ListView)findViewById(R.id.BillList);
		//�б�ĵ����Ӧ
		BillList.setOnItemClickListener(new OnItemClickListener(){			 
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
            	//����б�Ϊ��
                if(Data.bill.size() != 0)
                {
                	//��ȡ�˵�����
	                Data.Bill = Data.bill.get(arg2-1).getDetail();
	                //�����˵��������
	                Intent intent = new Intent(context, BillInfo.class);
	                startActivity(intent);
                }
            }	             
	     });
		//��ǩ�ĵ���¼�
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){    
            @Override  
            public void onTabChanged(String tabId) {  
                if (tabId.equals("tab02")) {   
                	//����ڶ�����ǩ  
                	text.setText("�û�����"+Data.usrname);
                	//ˢ���˻����
                	try{
						Data.balance = Account.getBalance().replace("\"", "");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                	text1.setText("�˻���"+Data.balance);
                	//ˢ���˵��б�
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
	                	ListData[0] = "ʱ��"+"                 "+"�̼�"+"    "+"���";
	                	for(int i=0;i<Data.bill.size();i++)
	                	{
	                		ListData[i+1] = Data.bill.get(i).getDate().substring(0,10)+"  "+Data.bill.get(i).getMerchant_id() + "     "+Data.bill.get(i).getAmount();
	                	}	                	
                	}
                	//���û���˵�
                	else 
                	{
                		ListData = new String[1];
                		ListData[0]="û���˵�";
                	}
                	BillList.setAdapter(new ArrayAdapter<String>(Operate.this, android.R.layout.simple_expandable_list_item_1,ListData));
                }  
            }              
        });   
		//֧����������Ӧ�¼�	
	    button1.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick(View v)
			{
	    		//�����ȷ��֧���ͽ���ȷ��֧������
	    		if(Data.mode==0)
	    		{
	    			Intent intent = new Intent(context, NFC_receive.class);
		      		startActivity(intent);
	    		}
	    		//���������֧���ͽ���ȷ�����ܽ���
	    		else
	    		{
	    			Intent intent = new Intent(context, NFC_send.class);
		      		startActivity(intent);
	    		}
			}
	    });
	    //�л�ģʽ����Ӧ�¼�
	    button2.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick(View v)
			{
	    		//���Ҫ�л�������֧��
	    		if(Data.mode==0)
	    		{
	    			alertDialog.show();
	    		}
	    		else
	    		{
	    			//���÷���
	    			try {
						Account.exitPasswordFreeMode();
						Data.mode=0;
						textmode.setText("ģʽ��ȷ��֧��");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
			}
	    });
	    //ע����������Ӧ�¼�
	    exit.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick(View v)
			{
	    		//�����¼����
	    		Intent intent = new Intent(context, MainActivity.class);
	      		startActivity(intent);
			}
	    });
	}
}
