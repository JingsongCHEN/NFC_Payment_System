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

//��ѯ��������
@SuppressWarnings("serial")
public class QueryGUI extends JFrame implements ActionListener{
	
	//��ѯ���
	String query;
	//�����
	JTextArea Query;
	
	QueryGUI()
	{
		//��ʼ��
		query = "";
		setVisible(true);
		this.setAlwaysOnTop(true);
		setTitle("Query");
		setLayout(null);
		JLabel L = new JLabel ("�������ѯ���:");
		//���ó����
		Query = new JTextArea(10,20);
		//�󶨹�����
		JScrollPane View = new JScrollPane(Query);
		JButton B = new JButton("ȷ��"); 
		//��λ
		L.setBounds(20, 20, 120, 20);
		View.setBounds(20,50,400,200);
		B.setBounds(360, 260, 60, 30);
		B.addActionListener(this); 
		//������
		getContentPane().add(L); 
		getContentPane().add(View); 
		getContentPane().add(B); 
		setSize(445,330);
		setResizable(false);
		//���ڶ�λ
		double width = Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2; //�õ���ǰ��Ļ�ֱ��ʵĸ�
	    double height = Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;//�õ���ǰ��Ļ�ֱ��ʵĿ�
	    setLocation((int)width,(int)height);//���ô�С 
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	public void actionPerformed(ActionEvent e) 
	{
		//ȡ�ò�ѯ���
		query = Query.getText();
		this.dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING) );
	}
	public static void main(String[] args) 
	{
		new QueryGUI();
	}
}
