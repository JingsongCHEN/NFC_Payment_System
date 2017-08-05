package com.example.merchant;

import org.json.JSONObject;

import android.util.Log;

public class Merchant {
	 public static int payWithoutPassword(String username, String cyphertextPayPassword,
             String cyphertextDate, String merchantId,
             String amount, String detail) throws Exception {
			String url = HTTPUtil.BASE_URL + "payWithoutPassword";			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", username);
			jsonObject.put("paymentPassword", cyphertextPayPassword);
			jsonObject.put("time", cyphertextDate);
			jsonObject.put("merchant_id", merchantId);
			jsonObject.put("amount", amount);
			jsonObject.put("detail", detail);
			String responseString = HTTPUtil.HTTPPost(url, jsonObject.toString());
			Log.i("sadfsf:", jsonObject.toString());
			return Integer.parseInt(responseString);
}
	 public static String encapsulateMessage() throws Exception {
	        JSONObject jsonObject = new JSONObject();

	        jsonObject.put("merchant_id", Data.merchantID);
	        jsonObject.put("amount", Data.amount);
	        jsonObject.put("detail", Data.detail);
	        Log.i("jsonObject.toString()", jsonObject.toString());
	        return jsonObject.toString();
	    }

}
