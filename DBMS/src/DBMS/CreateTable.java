package DBMS;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;



import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

//�½���Ĵ���
@SuppressWarnings("serial")
public class CreateTable extends JFrame implements ActionListener{
	
	//����1
	JPanel Pan1;
	JTextField Tablename;
	//����2
	JPanel Pan2;
	JTextField [] Attrname;
	JComboBox<String> [] Attr;
	JRadioButton []Primary;
	JCheckBox [] Unique;
	JTextField [] Size;
	//����3
	JPanel Pan3;
	JButton Create;
	//��ѯ���
	String query;
	
	@SuppressWarnings("unchecked")
	CreateTable()
	{
		//��ʼ��
		Size = new JTextField[5];
		Attrname = new JTextField[5];
		Primary = new JRadioButton[5];
		query = "";
		Attr = new JComboBox[5];
		Unique = new JCheckBox[5];
		setVisible(true);
		this.setAlwaysOnTop(true);
		setTitle("CreateTable");
		//���ò���
		setLayout(new FlowLayout(FlowLayout.CENTER,10,10)); 
		//��������
		setpan1();
		setpan2();
		setpan3();
		setSize(580,360);
		setResizable(false);
		//���ڶ�λ
		double width = Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2; //�õ���ǰ��Ļ�ֱ��ʵĸ�
	    double height = Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;//�õ���ǰ��Ļ�ֱ��ʵĿ�
	    setLocation((int)width,(int)height);//���ô�С 
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	//����1
	public void setpan1()
	{
		Pan1= new JPanel();
		Pan1.setPreferredSize(new Dimension(560, 35));
		Pan1.setLayout(new FlowLayout(FlowLayout.LEFT,20,10));
		JLabel L = new JLabel ("����:");
		Tablename =  new JTextField(10);
		Pan1.add(L);
		Pan1.add(Tablename);
		getContentPane().add(Pan1); 
	}
	//����2
	public void setpan2()
	{
		Pan2= new JPanel();
		Pan2.setPreferredSize(new Dimension(560, 220));
		Pan2.setLayout(new FlowLayout(FlowLayout.LEFT,20,10));
		String [] attrs = {"int","float","double","char","varchar"};		
		ButtonGroup P = new ButtonGroup();
		//����1
		JLabel L1 = new JLabel ("������:");
		Attrname[0] = new JTextField(8);
		Attr[0] = new JComboBox<String>(attrs);
		JLabel S1 = new JLabel("��С:");
		Size[0] =  new JTextField(5);
		Primary[0] = new JRadioButton("����",true);
		Unique[0] = new JCheckBox("Ψһ");
		//������
		Pan2.add(L1);
		Pan2.add(Attrname[0]);
		Pan2.add(Attr[0]);
		Pan2.add(S1);
		Pan2.add(Size[0]);
		Pan2.add(Primary[0]);
		Pan2.add(Unique[0]);	
		P.add(Primary[0]);
		//����2
		JLabel L2 = new JLabel ("������:");
		Attrname[1] = new JTextField(8);
		Attr[1] = new JComboBox<String>(attrs);
		JLabel S2 = new JLabel("��С:");
		Size[1] =  new JTextField(5);
		Primary[1] = new JRadioButton("����");
		Unique[1] = new JCheckBox("Ψһ");
		//������
		Pan2.add(L2);
		Pan2.add(Attrname[1]);
		Pan2.add(Attr[1]);
		Pan2.add(S2);
		Pan2.add(Size[1]);
		Pan2.add(Primary[1]);
		Pan2.add(Unique[1]);	
		P.add(Primary[1]);
		//����3
		JLabel L3 = new JLabel ("������:");
		Attrname[2] = new JTextField(8);
		Attr[2] = new JComboBox<String>(attrs);
		JLabel S3 = new JLabel("��С:");
		Size[2] =  new JTextField(5);
		Primary[2] = new JRadioButton("����");
		Unique[2] = new JCheckBox("Ψһ");
		//������
		Pan2.add(L3);
		Pan2.add(Attrname[2]);
		Pan2.add(Attr[2]);
		Pan2.add(S3);
		Pan2.add(Size[2]);
		Pan2.add(Primary[2]);
		Pan2.add(Unique[2]);	
		P.add(Primary[2]);
		//����4
		JLabel L4 = new JLabel ("������:");
		Attrname[3] = new JTextField(8);
		Attr[3] = new JComboBox<String>(attrs);
		JLabel S4 = new JLabel("��С:");
		Size[3] =  new JTextField(5);
		Primary[3] = new JRadioButton("����");
		Unique[3] = new JCheckBox("Ψһ");
		//������
		Pan2.add(L4);
		Pan2.add(Attrname[3]);
		Pan2.add(Attr[3]);
		Pan2.add(S4);
		Pan2.add(Size[3]);
		Pan2.add(Primary[3]);
		Pan2.add(Unique[3]);	
		P.add(Primary[3]);
		//����5
		JLabel L5 = new JLabel ("������:");
		Attrname[4] = new JTextField(8);
		Attr[4] = new JComboBox<String>(attrs);
		JLabel S5 = new JLabel("��С:");
		Size[4] =  new JTextField(5);
		Primary[4] = new JRadioButton("����");
		Unique[4] = new JCheckBox("Ψһ");
		//������
		Pan2.add(L5);
		Pan2.add(Attrname[4]);
		Pan2.add(Attr[4]);
		Pan2.add(S5);
		Pan2.add(Size[4]);
		getContentPane().add(Pan2); 
		Pan2.add(Primary[4]);
		Pan2.add(Unique[4]);	
		P.add(Primary[4]);
	}
	//����3
	public void setpan3()
	{
		Pan3= new JPanel();
		Pan3.setPreferredSize(new Dimension(560, 35));
		Pan3.setLayout(new FlowLayout(FlowLayout.RIGHT,30,5));
		Create = new JButton("������");
		//��Ӱ�����Ӧ�¼�
		Create.addActionListener(this); 
		Pan3.add(Create);
		getContentPane().add(Pan3); 
	}
	//������Ӧ
	public void actionPerformed(ActionEvent e) 
	{
		int count=0;
		//���������Ϊ��
		if(!Tablename.getText().trim().equals(""))
		{
			//��ʼ�齨��ѯ���
			query = "create table " + Tablename.getText().trim()+" ( ";
			int i;	
			int pri = -1;
			//�鿴5������
			for(i=0;i<5;i++)
			{
				//�����������Դ��
				if(!Attrname[i].getText().trim().equals("") && (!Size[i].getText().trim().equals("") || !Attr[i].getSelectedItem().toString().equals("char")))
				{
					count ++;
					//������ַ���
					if(Attr[i].getSelectedItem().toString().equals("char") || Attr[i].getSelectedItem().toString().equals("varchar"))
						query += Attrname[i].getText().trim()+" "+Attr[i].getSelectedItem().toString() +" (" 
							+Size[i].getText().trim()+") "; 
					else
						query += Attrname[i].getText().trim()+" "+Attr[i].getSelectedItem().toString()+" "; 
					//�ж��Ƿ���unique
					if(Unique[i].isSelected())
					{
						query += "unique";
					}
					//�ж��Ƿ�������
					if(Primary[i].isSelected())
					{
						pri = i;
					}
					query += ",";
				}
			}
			//���������
			if(pri != -1)
			{
				query += "primary key ("+Attrname[pri].getText().trim()+"),";
			}
			//ɾȥ���һ��','
			query = query.substring(0, query.length()-1);
			query += ");";
		}
		//���û������
		if(count == 0)
		{
			query = "";
		}	
		//�رմ���
		this.dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING) );
	}
	public static void main(String[] args) 
	{
		new CreateTable();
	}
}
