using System;
using System.Collections;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace _2_PaymentService
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "Service1" in code, svc and config file together.
    // NOTE: In order to launch WCF Test Client for testing this service, please select Service1.svc or Service1.svc.cs at the Solution Explorer and start debugging.
    public class Service1 : IService1
    {
        //String SQLstring = "Server=.;Database=PaymentAPP;uid=sa;pwd=443";
        string SQLstring = "data source=.;initial catalog=PaymentAPP;user id=sa;pwd=443";
        //测试用
        public string getBill(GetBillPara para)
        {
            return "Your id is " + para.id + ", name is "+ para.username+"\n";
        }

        public Rs_Pki register1(string username)
        {
            Rs_Pki result = new Rs_Pki();

            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();
            if (MySQL.Exist_User_id(lo_conn, username))
            {
                lo_conn.Close();//关闭连接
                result.rs = -1;
                return result;//用户名已存在，返回错误码
            }
            else
            {
                string[] keys = MyRAS.RSAKey();//0:私钥;1:公钥
                result.pki = keys[1];

                //测试用，将公钥输出到文件里，供另一个程序调用。
                string filePath = "D:\\nfc项目\\testData\\" + username + "'s PKI.txt";//   ../abc'sPKI
                System.IO.File.WriteAllText(@filePath, keys[1]);
                //

                MySQL.Insert_User_1(lo_conn, username, keys[0]);
                lo_conn.Close();//关闭连接 
                result.rs = 0;
                return result;
            }            
        }

        public int register2(User_Cyphertext user_cyphertext)
        {
            string username = user_cyphertext.username;
            string Epassword = user_cyphertext.cyphertext;
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();
            //获得公钥
            SqlDataReader sqlDataReader = MySQL.Info_of_a_user(lo_conn, username);
            string pki = sqlDataReader["Public_key"].ToString();
            string Dpassword = MyRAS.RSADecrypt(pki, Epassword);//解密
            string[] passwords = Dpassword.Split('#');
            sqlDataReader.Close();
            MySQL.Insert_User_2(lo_conn, username, passwords[0], passwords[1]);
            sqlDataReader.Close();
            lo_conn.Close();
            return 0;
        }


        public Rs_Pki login1(string username)
        {
            Rs_Pki result = new Rs_Pki();

            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();
            if (!MySQL.Exist_User_id(lo_conn, username))
            {
                lo_conn.Close();//关闭连接
                result.rs = -1;
                return result;//用户名不存在，返回错误码
            }
            else//用户存在
            {
                string[] keys = MyRAS.RSAKey();//0:私钥;1:公钥

                //测试用，将公钥输出到文件里，供另一个程序调用。
                string filePath = "D:\\nfc项目\\testData\\" + username + "'s PKI.txt";//   ../abc'sPKI
                System.IO.File.WriteAllText(@filePath, keys[1]);
                //

                result.pki = keys[1];
                MySQL.Insert_Temp_User(lo_conn, username, keys[0]);//在temp表中插入一条记录
                lo_conn.Close();//关闭连接 
                result.rs = 0;
                return result;
            }
        }

    
 

        public int login2(User_Cyphertext user_cyphertext)
        {
            //debug
            /*
            System.IO.Stream sm = System.Web.HttpContext.Current.Request.InputStream;
            int len = (int)sm.Length;
            byte[] inputByts = new byte[len];
            sm.Read(inputByts, 0, len);
            sm.Close();

            string requestData= Encoding.Default.GetString(inputByts);
            //输出到文件
            string filePath = "D:\\nfc项目\\testData\\" + "request.txt" ;
            System.IO.FileStream fs = new System.IO.FileStream(filePath, System.IO.FileMode.OpenOrCreate);
            byte[] headByts = Encoding.Default.GetBytes(requestData);
            fs.Seek(0, System.IO.SeekOrigin.End);
            fs.Write(headByts, 0, headByts.Length);
            fs.Close();
        */

        //


        string username = user_cyphertext.username;
            string loginPassword = user_cyphertext.cyphertext;
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();

            SqlDataReader sqlDataReader_temp = MySQL.Temp_Info_of_a_user(lo_conn, username);
            string pki = sqlDataReader_temp["Public_key"].ToString();
            sqlDataReader_temp.Close();

            string Password = MyRAS.RSADecrypt(pki, loginPassword);//解密
            SqlDataReader sqlDataReader = MySQL.Info_of_a_user(lo_conn, username);
            string a = sqlDataReader["Password"].ToString();
            if (Password.CompareTo(sqlDataReader["Password"].ToString()) == 0)
            {
                sqlDataReader.Close();
                MySQL.Update_Public_key(lo_conn, username, pki);
                MySQL.Delete_Temp_User(lo_conn, username);
                lo_conn.Close();
                return 0;
            }
            else
            {
                sqlDataReader.Close();
                lo_conn.Close();
                return -1;
            }
        }

        //密码错返回-1＊剩余尝试次数 值为-1表示已锁定
        public int turnToPasswordFreeMode(User_Cyphertext user_cyphertext)
        {
            string username = user_cyphertext.username;
            string paymentPassword = user_cyphertext.cyphertext;
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();

            int rs = MySQL.Timeup_Reset_Remain_try(lo_conn, username);//timeup reset
            SqlDataReader sqlDataReader = MySQL.Info_of_a_user(lo_conn, username);
            string pki = sqlDataReader["Public_key"].ToString();
            string Payword = MyRAS.RSADecrypt(pki, paymentPassword);//解密

            if (rs < 0)//锁定
            {
                sqlDataReader.Close();
                lo_conn.Close();
                return -rs;
            }

            if (Payword.CompareTo(sqlDataReader["Payword"].ToString()) == 0)//密码正确
            {
                sqlDataReader.Close();
                MySQL.Set_No_payword(lo_conn, username, 1);
                MySQL.Reset_Remain_try(lo_conn, username, DateTime.Now);
                lo_conn.Close();
                return 0;
            }
            else//密码错误
            {
                int a = int.Parse(sqlDataReader["Remain_try"].ToString()) - 1; 
                sqlDataReader.Close();
                MySQL.Decrease_Remain_try(lo_conn, username, DateTime.Now);
                lo_conn.Close();
                return -a-1;
            }
        }

        public int exitPasswordFreeMode(User_Cyphertext user_cyphertext)
        {
            string username = user_cyphertext.username;
            string loginPassword = user_cyphertext.cyphertext;
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();

            SqlDataReader sqlDataReader = MySQL.Info_of_a_user(lo_conn, username);
            string pki = sqlDataReader["Public_key"].ToString();
            string Password = MyRAS.RSADecrypt(pki, loginPassword);//解密

            if (Password.CompareTo(sqlDataReader["Password"].ToString()) == 0)
            {
                sqlDataReader.Close();
                MySQL.Set_No_payword(lo_conn, username, 0);
                lo_conn.Close();
                return 0;
            }
            else
            {
                sqlDataReader.Close();
                lo_conn.Close();
                return -1;
            }
        }

        //建议：检查参数
        public int getMode(string username)
        {
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();

            SqlDataReader sqlDataReader = MySQL.Info_of_a_user(lo_conn, username);
            bool No_payword = bool.Parse(sqlDataReader["No_payword"].ToString());
            sqlDataReader.Close();
            lo_conn.Close();
            if (No_payword) return 1;
            else return 0;
        }

        public string getBalance(string username)
        {
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();

            SqlDataReader sqlDataReader = MySQL.Info_of_a_user(lo_conn, username);
            string balance = sqlDataReader["Balance"].ToString();
            sqlDataReader.Close();
            lo_conn.Close();
            return balance;
        }

        public Bill[] getBills(string username)//bill_id改成int
        {
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();

            System.Collections.ArrayList all_bills = new ArrayList();
            SqlDataReader sqlDataReader = MySQL.Get_bills_of_a_User(lo_conn, username);

            while (sqlDataReader.Read())
            {
                Bill temp = new Bill();
                temp.bill_id = int.Parse(sqlDataReader["Bill_id"].ToString());
                temp.user_id = sqlDataReader["User_id"].ToString();
                temp.merchant_id = sqlDataReader["Merchant_id"].ToString();
                temp.date = DateTime.Parse(sqlDataReader["Date"].ToString()).ToString("yyyy-MM-dd HH:mm:ss");
                temp.amount = sqlDataReader["Amount"].ToString();
                temp.detail = sqlDataReader["Detail"].ToString();
                all_bills.Add(temp);
            }

            Bill[] Bill_Array = (Bill[])all_bills.ToArray(typeof(Bill));
            sqlDataReader.Close();
            lo_conn.Close();
            return Bill_Array;
        }

        
        public int payWithPassword(PayInfo payInfo)
        {
            string username = payInfo.username;
            string detail = payInfo.detail;
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();

            int rs = MySQL.Timeup_Reset_Remain_try(lo_conn, username);//timeup reset
            SqlDataReader sqlDataReader = MySQL.Info_of_a_user(lo_conn, username);

            //检查是否是确认支付
            bool No_payword = bool.Parse(sqlDataReader["No_payword"].ToString());
            if (No_payword)
            {
                sqlDataReader.Close();
                return -10;
            }

            string pki = sqlDataReader["Public_key"].ToString();
            string correct_Payword = sqlDataReader["Payword"].ToString();
            int correct_Remain_try = int.Parse(sqlDataReader["Remain_try"].ToString());
            double correct_Balance = double.Parse(sqlDataReader["Balance"].ToString());
            sqlDataReader.Close();

            //解密
            string Payword = MyRAS.RSADecrypt(pki, payInfo.paymentPassword);
            DateTime Datestamp = DateTime.Parse(MyRAS.RSADecrypt(pki, payInfo.time));
            string Merchant_id = MyRAS.RSADecrypt(pki, payInfo.merchant_id);
            float Amount = float.Parse(MyRAS.RSADecrypt(pki, payInfo.amount));

            TimeSpan tSpan = DateTime.Now - Datestamp;
            int secondCount = (int)tSpan.TotalSeconds;
            //debug改为120，原来是20
            if (secondCount > 120 || secondCount<0 )//时间戳与现在相差20秒，认为无效
            {
                lo_conn.Close();
                return -1;//时间戳错误
            }

            if (rs < 0)//支付密码锁定状态
            {
                lo_conn.Close();
                return -rs;
            }

            if (Payword.CompareTo(correct_Payword) == 0)//支付密码正确
            {
                MySQL.Reset_Remain_try(lo_conn, username, DateTime.Now);
            }
            else//支付密码错误，返回剩余尝试次数
            {
                MySQL.Decrease_Remain_try(lo_conn, username, DateTime.Now);
                int a = correct_Remain_try - 1;
                lo_conn.Close();
                return -3 - a;
            }

            if (correct_Balance < Amount)//余额不足
            {
                lo_conn.Close();
                return -2;//余额不足
            }
            else
            {
                MySQL.GAIN_of_merchant(lo_conn, Merchant_id, Amount);
                MySQL.PAY_of_user(lo_conn, username, Amount);
                MySQL.Insert_A_new_Bill(lo_conn, username, Merchant_id, Datestamp, Amount, detail);
                lo_conn.Close();
                return 0;//成功
            }
        }

        //只加密支付密码和时间戳
        //返回-10，模式错误
        //成功返回0，时间戳错误-1，余额不足-2，支付密码错误<-3-剩余尝试次数>，支付密码错误锁定<剩余尝试次数为0>返回剩余秒数)
        public int payWithoutPassword(PayInfo payInfo)
        {
            string username = payInfo.username;
            string detail = payInfo.detail;
            string Merchant_id = payInfo.merchant_id;//不解密
            float Amount = float.Parse(payInfo.amount);//不解密
            SqlConnection lo_conn = new SqlConnection(SQLstring);//连接方式选择sqlServer验证方式而不是windows
            lo_conn.Open();

            int rs = MySQL.Timeup_Reset_Remain_try(lo_conn, username);//timeup reset
            SqlDataReader sqlDataReader = MySQL.Info_of_a_user(lo_conn, username);

            bool No_payword = bool.Parse(sqlDataReader["No_payword"].ToString());
            if (!No_payword)
            {
                sqlDataReader.Close();
                return -10;
            }

            string pki = sqlDataReader["Public_key"].ToString();
            string correct_Payword = sqlDataReader["Payword"].ToString();
            int correct_Remain_try = int.Parse(sqlDataReader["Remain_try"].ToString());
            double correct_Balance = double.Parse(sqlDataReader["Balance"].ToString());
            sqlDataReader.Close();

            //解密
            string Payword = MyRAS.RSADecrypt(pki, payInfo.paymentPassword);
            DateTime Datestamp = DateTime.Parse(MyRAS.RSADecrypt(pki, payInfo.time));

            TimeSpan tSpan = DateTime.Now - Datestamp;
            int secondCount = (int)tSpan.TotalSeconds;
            //debug改为120，原来是20
            if (secondCount > 120 || secondCount < 0)//时间戳与现在相差20秒，认为无效
            {
                lo_conn.Close();
                return -1;//时间戳错误
            }

            if (rs < 0)//支付密码锁定状态
            {
                lo_conn.Close();
                return -rs;
            }

            if (Payword.CompareTo(correct_Payword) == 0)//支付密码正确
            {
                MySQL.Reset_Remain_try(lo_conn, username, DateTime.Now);
            }
            else//支付密码错误，返回剩余尝试次数
            {
                MySQL.Decrease_Remain_try(lo_conn, username, DateTime.Now);
                int a = correct_Remain_try - 1;
                lo_conn.Close();
                return -3 - a;
            }

            if (correct_Balance < Amount)//余额不足
            {
                lo_conn.Close();
                return -2;//余额不足
            }
            else
            {
                MySQL.GAIN_of_merchant(lo_conn, Merchant_id, Amount);
                MySQL.PAY_of_user(lo_conn, username, Amount);
                MySQL.Insert_A_new_Bill(lo_conn, username, Merchant_id, Datestamp, Amount, detail);
                lo_conn.Close();
                return 0;//成功
            }

        }


    }
}
