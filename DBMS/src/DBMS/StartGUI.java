package DBMS;

import java.awt.*;
import java.awt.event.*; 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

//起始框
@SuppressWarnings("serial")
public class StartGUI extends JFrame implements ActionListener {

	private JPanel pan1,pan2;
	
	//输入框
	JTextField HOST;	
	JTextField PORT;
	JTextField DBNAME;
	JTextField USER;
	JPasswordField PASSWORD;
	
	//连接数据库标志
	int exist_db;
	DB mydb;
	
	//主界面
	MainGUI main;
	
	//连接数据
	String host;
	String port;
	String dbname;
	String user;
	String password;
	
	//勾选框
	JCheckBox A;
	
	//确认按钮
	JButton B;

	public StartGUI()
	{
		//初始化
		host="";
		port="";
		dbname="";
		user="";
		password="";       
		exist_db = 0;
		setTitle("Login");
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		//设置布局
        setLayout(new FlowLayout(FlowLayout.LEFT,20,20));  //设置窗体布局为空布局
        //检查书否有之前记录的账号
        Check();
        //定义容器
        setpan1();
        setpan2();
        pack();
        //定位
        double width = Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2; //得到当前屏幕分辨率的高
	    double height = Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;//得到当前屏幕分辨率的宽
	    setLocation((int)width,(int)height);//设置大小 
	}
	
	public void setpan1()
	{
		pan1 = new JPanel();
        pan1.setPreferredSize(new Dimension(600, 400));
        //添加图片
        JLabel lb = new JLabel(new ImageIcon("draw/cover.jpg"));
        pan1.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        pan1.add(lb);
        getContentPane().add(pan1); 
	}
	
	public void setpan2()
	{
		pan2 = new JPanel();
        pan2.setPreferredSize(new Dimension(200, 400));
        pan2.setLayout(new FlowLayout(FlowLayout.LEFT,15,12));
        //设置输入框
        JLabel Host = new JLabel("主机名:");
        HOST = new JTextField(15);
        HOST.setText(host);
        JLabel Port = new JLabel("端口:");
        PORT = new JTextField(15);
        PORT.setText(port);
        JLabel Dbname = new JLabel("数据库名:");
        DBNAME = new JTextField(15);
        DBNAME.setText(dbname);
        JLabel User = new JLabel("用户名:");
        USER = new JTextField(15);
        USER.setText(user);
        JLabel Password = new JLabel("密码:");
        PASSWORD = new JPasswordField(15);
        //掩藏密码
        PASSWORD.setText(password);
        PASSWORD.setEchoChar('*');
		pan2.add(Host);
        pan2.add(HOST);
        pan2.add(Port);
        pan2.add(PORT);
        pan2.add(Dbname);
        pan2.add(DBNAME);
        pan2.add(User);
        pan2.add(USER);
        pan2.add(Password);
        pan2.add(PASSWORD);
        //初始化勾选框
        A = new JCheckBox("记住密码", false);
        pan2.add(A);
        //定义按键并设置响应事件
        B = new JButton("Login");
        B.addActionListener(this);  
        pan2.add(B);
        getContentPane().add(pan2); 
	}
	//当Login键按下时
	public void actionPerformed(ActionEvent e) {
		//读取输入框里的信息
		host = HOST.getText().trim();	
		port = PORT.getText().trim();
		dbname = DBNAME.getText().trim();
		user = USER.getText().trim();
		password = PASSWORD.getText().trim();
		//如果需要保存账户信息
		if(A.isSelected())
		{
			String account = host + "\r\n"+port+"\r\n"+dbname+" \r\n"+user+" \r\n"+password+" \r\n";
			File txt=new File("account.txt");
			try {
			if(!txt.exists()){			
					txt.createNewFile();
			}
			FileOutputStream fos=new FileOutputStream(txt);
			fos.write(account.getBytes());
			fos.close();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}		
		}
		//如果不需要就清空之前的账号
		else
		{
			String account ="";
			File txt=new File("account.txt");
			try {
			if(!txt.exists()){			
					txt.createNewFile();
			}
			FileOutputStream fos=new FileOutputStream(txt);
			fos.write(account.getBytes());
			fos.close();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}		
		}
		//如果还未连接成功
		if(exist_db == 0)
		{
			mydb = new DB(host,port,dbname,user,password);
			exist_db = mydb.exist;
			//如果连接成功
			if(exist_db == 1)					
			{
				dispose();
				//进入主界面
				main = new MainGUI(mydb);
			}
			//如果失败就弹出提示框
			else
			{
				String error = "账号或密码错误";
				JOptionPane.showMessageDialog(null,error, "错误",JOptionPane.ERROR_MESSAGE);  
			}
		}
    } 
	//核对是否有之前保存的账户
	public void Check()
	{
		File file = new File("account.txt");
        BufferedReader reader = null;
        try {
			if(!file.exists()){			
				file.createNewFile();
			}
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //如果有
            if((tempString = reader.readLine()) != null)
            {
            	host = tempString;
        		port =  reader.readLine();
        		dbname =  reader.readLine();
        		user =  reader.readLine();
        		password =  reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
        } 
	}
	public static void main(String args[]){
		new StartGUI();	
	}
}