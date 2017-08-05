package DBMS;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.awt.event.*; 

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

//主窗口
@SuppressWarnings("serial")
public class MainGUI extends JFrame implements ActionListener{

	//容器1
	JPanel Pan1;
	JButton Createtable;
	JButton Deletetable;	
	JList<String> Tableset;
	JScrollPane Tablelist;
	
	//容器2
	JPanel Pan2;
	JButton Query;
	JComboBox<String> Attr1;
	JComboBox<String> Attr2;
	JTextField Min;
	JTextField Max;
	JButton Select;
	JTextField New;
	JButton Update;
	JButton Delete;
	JTable TableContent;
	JScrollPane TableView;
	
	//一些需要保留的变量
	String Tablename;
	String condi;
	ArrayList<String> attr_type;

	//连接上的数据库
	DB Mydb;
	
	MainGUI(DB db) {
		//初始化
		attr_type = new ArrayList<String>();
		setTitle("Main");
		Mydb = db;
		Tablename = "";
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		//设置窗体布局
        setLayout(new FlowLayout(FlowLayout.LEFT,20,20));  
        //设置容器
        setpan1();
        setpan2();
        pack();
        //定位
        double width = Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2; //得到当前屏幕分辨率的高
	    double height = Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;//得到当前屏幕分辨率的宽
	    setLocation((int)width,(int)height);//设置大小 
	}
	//容器1
	void setpan1()
	{
		Pan1= new JPanel();
		Pan1.setPreferredSize(new Dimension(200, 600));
		//设置容器布局
		Pan1.setLayout(new FlowLayout(FlowLayout.CENTER,20,15));
		//设置按键响应事件
		Createtable = new JButton("创建表");
		Createtable.addActionListener(this); 
		Deletetable = new JButton("删除表");
		Deletetable.addActionListener(this);
		//设置列表和选取响应事件
		Tableset = new JList<String>();
		Tableset.addListSelectionListener(new ListSelectionListener() {
			//当有一个被选中
            public void valueChanged(ListSelectionEvent e) {
               Tablename = Tableset.getSelectedValue();
               //刷新复选框和输出表格
               if(Tablename != null)
               {
					Update_Combo(Tablename);
					Update_Table(Tablename,"");
					New.setText("");
					Min.setText("");
					Max.setText("");
					condi = "";
               }         
        }});
		//绑定滚动条
		Tablelist = new JScrollPane(Tableset);		
		Tablelist.setPreferredSize(new Dimension(150, 500));
		//刷新一次列表
		Update_List();
		//设置边纹
		TitledBorder tb = BorderFactory.createTitledBorder("表目录");
        tb.setTitleJustification(TitledBorder.LEFT);
		Pan1.setBorder(tb);
		//添加组件
		Pan1.add(Createtable);
		Pan1.add(Deletetable);
		Pan1.add(Tablelist);
		getContentPane().add(Pan1); 
	}

	void setpan2() 
	{
		Pan2= new JPanel();
		//设置布局
		Pan2.setPreferredSize(new Dimension(600, 600));
		Pan2.setLayout(null);
		//容器1
		JPanel Part1 = new JPanel();
		//设置边纹
		Part1.setBorder(BorderFactory.createRaisedBevelBorder());
		Part1.setBounds(10, 10, 100, 100);
		//设置布局
		Part1.setLayout(new FlowLayout(FlowLayout.CENTER,15,15));
		//容器2
		JPanel Part2 = new JPanel();
		TitledBorder p2 = BorderFactory.createTitledBorder("查询区");
        p2.setTitleJustification(TitledBorder.LEFT);
		Part2.setBorder(p2);
		Part2.setBounds(130, 10, 240, 100);
		Part2.setLayout(new FlowLayout(FlowLayout.RIGHT,15,10));
		//容器3
		JPanel Part3 = new JPanel();
		TitledBorder p3 = BorderFactory.createTitledBorder("修改区");
        p3.setTitleJustification(TitledBorder.LEFT);
		Part3.setBorder(p3);
		Part3.setBounds(370, 10, 200, 100);
		Part3.setLayout(new FlowLayout(FlowLayout.CENTER,15,10));
		//容器4
		JPanel Part4 = new JPanel();
		TitledBorder p4 = BorderFactory.createTitledBorder("输出区");
        p4.setTitleJustification(TitledBorder.LEFT);
		Part4.setBorder(p4);
		Part4.setBounds(10, 130, 580, 470);
		Part4.setLayout(new FlowLayout(FlowLayout.CENTER,15,15));
		//Part1
		Query = new JButton("查询");
		Query.addActionListener(this);
		Query.setPreferredSize(new Dimension(70, 70));
		//Part2
		Attr1 = new JComboBox<String>();
		Attr1.setPreferredSize(new Dimension(50, 20));
		Min = new JTextField(4);
		JLabel range = new JLabel("~");
		Max = new JTextField(4);
		Select = new JButton("查找");
		Select.addActionListener(this);
		//Part3
		Attr2 = new JComboBox<String>();
		Attr2.setPreferredSize(new Dimension(50, 20));
		JLabel  change= new JLabel("=>");
		New = new JTextField(4);
		Update = new JButton("更新");
		Update.addActionListener(this);
		Delete = new JButton("删除");
		Delete.addActionListener(this);
		//Part4
		TableContent = new JTable(); 
		TableView = new JScrollPane(TableContent);  
        TableView.setPreferredSize(new Dimension(519, 410));
        //添加组件
        Part1.add(Query);
        Part2.add(Attr1);
        Part2.add(Min);
        Part2.add(range);
        Part2.add(Max);
        Part2.add(Select);
        Part3.add(Attr2);
        Part3.add(change);
        Part3.add(New); 
        Part3.add(Delete);
        Part3.add(Update);
        Part4.add(TableView);
		Pan2.add(Part1);
		Pan2.add(Part2);
		Pan2.add(Part3);
		Pan2.add(Part4);
		getContentPane().add(Pan2); 
	}
	
	//刷新列表
	void Update_List()
	{
		try {
			//从数据库中读取所有表的表名
			DatabaseMetaData dbmd = Mydb.conn.getMetaData();
			ResultSet rs = dbmd.getTables(null, null, null, new String[]{"TABLE"});
			String tablename;
			ArrayList<String> tmp = new ArrayList<String>();
			while(rs.next())
			{
				tablename = rs.getString("TABLE_NAME");
				tmp.add(tablename);
			}
			String[] ListContent = new String[tmp.size()];
			for(int i=0;i<tmp.size();i++)
			{
				ListContent[i] = tmp.get(i);
			}
			//重置列表中的信息并重绘
			Tableset.removeAll();
			Tableset.setListData(ListContent);
			Tableset.repaint();
		} 
		catch (SQLException e) {	
			//弹出错误提示框
			String error = e.toString().substring(e.toString().indexOf(":")+1);
			JOptionPane.showMessageDialog(null,error, "错误",JOptionPane.ERROR_MESSAGE);
		}
	}
	//刷新选择框
	void Update_Combo(String Tablename)
	{
		try {
			if(!Tablename.equals(""))
			{
				//从数据库中读取一张表的所有属性名
				attr_type.clear();
				ArrayList<String> attr = new ArrayList<String>();
				ResultSet rs = Mydb.Select(Tablename, "");
				ResultSetMetaData mta = rs.getMetaData();		
				int columns;
				columns = mta.getColumnCount();
				for(int i = 1 ; i<= columns ; i++)
				{
					attr.add(mta.getColumnName(i));
					//记录每个属性的数据类型
					attr_type.add(mta.getColumnTypeName(i));
				}
				//重置复选框
				Attr1.removeAllItems();
				Attr2.removeAllItems();
				for(int i=0;i<attr.size();i++)
				{
					Attr1.addItem(attr.get(i));
					Attr2.addItem(attr.get(i));
				}
				//重绘复选框
				Attr1.repaint();
				Attr2.repaint();
			}
		else
		{
			//如果没有表名就置空
			Attr1.removeAllItems();
			Attr2.removeAllItems();
			Attr1.repaint();
			Attr2.repaint();
		}
		}
		catch (SQLException e) {
			String error = e.toString().substring(e.toString().indexOf(":")+1);
			JOptionPane.showMessageDialog(null,error, "错误",JOptionPane.ERROR_MESSAGE);
		}
	}
	//刷新表
	void Update_Table(String Tablename,String condi)
	{
		//如果表名不为空
		if(!Tablename.equals("")){
			try {		
				ArrayList<String> values = new ArrayList<String>();
				//使用select语句得到所有数据
				ResultSet rs = Mydb.Select(Tablename, condi);
				ResultSetMetaData mta = rs.getMetaData();	
				int columns = mta.getColumnCount();
				for(int i = 1 ; i<= columns ; i++)
					values.add(mta.getColumnName(i));
				String [] Colunames = new String[values.size()];
				for(int i = 0 ; i< columns ; i++)
					Colunames[i] = values.get(i);
				values.clear();
				int rows = 0;
				//读取属性名
				while(rs.next())
				{
					for(int i = 1 ; i<= columns ; i++)
						values.add(rs.getString(mta.getColumnName(i)));
					rows++;
				}    
				String [][] Values = new String [rows][columns];
				int k = 0;
				//读取所有的值
				for(int i=0;i<rows;i++)
					for(int j=0;j<columns;j++)
					{
						Values[i][j] = values.get(k);
						k++;
					}
				//重置表
				TableContent.setModel(new DefaultTableModel(Values, Colunames));
				//设置列宽
				TableColumn column = null;  
		        if(columns<5)
		        {
		        	for(int i = 0; i < columns; i++)  
			        {  
			            column = TableContent.getColumnModel().getColumn(i);  
			            column.setPreferredWidth(516/columns); 
			        }
		        }
		        else
		        {    	
			        for(int i = 0; i < columns; i++)  
			        {  
			            column = TableContent.getColumnModel().getColumn(i);  
			            column.setPreferredWidth(100); 
			        }
		        }
		        //重绘
		       TableContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  
		       TableContent.repaint();
			}
			catch (SQLException e) {
				//弹出错误框
				String error = e.toString().substring(e.toString().indexOf(":")+1);
				JOptionPane.showMessageDialog(null,error, "错误",JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			//如果没有表名就置空
			TableContent.removeAll();;
			TableContent.repaint();
		}
	}
	//按键的响应事件
	public void actionPerformed(ActionEvent e) {
		//新建表
		if(e.getSource()==Createtable)
		{
			setEnabled(false);
			//弹出新建表的窗口
			CreateTable A =new CreateTable();
			A.addWindowListener(new WindowAdapter(){     
				@Override    
				public void windowClosed(WindowEvent e){
					//如果查询语句不为空
					if(!A.query.equals(""))
					{
						try {
							Mydb.ExeQuery(A.query);
						} 
						catch (SQLException e1) {
							String error = e1.toString().substring(e1.toString().indexOf(":")+1);
							JOptionPane.showMessageDialog(null,error, "错误",JOptionPane.ERROR_MESSAGE);
						}
					}
					//更新列表和复选框
					Update_List();
					Update_Combo("");
					setVisible(true);
					setEnabled(true);
				}       
			});
		}
		//删除表
		if(e.getSource()==Deletetable)
		{
			//拿取被选中的表名
			Tablename = Tableset.getSelectedValue();
			if(!Tablename.equals(""))
			{
				//弹出提示框
				int op = JOptionPane.showConfirmDialog(null, "是否要删除表？", "提示",JOptionPane.YES_NO_OPTION);
				if(op == 0)
				{
					try {
						String Q = "drop table " + Tablename;
						Mydb.ExeQuery(Q);
					} 
					catch (SQLException e1) {
						String error = e1.toString().substring(e1.toString().indexOf(":")+1);
						JOptionPane.showMessageDialog(null,error, "错误",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			//更新列表和复选框
			Update_List();
			Update_Combo("");
		}
		//查询语句
		if(e.getSource()==Query)
		{
			setEnabled(false);
			//弹出输入框
			QueryGUI B =new QueryGUI();
			B.addWindowListener(new WindowAdapter(){     
				@Override    
				public void windowClosed(WindowEvent e){
					//如果查询语句不为空
					if(!B.query.equals(""))
					{
						try {
							Mydb.ExeQuery(B.query);
						} 
						catch (SQLException e1) {
							String error = e1.toString().substring(e1.toString().indexOf(":")+1);
							JOptionPane.showMessageDialog(null,error, "错误",JOptionPane.ERROR_MESSAGE);
						}
					}
					//更新列表的复选框
					Update_List();
					Update_Table("","");
					Update_Combo("");
					setVisible(true);
					setEnabled(true);
				}       
			});
		}
		//查找表
		if(e.getSource()==Select)
		{
			//如果复选框不为空
			if(Attr1.getSelectedItem()!=null)
			{
				 condi = "";
				 Tablename = Tableset.getSelectedValue();
				 //如果既有上限也有下限
				 if(!Min.getText().trim().equals("") && !Max.getText().trim().equals(""))
				 {
					 //如果是字符型
					 if(attr_type.get(Attr1.getSelectedIndex()).equals("CHAR")||attr_type.get(Attr1.getSelectedIndex()).equals("VARCHAR"))
						 condi = Attr1.getSelectedItem().toString()+" >= " + "'"+ Min.getText().trim() +"'"+ " && " + Attr1.getSelectedItem().toString()+" <= " + "'"+Max.getText().trim()+"'";
					 else
						 condi = Attr1.getSelectedItem().toString()+">=" + Min.getText().trim() + " && " + Attr1.getSelectedItem().toString()+"=<" + Max.getText().trim();
				 }
				 //如果只有下限
				 else if(!Min.getText().trim().equals(""))
				 {
					 //如果是字符型
					 if(attr_type.get(Attr1.getSelectedIndex()).equals("CHAR")||attr_type.get(Attr1.getSelectedIndex()).equals("VARCHAR"))
						 condi = Attr1.getSelectedItem().toString()+">=" + "'" +Min.getText().trim()+"'";
					 else
						 condi = Attr1.getSelectedItem().toString()+">=" +Min.getText().trim();
				 } 
				 //如果只有上限
				 else if(!Max.getText().trim().equals(""))
				 {
					 //如果是字符型
					 if(attr_type.get(Attr1.getSelectedIndex()).equals("CHAR")||attr_type.get(Attr1.getSelectedIndex()).equals("VARCHAR"))
						 condi = Attr1.getSelectedItem().toString()+"<=" + "'" +Max.getText().trim()+"'";
					 else
						 condi = Attr1.getSelectedItem().toString()+"<=" +Max.getText().trim();
				 }
				//更新表
				Update_Table(Tablename, condi);
				New.setText("");
				Min.setText("");
				Max.setText("");
			}
		}
		//修改表
		if(e.getSource()==Update)
		{
			//如果复选框不为空且新值不为空
			if(Attr2.getSelectedItem() !=null && !New.getText().trim().equals(""))
			{
				//弹出提示框
				int op = JOptionPane.showConfirmDialog(null, "是否要更新？", "提示",JOptionPane.YES_NO_OPTION);
				//确定修改
				if(op == 0)
				{
					//取得新值
					String Newvalue = New.getText().trim();
					//如果是字符型
					if(attr_type.get(Attr2.getSelectedIndex()).equals("CHAR")||attr_type.get(Attr2.getSelectedIndex()).equals("VARCHAR"))
					{
						Newvalue = "'"+Newvalue+"'";
					}
					//取得表名
					Tablename =  Tableset.getSelectedValue();				
					try {
						Mydb.Update(Tablename,Attr2.getSelectedItem().toString(), Newvalue, condi);
					} 
					catch (SQLException e1) {
						//弹出错误
						String error = e1.toString().substring(e1.toString().indexOf(":")+1);
						JOptionPane.showMessageDialog(null,error, "错误",JOptionPane.ERROR_MESSAGE);  
					}
					//更新表、输入框和复选框
					Update_Table(Tablename, "");
					condi = "";
					Update_Combo("");
					New.setText("");
					Min.setText("");
					Max.setText("");
				}
			}
		}
		//删除元组
		if(e.getSource()==Delete)
		{
			//取得表名
			Tablename =  Tableset.getSelectedValue();
			//如果表名不为空
			if(Tablename!=null && !Tablename.equals("") && Attr2.getSelectedItem() !=null)
			{
				//弹出选择框
				int op = JOptionPane.showConfirmDialog(null, "是否要删除？", "提示",JOptionPane.YES_NO_OPTION);
				//确认删除
				if(op == 0)
				{
					Mydb.Delete(Tablename, condi);
					//更新表、输入框和复选框
					Update_Table(Tablename, condi);
					Update_Combo("");
					condi ="";
					New.setText("");
					Min.setText("");
					Max.setText("");
				}
			}
		}
	}
	public static void main(String[] args) throws SQLException
	{
		//测试用
		DB mydb = new DB("localhost","3306","library","sun","2042868");
		new MainGUI(mydb);
	}
}
