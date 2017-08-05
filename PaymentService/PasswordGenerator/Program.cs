using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SqlClient; //连接SQLServer 数据库专用
using System.Security.Cryptography;

namespace PasswordGenerator
{

    abstract class MyRAS
    {
        public static int MAXDECRYPTSIZE = 128;

        public static string RSAEncrypt(string xmlPublicKey, string m_strEncryptString)
        {
            string str2;
            try
            {
                RSACryptoServiceProvider provider = new RSACryptoServiceProvider();
                provider.FromXmlString(xmlPublicKey);
                byte[] bytes = new UnicodeEncoding().GetBytes(m_strEncryptString);
                str2 = Convert.ToBase64String(provider.Encrypt(bytes, false));
            }
            catch (Exception exception)
            {
                throw exception;
            }
            return str2;
        }

        //RSA解密  对经过Base64编码的密文用私钥解密
        public static string RSADecrypt(string privateKey, string encryptData)
        {
            string decryptData = "";
            
                RSACryptoServiceProvider provider = new RSACryptoServiceProvider();
                provider.FromXmlString(privateKey);
                byte[] bEncrypt = Convert.FromBase64String(encryptData);
                int length = bEncrypt.Length;
                int offset = 0;
                string cache;
                int i = 0;
                while (length - offset > 0)
                {
                    if (length - offset > MAXDECRYPTSIZE)
                    {
                        cache = Encoding.UTF8.GetString(provider.Decrypt(getSplit(bEncrypt, offset, MAXDECRYPTSIZE), false));
                    }
                    else
                    {
                        cache = Encoding.UTF8.GetString(provider.Decrypt(getSplit(bEncrypt, offset, length - offset), false));
                    }
                    decryptData += cache;
                    i++;
                    offset = i * MAXDECRYPTSIZE;
                }
            
            
            return decryptData;
        }

        /// <summary>  
        /// 截取字节数组部分字节  
        /// </summary>  
        /// <param name="input"></param>  
        /// <param name="offset">起始偏移位</param>  
        /// <param name="length">截取长度</param>  
        /// <returns></returns>  
        private static byte[] getSplit(byte[] input, int offset, int length)
        {
            byte[] output = new byte[length];
            for (int i = offset; i < offset + length; i++)
            {
                output[i - offset] = input[i];
            }
            return output;
        }


        // 产生公钥和私钥对    string[] 0:私钥;1:公钥
        public static string[] RSAKey()
        {
            string[] keys = new string[2];
            System.Security.Cryptography.RSACryptoServiceProvider rsa = new RSACryptoServiceProvider(1024);
            keys[0] = rsa.ToXmlString(true);
            keys[1] = rsa.ToXmlString(false);
            return keys;
        }
    }

    class Program
    {

        static void Main(string[] args)
        {
           string username = Console.ReadLine();
           string filePath3 = "D:\\nfc项目\\testData\\" + username + "'s PKI.txt";//   ../abc's PKI
           string pki = System.IO.File.ReadAllText(filePath3);
            /* 
            string filePath2 = "D:\\nfc项目\\testData\\" + username + "'s password.txt";
           using (System.IO.StreamWriter file =
           new System.IO.StreamWriter(@filePath2))
           {
               string key = Console.ReadLine();
               while (!key.Equals(" "))
               {
                   file.WriteLine(key + ":");
                   file.WriteLine(MyRAS.RSAEncrypt(pki, key));
                   key = Console.ReadLine();
               }
           }*/
            generatePayInfo(username, pki);

        }

        static void date(string username, string pki) {
            string now = DateTime.Now.ToString();

            string encrypted = MyRAS.RSAEncrypt(pki, now);
            string privateKey = "<RSAKeyValue><Modulus>tI0Z9oXgXRbHxt6ski7f4NM+xPIlTZHNrORkZl20KAuWhbeGA3N9tvS+Lff0F18EauSrU6Q/BSmvKHiVdrr/7Ctxld/PA2d7oojHLTTzJa9BdVyaBfpxInFod+69GJo29FNvvd5+Bfm++aZunuSMbePtMHhHRw2DwbycPc/Mii8=</Modulus><Exponent>AQAB</Exponent><P>12nD8GCLId8rvO5ysnsKOAuCQ5kF7kC3m9UH6sxxejTvfEKzhB1KXFHhoPdhHJWcCK4mUiA1FZL8klNQaxXiaw==</P><Q>1pHN6NF6XmqRj6CCPoh7LgMG56LBRHdMJXgdMeTeVtypt3JWSau8A9VVFFPe/vewg02v5QTohZ4TmA+A4+JQTQ==</Q><DP>L+tsViXz7YYK4c2doEy0ukD9KnJ5GkY1Uioyx63RNKFgASGJc8NcTms8qSU+6i/x9RBgn/ZgOLtpClgUVK4anw==</DP><DQ>ojfLL4gndnglIivcLACs9+VL9BWE/gkfJXjAwbbhLVRYbxtfBwVx+IfxcZPTFHoYFbzHDWL/k/0lnSGjdSXlRQ==</DQ><InverseQ>jGxtKAsnJzbgeUTJAsVElbv3jqX8qJK/lVIUIyRP6Cv5Bej+FhVg+CekczIKICmfkIboeslUeJceEtEcfY+v9g==</InverseQ><D>CIsIqHaLODxktY9BwSUAqOW1+aGvjC0S+1RPP9y9Movh0m/tomorj6Wi/g/Wd+iiD+DEDpAd/0OKqzKRd+2/QDwxgl1Bvm3bGYqw6lCknQP5qd1oJTZUFxSkJlYUAqsp3lbxAaYsuk9tM+cvEkQWdmBv5iJg2G81ZHuTxgOOiJk=</D></RSAKeyValue>";
            Console.WriteLine(encrypted);
            string decrypted = MyRAS.RSADecrypt(privateKey, encrypted);
            string[] s = decrypted.Split('\0');
            decrypted = "";
            for (int i = 0; i < s.Length; i++)
            {
                decrypted += s[i];
            }
            Console.WriteLine(decrypted);

            Console.ReadLine();
        }

        //支付密码，商家id,金额，由文本确定
        //时间戳分别为当前的和过时的
        static void generatePayInfo(string username,string pki) {
            string filePath = "D:\\nfc项目\\testData\\" + username + "'s text.txt";//   ../abc's PKI
            string[] text = (string[])(System.IO.File.ReadLines(filePath).ToArray<string>());
            string password = text[0];
            string mechant_id = text[1];
            string amount_string = text[2];
            string detail = text[3];
            string now = DateTime.Now.ToString();
            string outOfDate = "2016/4/4 11:02:26";
            string filePath2 = "D:\\nfc项目\\testData\\" + username + "'s payInfo.txt";
            using (System.IO.StreamWriter file =
            new System.IO.StreamWriter(@filePath2))
            {
                string p = MyRAS.RSAEncrypt(pki, password);
                file.WriteLine(MyRAS.RSAEncrypt(pki, password));
                file.WriteLine(MyRAS.RSAEncrypt(pki, now));
                file.WriteLine(MyRAS.RSAEncrypt(pki, mechant_id));
                file.WriteLine(MyRAS.RSAEncrypt(pki, amount_string));
                file.WriteLine(p.Length);

                file.WriteLine(MyRAS.RSAEncrypt(pki, password));
                file.WriteLine(MyRAS.RSAEncrypt(pki, outOfDate));
                file.WriteLine(MyRAS.RSAEncrypt(pki, mechant_id));
                file.WriteLine(MyRAS.RSAEncrypt(pki, amount_string));
                file.WriteLine();
            }
        }
    }
}

    
