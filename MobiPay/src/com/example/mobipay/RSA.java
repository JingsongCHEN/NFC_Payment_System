package com.example.mobipay;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class RSA {
    public static final int MAXENCRYPTSIZE = 128;

    public static String encodeBase64(byte[]input) throws Exception{
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod= clazz.getMethod("encode", byte[].class);
        mainMethod.setAccessible(true);
        Object retObj=mainMethod.invoke(null, new Object[]{input});
        return (String)retObj;
    }

    public static byte[] decodeBase64(String input) throws Exception{
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod= clazz.getMethod("decode", String.class);
        mainMethod.setAccessible(true);
        Object retObj=mainMethod.invoke(null, input);
        return (byte[])retObj;
    }

    /**
     * 返回包含模数modulus和指数exponent的haspMap
     * @return
     * @throws MalformedURLException
     * @throws DocumentException
     */
    public static HashMap<String,String> rsaParameters(String xmlPublicKey) throws MalformedURLException, DocumentException {
        HashMap<String ,String> map = new HashMap<String, String>();
        Document doc = DocumentHelper.parseText(xmlPublicKey);
        
        String modulus = (String) doc.getRootElement().element("Modulus").getData();
        String exponent = (String) doc.getRootElement().element("Exponent").getData();
        map.put("modulus", modulus);
        map.put("exponent", exponent);
        return map;
    }

    /**
     * 返回RSA公钥
     * @param modulus
     * @param exponent
     * @return
     */
    public static PublicKey getPublicKey(String modulus, String exponent){
        try {
            byte[] m = decodeBase64(modulus);
            byte[] e = decodeBase64(exponent);
            BigInteger b1 = new BigInteger(1,m);
            BigInteger b2 = new BigInteger(1,e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(byte[] source, PublicKey publicKey) throws Exception   {
        String encryptData ="";
        try {
            //Cipher cipher = Cipher.getInstance("RSA");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int length = source.length;
            int offset = 0;
            byte[] cache;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int i = 0;
            while(length - offset > 0){
                if(length - offset > MAXENCRYPTSIZE){
                    cache = cipher.doFinal(source, offset, MAXENCRYPTSIZE);
                }else{
                    cache = cipher.doFinal(source, offset, length - offset);
                }
                outStream.write(cache, 0, cache.length);
                i++;
                offset = i * MAXENCRYPTSIZE;
            }
            return encodeBase64(outStream.toByteArray());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return encryptData;
    }
}
