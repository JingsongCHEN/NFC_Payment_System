package DBMS;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//查询语句输入框
@SuppressWarnings("serial")
public class QueryGUI extends JFrame implements ActionListener{
	
	//查询语句
	String query;
	//输入框
	JTextArea Query;
	
	QueryGUI()
	{
		//初始化
		query = "";
		setVisible(true);
		this.setAlwaysOnTop(true);
		setTitle("Query");
		setLayout(null);
		JLabel L = new JLabel ("请输入查询语句:");
		//设置出入框
		Query = new JTextArea(10,20);
		//绑定滚动条
		JScrollPane View = new JScrollPane(Query);
		JButton B = new JButton("确认"); 
		//定位
		L.setBounds(20, 20, 120, 20);
		View.setBounds(20,50,400,200);
		B.setBounds(360, 260, 60, 30);
		B.addActionListener(this); 
		//添加组件
		getContentPane().add(L); 
		getContentPane().add(View); 
		getContentPane().add(B); 
		setSize(445,330);
		setResizable(false);
		//窗口定位
		double width = Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2; //得到当前屏幕分辨率的高
	    double height = Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;//得到当前屏幕分辨率的宽
	    setLocation((int)width,(int)height);//设置大小 
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	public void actionPerformed(ActionEvent e) 
	{
		//取得查询语句
		query = Query.getText();
		this.dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING) );
	}
	public static void main(String[] args) 
	{
		new QueryGUI();
	}
}
