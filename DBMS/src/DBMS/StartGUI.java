package DBMS;

import java.awt.*;
import java.awt.event.*; 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

//��ʼ��
@SuppressWarnings("serial")
public class StartGUI extends JFrame implements ActionListener {

	private JPanel pan1,pan2;
	
	//�����
	JTextField HOST;	
	JTextField PORT;
	JTextField DBNAME;
	JTextField USER;
	JPasswordField PASSWORD;
	
	//�������ݿ��־
	int exist_db;
	DB mydb;
	
	//������
	MainGUI main;
	
	//��������
	String host;
	String port;
	String dbname;
	String user;
	String password;
	
	//��ѡ��
	JCheckBox A;
	
	//ȷ�ϰ�ť
	JButton B;

	public StartGUI()
	{
		//��ʼ��
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
		//���ò���
        setLayout(new FlowLayout(FlowLayout.LEFT,20,20));  //���ô��岼��Ϊ�ղ���
        //��������֮ǰ��¼���˺�
        Check();
        //��������
        setpan1();
        setpan2();
        pack();
        //��λ
        double width = Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2; //�õ���ǰ��Ļ�ֱ��ʵĸ�
	    double height = Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;//�õ���ǰ��Ļ�ֱ��ʵĿ�
	    setLocation((int)width,(int)height);//���ô�С 
	}
	
	public void setpan1()
	{
		pan1 = new JPanel();
        pan1.setPreferredSize(new Dimension(600, 400));
        //���ͼƬ
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
        //���������
        JLabel Host = new JLabel("������:");
        HOST = new JTextField(15);
        HOST.setText(host);
        JLabel Port = new JLabel("�˿�:");
        PORT = new JTextField(15);
        PORT.setText(port);
        JLabel Dbname = new JLabel("���ݿ���:");
        DBNAME = new JTextField(15);
        DBNAME.setText(dbname);
        JLabel User = new JLabel("�û���:");
        USER = new JTextField(15);
        USER.setText(user);
        JLabel Password = new JLabel("����:");
        PASSWORD = new JPasswordField(15);
        //�ڲ�����
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
        //��ʼ����ѡ��
        A = new JCheckBox("��ס����", false);
        pan2.add(A);
        //���尴����������Ӧ�¼�
        B = new JButton("Login");
        B.addActionListener(this);  
        pan2.add(B);
        getContentPane().add(pan2); 
	}
	//��Login������ʱ
	public void actionPerformed(ActionEvent e) {
		//��ȡ����������Ϣ
		host = HOST.getText().trim();	
		port = PORT.getText().trim();
		dbname = DBNAME.getText().trim();
		user = USER.getText().trim();
		password = PASSWORD.getText().trim();
		//�����Ҫ�����˻���Ϣ
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
		//�������Ҫ�����֮ǰ���˺�
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
		//�����δ���ӳɹ�
		if(exist_db == 0)
		{
			mydb = new DB(host,port,dbname,user,password);
			exist_db = mydb.exist;
			//������ӳɹ�
			if(exist_db == 1)					
			{
				dispose();
				//����������
				main = new MainGUI(mydb);
			}
			//���ʧ�ܾ͵�����ʾ��
			else
			{
				String error = "�˺Ż��������";
				JOptionPane.showMessageDialog(null,error, "����",JOptionPane.ERROR_MESSAGE);  
			}
		}
    } 
	//�˶��Ƿ���֮ǰ������˻�
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
            //�����
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