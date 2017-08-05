package com.example.merchant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.HttpURLConnection;

public class HTTPUtil {
    public static final String IP = "192.168.173.1";
    public static final String BASE_URL = "http://" + IP + ":54321/Service1.svc/";

    public static String HTTPPost(String url, String params) {
    	
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", "application/json");
            
            out = new PrintWriter(conn.getOutputStream());
            out.print(params);
            out.flush();
            System.out.println(conn.getResponseCode()+"+++++++++++++++++++++");
            
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            conn.disconnect();
        }
        catch (Exception e) {
            System.out.println("閸欐垿锟戒赋OST鐠囬攱鐪伴崙铏瑰箛瀵倸鐖堕敍锟�" + e);
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
