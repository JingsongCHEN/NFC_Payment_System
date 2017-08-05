package DBMS;

import java.sql.*;

//数据库类
public class DB{
	
	Statement statement;
	Connection conn;
	int exist;
	
	DB(String host,String port, String db,String user,String password)
	{
		//连接数据库
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			String URL = "jdbc:mysql://"+host+":"+port+"/"+db; 
			conn = DriverManager.getConnection(URL,user,password);
			statement = conn.createStatement();
			exist = 1;
		}
		catch(ClassNotFoundException e1)  
		{
			exist = 0;
		}  
		catch(SQLException e2)  
		{  
			exist = 0;
		}  
	}
	//查询元组
	ResultSet Select(String Tablename,String condi)
	{
		try
		{
			ResultSet rs;
			if(condi.equals(""))
				rs = statement.executeQuery("SELECT * FROM "+Tablename);
			else
				rs = statement.executeQuery("SELECT * FROM "+Tablename+" WHERE "+condi);
			return rs;
		}
		catch(SQLException e)  
		{  
			return null;
		}  
	}
	//删除元组
	void Delete(String Tablename, String condi) 
	{
		try {
			if(condi.equals(""))
			{
				statement.execute("DELETE FROM "+Tablename);
			}
			else
				statement.execute("DELETE FROM "+Tablename+" WHERE "+condi);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//更新元组
	void Update(String Tablename, String attr, String value, String condi) throws SQLException
	{
			if(condi.equals(""))
			{
				statement.execute("Update "+Tablename+ " SET "+attr+"="+value);
			}
			else
				statement.execute("Update "+Tablename+" SET "+attr+"="+value+" WHERE "+condi);
	}
	//执行查询语句
	void ExeQuery(String Query) throws SQLException
	{		
			if(!Query.equals(""))
			{
				statement.execute(Query);
			}
	}
	public static void main(String[] args)
	{
	}
}
