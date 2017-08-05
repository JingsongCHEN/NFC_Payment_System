package com.example.mobipay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * HTTP POST方法通信的工具类
 * @author hzw
 */
public class HTTPUtil {
    //  设置URL基地址
    public static final String IP = "192.168.1.1";
    public static final String BASE_URL = "http://" + IP + ":54321/Service1.svc/";
    /**
     * 向指定URL发送POST方法的请求
     * @param url 发送请求的URL
     * @param params json格式的请求参数
     * @return URL所代表远程资源的响应
     */
    public static String HTTPPost(String url, String params) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {
            URL realUrl = new URL(url);
            //  打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            //  设置为POST方法
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            //  设置传输内容格式为json
            conn.setRequestProperty("Content-Type", "application/json");

            //  获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //  发送请求参数
            out.print(params);
            //  flush输出流的缓冲
            out.flush();

            //  定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }
        catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
