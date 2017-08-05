package com.example.mobipay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Account类的方法用于与服务器通信
 * @author hzw
 */
public class Account {
    public static String username;
    public static String loginPassword;
    public static String payPassword;
    public static PublicKey publicKey;

    /**
     * 注册用户
     * @return responseCode 成功返回0，返回-1则用户名重复
     * @throws Exception
     */
    public static int register()
            throws Exception {
        //  设置URL地址
        String urlFirst = HTTPUtil.BASE_URL + "register1";
        String urlSecond = HTTPUtil.BASE_URL + "register2";
        //  将用户名打包成json格式并建立Http连接
        String responseString = HTTPUtil.HTTPPost(urlFirst, "\"" + username + "\"");
        JSONObject jsonObjectResponseFirst = new JSONObject(responseString);
        int responseCode = jsonObjectResponseFirst.getInt("rs");
        if (responseCode == -1) {
            return responseCode;
        }
        else if(responseCode == 0) {
            //  获取公钥
            String publicKeyXml = jsonObjectResponseFirst.getString("pki");
            HashMap<String,String> rsaParams = RSA.rsaParameters(publicKeyXml);
            PublicKey publicKey = RSA.getPublicKey(rsaParams.get("modulus"), rsaParams.get("exponent"));
            //  加密登录密码和支付密码
            String plaintextString = loginPassword + "#" + payPassword;
            byte[] plaintext = plaintextString.getBytes();
            String cyphertext = RSA.encrypt(plaintext, publicKey);
            //  将用户名和加密后的登录密码和支付密码打包成json格式
            JSONObject jsonObjectSecond = new JSONObject();
            jsonObjectSecond.put("username", username);
            jsonObjectSecond.put("cyphertext", cyphertext);
            //  建立Http连接
            responseString = HTTPUtil.HTTPPost(urlSecond, jsonObjectSecond.toString());
            return Integer.parseInt(responseString);
        }
        return responseCode;
    }

    /**
     * 用户登录
     * @return responseCode 成功返回0，返回-1则登录密码错误
     * @throws Exception
     */
    public static int login() throws Exception {
        //  设置URL地址
        String urlFirst = HTTPUtil.BASE_URL + "login1";
        String urlSecond = HTTPUtil.BASE_URL + "login2";

        //  将用户名打包成json格式并建立Http连接
        String responseString = HTTPUtil.HTTPPost(urlFirst, "\"" + username + "\"");
        JSONObject jsonObjectResponseFirst = new JSONObject(responseString);
        int responseCode = jsonObjectResponseFirst.getInt("rs");
        if (responseCode == -1) {
            System.out.println("登录用户" + username + "失败！");
            return responseCode;
        } else if (responseCode == 0) {
            //  获取公钥
            String publicKeyXml = jsonObjectResponseFirst.getString("pki");
            HashMap<String, String> rsaParams = RSA.rsaParameters(publicKeyXml);
            publicKey = RSA.getPublicKey(rsaParams.get("modulus"), rsaParams.get("exponent"));
            //  加密登录密码
            String plaintextString = loginPassword;
            byte[] plaintext = plaintextString.getBytes();
            String cyphertext = RSA.encrypt(plaintext, publicKey);
            //  将用户名和加密后的登录密码打包成json格式
            JSONObject jsonObjectSecond = new JSONObject();
            jsonObjectSecond.put("username", username);
            jsonObjectSecond.put("cyphertext", cyphertext);
            //  建立Http连接
            responseString = HTTPUtil.HTTPPost(urlSecond, jsonObjectSecond.toString());
            return Integer.parseInt(responseString);
        }
        return responseCode;
    }

    /**
     * 转换为免密支付模式
     * @return responseCode 成功返回0，密码错返回-1＊剩余尝试次数，已锁定返回1＊等待秒数
     * @throws Exception
     */
    public static int turnToPasswordFreeMode() throws Exception {
        //  设置URL地址
        String url = HTTPUtil.BASE_URL + "turnToPasswordFreeMode";
        //  加密支付密码
        String plaintextString = payPassword;
        byte[] plaintext = plaintextString.getBytes();
        String cyphertext = RSA.encrypt(plaintext, publicKey);
        //  将用户名和加密后的支付密码打包成json格式
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("cyphertext", cyphertext);
        //  建立Http连接
        String responseString = HTTPUtil.HTTPPost(url, jsonObject.toString());
        return Integer.parseInt(responseString);
    }

    /**
     * 转换为确认支付模式
     * @return responseCode 成功返回0
     * @throws Exception
     */
    public static int exitPasswordFreeMode() throws Exception {
        //  设置URL地址
        String url = HTTPUtil.BASE_URL + "exitToPasswordFreeMode";
        //  加密登录密码
        String plaintextString = loginPassword;
        byte[] plaintext = plaintextString.getBytes();
        String cyphertext = RSA.encrypt(plaintext, publicKey);
        //  将用户名和加密后的登录密码打包成json格式
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("cyphertext", cyphertext);
        //  建立Http连接
        String responseString = HTTPUtil.HTTPPost(url, jsonObject.toString());
        return Integer.parseInt(responseString);
    }

    /**
     * 获取当前支付模式
     * @return responseCode 确认支付返回0，免密支付返回1
     * @throws Exception
     */
    public static int getMode() throws Exception {
        //  设置URL地址
        String url = HTTPUtil.BASE_URL + "getMode";
        //  将用户名打包成json格式并建立Http连接
        String responseString = HTTPUtil.HTTPPost(url, "\"" + username + "\"");
        return Integer.parseInt(responseString);
    }

    /**
     * 查询用户余额
     * @return balance 用户余额
     * @throws Exception
     */
    public static String getBalance() throws Exception {
        //  设置URL地址
        String url = HTTPUtil.BASE_URL + "getBalance";
        //  将用户名打包成json格式并建立Http连接
        String responseString = HTTPUtil.HTTPPost(url, "\"" + username + "\"");
        return responseString;
    }

    /**
     * 查询账单
     * @return Bill[] 用户账单信息数组
     * @throws Exception
     */
    public static Bill[] getBills() throws Exception {
        //  设置URL地址
        String url = HTTPUtil.BASE_URL + "getBalance";
        //  初始化账单数组
        Bill[] bill = new Bill[20];
        //  将用户名打包成json格式并建立Http连接
        String responseString = HTTPUtil.HTTPPost(url, "\"" + username + "\"");
        JSONArray jsonArray = new JSONArray(responseString);
        int size = jsonArray.length();
        Bill tmp = new Bill();
        for (int i = 0; i < size; i++) {
            //  保存每一条账单信息
            tmp = new Bill();
            JSONObject jsonObjectResponse = jsonArray.getJSONObject(i);
            tmp.setBill_id(jsonObjectResponse.getString("bill_id"));
            tmp.setUser_id(jsonObjectResponse.getString("user_id"));
            tmp.setMerchant_id(jsonObjectResponse.getString("merchant_id"));
            tmp.setDate(jsonObjectResponse.getString("date"));
            tmp.setAmount(jsonObjectResponse.getString("amount"));
            tmp.setDetail(jsonObjectResponse.getString("detail"));
            Data.bill.add(tmp);
        }
        return bill;
    }

    /**
     * 确认模式下支付
     * @param merchant_id 商家ID
     * @param amount 金额
     * @param detail 账单详情
     * @return responseCode (成功返回0，时间戳错误-1，余额不足-2，支付密码错误<-3-剩余尝试次数>，支付密码错误锁定<剩余尝试次数为0>返回剩余秒数
     * @throws Exception
     */
    public static int  payWithPassword(String merchant_id, String amount, String detail) throws Exception {
        //  设置URL地址
        String url = HTTPUtil.BASE_URL + "payWithPassword";
        //  将用户名、加密后的支付密码、加密后的时间戳、加密后的商家ID、加密后的金额和账单详情打包成json格式
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        //  加密支付密码
        String plaintextPayPasswordString = payPassword;
        byte[] plaintextPayPassword = plaintextPayPasswordString.getBytes();
        String cyphertextPayPassword = RSA.encrypt(plaintextPayPassword, publicKey);
        jsonObject.put("paymentPassword", cyphertextPayPassword);
        //  加密时间戳
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String plaintextDateString = simpleDateFormat.format(date);
        byte[] plaintextDate = plaintextDateString.getBytes();
        String cyphertextDate = RSA.encrypt(plaintextDate, publicKey);
        jsonObject.put("time", cyphertextDate);
        //  加密商家ID
        String plaintextMerchantIdString = merchant_id;
        byte[] plaintextMerchantId = plaintextMerchantIdString.getBytes();
        String cyphertextMerchantId = RSA.encrypt(plaintextMerchantId, publicKey);
        jsonObject.put("merchant_id", cyphertextMerchantId);
        //  加密金额
        String plaintextAmountString = amount;
        byte[] plaintextAmount = plaintextAmountString.getBytes();
        String cyphertextAmount = RSA.encrypt(plaintextAmount, publicKey);
        jsonObject.put("aoumnt", cyphertextAmount);

        jsonObject.put("detail", detail);
        //  建立Http连接
        String responseString = HTTPUtil.HTTPPost(url, jsonObject.toString());
        return Integer.parseInt(responseString);
    }

    /**
     * 封装用户名、加密后的支付密码、加密后的时间戳成json格式
     * @return String json格式的字符串
     * @throws Exception
     */
    public static String encapsulateMessage() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        //  加密支付密码
        String plaintextPayPasswordString = payPassword;
        byte[] plaintextPayPassword = plaintextPayPasswordString.getBytes();
        String cyphertextPayPassword = RSA.encrypt(plaintextPayPassword, publicKey);
        jsonObject.put("cyphertextPayPassword", cyphertextPayPassword);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String plaintextDateString = simpleDateFormat.format(date);
        byte[] plaintextDate = plaintextDateString.getBytes();
        String cyphertextDate = RSA.encrypt(plaintextDate, publicKey);
        jsonObject.put("cyphertextDate", cyphertextDate);

        return jsonObject.toString();
    }
}
