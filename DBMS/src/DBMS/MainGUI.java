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

//������
@SuppressWarnings("serial")
public class MainGUI extends JFrame implements ActionListener{

	//����1
	JPanel Pan1;
	JButton Createtable;
	JButton Deletetable;	
	JList<String> Tableset;
	JScrollPane Tablelist;
	
	//����2
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
	
	//һЩ��Ҫ�����ı���
	String Tablename;
	String condi;
	ArrayList<String> attr_type;

	//�����ϵ����ݿ�
	DB Mydb;
	
	MainGUI(DB db) {
		//��ʼ��
		attr_type = new ArrayList<String>();
		setTitle("Main");
		Mydb = db;
		Tablename = "";
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		//���ô��岼��
        setLayout(new FlowLayout(FlowLayout.LEFT,20,20));  
        //��������
        setpan1();
        setpan2();
        pack();
        //��λ
        double width = Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2; //�õ���ǰ��Ļ�ֱ��ʵĸ�
	    double height = Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;//�õ���ǰ��Ļ�ֱ��ʵĿ�
	    setLocation((int)width,(int)height);//���ô�С 
	}
	//����1
	void setpan1()
	{
		Pan1= new JPanel();
		Pan1.setPreferredSize(new Dimension(200, 600));
		//������������
		Pan1.setLayout(new FlowLayout(FlowLayout.CENTER,20,15));
		//���ð�����Ӧ�¼�
		Createtable = new JButton("������");
		Createtable.addActionListener(this); 
		Deletetable = new JButton("ɾ����");
		Deletetable.addActionListener(this);
		//�����б��ѡȡ��Ӧ�¼�
		Tableset = new JList<String>();
		Tableset.addListSelectionListener(new ListSelectionListener() {
			//����һ����ѡ��
            public void valueChanged(ListSelectionEvent e) {
               Tablename = Tableset.getSelectedValue();
               //ˢ�¸�ѡ���������
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
		//�󶨹�����
		Tablelist = new JScrollPane(Tableset);		
		Tablelist.setPreferredSize(new Dimension(150, 500));
		//ˢ��һ���б�
		Update_List();
		//���ñ���
		TitledBorder tb = BorderFactory.createTitledBorder("��Ŀ¼");
        tb.setTitleJustification(TitledBorder.LEFT);
		Pan1.setBorder(tb);
		//������
		Pan1.add(Createtable);
		Pan1.add(Deletetable);
		Pan1.add(Tablelist);
		getContentPane().add(Pan1); 
	}

	void setpan2() 
	{
		Pan2= new JPanel();
		//���ò���
		Pan2.setPreferredSize(new Dimension(600, 600));
		Pan2.setLayout(null);
		//����1
		JPanel Part1 = new JPanel();
		//���ñ���
		Part1.setBorder(BorderFactory.createRaisedBevelBorder());
		Part1.setBounds(10, 10, 100, 100);
		//���ò���
		Part1.setLayout(new FlowLayout(FlowLayout.CENTER,15,15));
		//����2
		JPanel Part2 = new JPanel();
		TitledBorder p2 = BorderFactory.createTitledBorder("��ѯ��");
        p2.setTitleJustification(TitledBorder.LEFT);
		Part2.setBorder(p2);
		Part2.setBounds(130, 10, 240, 100);
		Part2.setLayout(new FlowLayout(FlowLayout.RIGHT,15,10));
		//����3
		JPanel Part3 = new JPanel();
		TitledBorder p3 = BorderFactory.createTitledBorder("�޸���");
        p3.setTitleJustification(TitledBorder.LEFT);
		Part3.setBorder(p3);
		Part3.setBounds(370, 10, 200, 100);
		Part3.setLayout(new FlowLayout(FlowLayout.CENTER,15,10));
		//����4
		JPanel Part4 = new JPanel();
		TitledBorder p4 = BorderFactory.createTitledBorder("�����");
        p4.setTitleJustification(TitledBorder.LEFT);
		Part4.setBorder(p4);
		Part4.setBounds(10, 130, 580, 470);
		Part4.setLayout(new FlowLayout(FlowLayout.CENTER,15,15));
		//Part1
		Query = new JButton("��ѯ");
		Query.addActionListener(this);
		Query.setPreferredSize(new Dimension(70, 70));
		//Part2
		Attr1 = new JComboBox<String>();
		Attr1.setPreferredSize(new Dimension(50, 20));
		Min = new JTextField(4);
		JLabel range = new JLabel("~");
		Max = new JTextField(4);
		Select = new JButton("����");
		Select.addActionListener(this);
		//Part3
		Attr2 = new JComboBox<String>();
		Attr2.setPreferredSize(new Dimension(50, 20));
		JLabel  change= new JLabel("=>");
		New = new JTextField(4);
		Update = new JButton("����");
		Update.addActionListener(this);
		Delete = new JButton("ɾ��");
		Delete.addActionListener(this);
		//Part4
		TableContent = new JTable(); 
		TableView = new JScrollPane(TableContent);  
        TableView.setPreferredSize(new Dimension(519, 410));
        //������
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
	
	//ˢ���б�
	void Update_List()
	{
		try {
			//�����ݿ��ж�ȡ���б�ı���
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
			//�����б��е���Ϣ���ػ�
			Tableset.removeAll();
			Tableset.setListData(ListContent);
			Tableset.repaint();
		} 
		catch (SQLException e) {	
			//����������ʾ��
			String error = e.toString().substring(e.toString().indexOf(":")+1);
			JOptionPane.showMessageDialog(null,error, "����",JOptionPane.ERROR_MESSAGE);
		}
	}
	//ˢ��ѡ���
	void Update_Combo(String Tablename)
	{
		try {
			if(!Tablename.equals(""))
			{
				//�����ݿ��ж�ȡһ�ű������������
				attr_type.clear();
				ArrayList<String> attr = new ArrayList<String>();
				ResultSet rs = Mydb.Select(Tablename, "");
				ResultSetMetaData mta = rs.getMetaData();		
				int columns;
				columns = mta.getColumnCount();
				for(int i = 1 ; i<= columns ; i++)
				{
					attr.add(mta.getColumnName(i));
					//��¼ÿ�����Ե���������
					attr_type.add(mta.getColumnTypeName(i));
				}
				//���ø�ѡ��
				Attr1.removeAllItems();
				Attr2.removeAllItems();
				for(int i=0;i<attr.size();i++)
				{
					Attr1.addItem(attr.get(i));
					Attr2.addItem(attr.get(i));
				}
				//�ػ渴ѡ��
				Attr1.repaint();
				Attr2.repaint();
			}
		else
		{
			//���û�б������ÿ�
			Attr1.removeAllItems();
			Attr2.removeAllItems();
			Attr1.repaint();
			Attr2.repaint();
		}
		}
		catch (SQLException e) {
			String error = e.toString().substring(e.toString().indexOf(":")+1);
			JOptionPane.showMessageDialog(null,error, "����",JOptionPane.ERROR_MESSAGE);
		}
	}
	//ˢ�±�
	void Update_Table(String Tablename,String condi)
	{
		//���������Ϊ��
		if(!Tablename.equals("")){
			try {		
				ArrayList<String> values = new ArrayList<String>();
				//ʹ��select���õ���������
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
				//��ȡ������
				while(rs.next())
				{
					for(int i = 1 ; i<= columns ; i++)
						values.add(rs.getString(mta.getColumnName(i)));
					rows++;
				}    
				String [][] Values = new String [rows][columns];
				int k = 0;
				//��ȡ���е�ֵ
				for(int i=0;i<rows;i++)
					for(int j=0;j<columns;j++)
					{
						Values[i][j] = values.get(k);
						k++;
					}
				//���ñ�
				TableContent.setModel(new DefaultTableModel(Values, Colunames));
				//�����п�
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
		        //�ػ�
		       TableContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  
		       TableContent.repaint();
			}
			catch (SQLException e) {
				//���������
				String error = e.toString().substring(e.toString().indexOf(":")+1);
				JOptionPane.showMessageDialog(null,error, "����",JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			//���û�б������ÿ�
			TableContent.removeAll();;
			TableContent.repaint();
		}
	}
	//��������Ӧ�¼�
	public void actionPerformed(ActionEvent e) {
		//�½���
		if(e.getSource()==Createtable)
		{
			setEnabled(false);
			//�����½���Ĵ���
			CreateTable A =new CreateTable();
			A.addWindowListener(new WindowAdapter(){     
				@Override    
				public void windowClosed(WindowEvent e){
					//�����ѯ��䲻Ϊ��
					if(!A.query.equals(""))
					{
						try {
							Mydb.ExeQuery(A.query);
						} 
						catch (SQLException e1) {
							String error = e1.toString().substring(e1.toString().indexOf(":")+1);
							JOptionPane.showMessageDialog(null,error, "����",JOptionPane.ERROR_MESSAGE);
						}
					}
					//�����б�͸�ѡ��
					Update_List();
					Update_Combo("");
					setVisible(true);
					setEnabled(true);
				}       
			});
		}
		//ɾ����
		if(e.getSource()==Deletetable)
		{
			//��ȡ��ѡ�еı���
			Tablename = Tableset.getSelectedValue();
			if(!Tablename.equals(""))
			{
				//������ʾ��
				int op = JOptionPane.showConfirmDialog(null, "�Ƿ�Ҫɾ����", "��ʾ",JOptionPane.YES_NO_OPTION);
				if(op == 0)
				{
					try {
						String Q = "drop table " + Tablename;
						Mydb.ExeQuery(Q);
					} 
					catch (SQLException e1) {
						String error = e1.toString().substring(e1.toString().indexOf(":")+1);
						JOptionPane.showMessageDialog(null,error, "����",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			//�����б�͸�ѡ��
			Update_List();
			Update_Combo("");
		}
		//��ѯ���
		if(e.getSource()==Query)
		{
			setEnabled(false);
			//���������
			QueryGUI B =new QueryGUI();
			B.addWindowListener(new WindowAdapter(){     
				@Override    
				public void windowClosed(WindowEvent e){
					//�����ѯ��䲻Ϊ��
					if(!B.query.equals(""))
					{
						try {
							Mydb.ExeQuery(B.query);
						} 
						catch (SQLException e1) {
							String error = e1.toString().substring(e1.toString().indexOf(":")+1);
							JOptionPane.showMessageDialog(null,error, "����",JOptionPane.ERROR_MESSAGE);
						}
					}
					//�����б�ĸ�ѡ��
					Update_List();
					Update_Table("","");
					Update_Combo("");
					setVisible(true);
					setEnabled(true);
				}       
			});
		}
		//���ұ�
		if(e.getSource()==Select)
		{
			//�����ѡ��Ϊ��
			if(Attr1.getSelectedItem()!=null)
			{
				 condi = "";
				 Tablename = Tableset.getSelectedValue();
				 //�����������Ҳ������
				 if(!Min.getText().trim().equals("") && !Max.getText().trim().equals(""))
				 {
					 //������ַ���
					 if(attr_type.get(Attr1.getSelectedIndex()).equals("CHAR")||attr_type.get(Attr1.getSelectedIndex()).equals("VARCHAR"))
						 condi = Attr1.getSelectedItem().toString()+" >= " + "'"+ Min.getText().trim() +"'"+ " && " + Attr1.getSelectedItem().toString()+" <= " + "'"+Max.getText().trim()+"'";
					 else
						 condi = Attr1.getSelectedItem().toString()+">=" + Min.getText().trim() + " && " + Attr1.getSelectedItem().toString()+"=<" + Max.getText().trim();
				 }
				 //���ֻ������
				 else if(!Min.getText().trim().equals(""))
				 {
					 //������ַ���
					 if(attr_type.get(Attr1.getSelectedIndex()).equals("CHAR")||attr_type.get(Attr1.getSelectedIndex()).equals("VARCHAR"))
						 condi = Attr1.getSelectedItem().toString()+">=" + "'" +Min.getText().trim()+"'";
					 else
						 condi = Attr1.getSelectedItem().toString()+">=" +Min.getText().trim();
				 } 
				 //���ֻ������
				 else if(!Max.getText().trim().equals(""))
				 {
					 //������ַ���
					 if(attr_type.get(Attr1.getSelectedIndex()).equals("CHAR")||attr_type.get(Attr1.getSelectedIndex()).equals("VARCHAR"))
						 condi = Attr1.getSelectedItem().toString()+"<=" + "'" +Max.getText().trim()+"'";
					 else
						 condi = Attr1.getSelectedItem().toString()+"<=" +Max.getText().trim();
				 }
				//���±�
				Update_Table(Tablename, condi);
				New.setText("");
				Min.setText("");
				Max.setText("");
			}
		}
		//�޸ı�
		if(e.getSource()==Update)
		{
			//�����ѡ��Ϊ������ֵ��Ϊ��
			if(Attr2.getSelectedItem() !=null && !New.getText().trim().equals(""))
			{
				//������ʾ��
				int op = JOptionPane.showConfirmDialog(null, "�Ƿ�Ҫ���£�", "��ʾ",JOptionPane.YES_NO_OPTION);
				//ȷ���޸�
				if(op == 0)
				{
					//ȡ����ֵ
					String Newvalue = New.getText().trim();
					//������ַ���
					if(attr_type.get(Attr2.getSelectedIndex()).equals("CHAR")||attr_type.get(Attr2.getSelectedIndex()).equals("VARCHAR"))
					{
						Newvalue = "'"+Newvalue+"'";
					}
					//ȡ�ñ���
					Tablename =  Tableset.getSelectedValue();				
					try {
						Mydb.Update(Tablename,Attr2.getSelectedItem().toString(), Newvalue, condi);
					} 
					catch (SQLException e1) {
						//��������
						String error = e1.toString().substring(e1.toString().indexOf(":")+1);
						JOptionPane.showMessageDialog(null,error, "����",JOptionPane.ERROR_MESSAGE);  
					}
					//���±������͸�ѡ��
					Update_Table(Tablename, "");
					condi = "";
					Update_Combo("");
					New.setText("");
					Min.setText("");
					Max.setText("");
				}
			}
		}
		//ɾ��Ԫ��
		if(e.getSource()==Delete)
		{
			//ȡ�ñ���
			Tablename =  Tableset.getSelectedValue();
			//���������Ϊ��
			if(Tablename!=null && !Tablename.equals("") && Attr2.getSelectedItem() !=null)
			{
				//����ѡ���
				int op = JOptionPane.showConfirmDialog(null, "�Ƿ�Ҫɾ����", "��ʾ",JOptionPane.YES_NO_OPTION);
				//ȷ��ɾ��
				if(op == 0)
				{
					Mydb.Delete(Tablename, condi);
					//���±������͸�ѡ��
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
		//������
		DB mydb = new DB("localhost","3306","library","sun","2042868");
		new MainGUI(mydb);
	}
}
