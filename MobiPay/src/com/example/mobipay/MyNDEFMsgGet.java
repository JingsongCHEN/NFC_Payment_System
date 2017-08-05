package com.example.mobipay;

import java.nio.charset.Charset;
import java.util.Locale;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;

public class MyNDEFMsgGet {
	public static NdefMessage getNdefMsg_RTD_URI(String data ,byte identifierCode){
		//获得可用的RTD_URI类型的ndefMessage
		byte[] uriField = data.getBytes(Charset.forName("US-ASCII"));
		byte[] payLoad 	= new byte[uriField.length+1];
		payLoad[0] 		= identifierCode;
		System.arraycopy(uriField, 0, payLoad, 1, uriField.length);
		
		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_URI, new byte[0], payLoad);
		return new NdefMessage(new NdefRecord[] {record});
	}
	
	public static NdefMessage getNdefMsg_RTD_TEXT(String data,boolean encodeInUTF8){
		//获得可用的RTD_TEXT类型的ndefMessage
		Locale local = new Locale("en","US");
		byte[] langBytes = local.getLanguage().getBytes(Charset.forName("US-ASCII"));
		Charset utfEndcoding = encodeInUTF8 ? Charset.forName("UTF-8"):Charset.forName("UTF-16"); 
		int utfBit = encodeInUTF8?0 : (1<<7);
		char status = (char)(utfBit+langBytes.length);
		byte[] textBytes = data.getBytes(utfEndcoding);
		byte[] payLoad = new byte[langBytes.length+textBytes.length+1];
		payLoad[0] = (byte)status;
		System.arraycopy(langBytes, 0, payLoad, 1, langBytes.length);
		System.arraycopy(textBytes, 0, payLoad, 1+langBytes.length, textBytes.length);
		
		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], payLoad);
		return new NdefMessage(new NdefRecord[]{record});
	}
	
	public static NdefMessage getNdefMsg_Absolute_URL(String data){
		//获得可用的Absolute_URL类型的ndefMessage
		byte[] payLoad = data.getBytes(Charset.forName("US-ASCII"));
		
		NdefRecord record = new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI,
				new byte[0], new byte[0], payLoad);
		
		return new NdefMessage(new NdefRecord[]{record});
	}
}
