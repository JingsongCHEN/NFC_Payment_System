package com.example.mobipay;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.primitives.Bytes;

import android.net.Uri;
import android.nfc.NdefRecord;
import android.util.Log;

public class MyNFCRecordParse {//解析nfcRecord来得到接收到的内容
	public static Uri parseWellKnownUriRecord(NdefRecord record){
		//解析WellKnownUri类型的记录
		byte[] A = record.getType();
		boolean B = Arrays.equals(A, NdefRecord.RTD_URI);
		Preconditions.checkArgument(B);
		byte[] payload = record.getPayload();
		String prefix = URI_PREFIX_MAP.get(payload[0]);
		byte[] fullUri = Bytes.concat(prefix.getBytes(Charset.forName("UTF-8")),
				Arrays.copyOfRange(payload, 1, payload.length));
		Uri uri = Uri.parse(new String(fullUri,Charset.forName("UTF-8")));
		return uri;
	}
	public static void parseAbsoluteUriRecord(NdefRecord record){
		//解析AbsoluteUri类型的记录
		byte[] payload = record.getPayload();
		Uri uri = Uri.parse(new String(payload,Charset.forName("UTF-8")));
		record.getId();
		record.getType();
		record.getTnf();
	}
	public static void parseWellKnownTextRecord(NdefRecord record){
		//解析WellKnownText类型的记录
		Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
		byte[] payload = record.getPayload();
		Byte statusByte = record.getPayload()[0];
		String textEncoding = ((statusByte & 0x80) == 0)? "UTF-8":"UTF-16";
		int langLength = statusByte & 0x3F;
		String langCode = new String(payload,1,langLength,Charset.forName("UTF-8"));
		try{
			String payLoadText = new String(payload,langLength+1,payload.length-langLength-1,textEncoding);
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		
	}
	public static void parseMimeRecord(NdefRecord record){
	
	}
	public static void parseExternalRecord(NdefRecord record){
	
	}
	
	/**
     * NFC Forum "URI Record Type Definition"<p>
     * This is a mapping of "URI Identifier Codes" to URI string prefixes,
     * per section 3.2.2 of the NFC Forum URI Record Type Definition document.
     */
    private static final BiMap<Byte,String> URI_PREFIX_MAP  = ImmutableBiMap.<Byte,String> builder()
    		.put((byte)0x00,"")			.put((byte)0x01,"http://www.")					.put((byte)0x02,"https://www.")
    		.put((byte)0x03,"http://")	.put((byte)0x04,"https://")						.put((byte)0x05,"tel:")
    		.put((byte)0x06,"mailto:")	.put((byte)0x07,"ftp://anonymous:anonymous@")	.put((byte)0x08,"ftp://ftp.")
    		.put((byte)0x09,"ftps://")	.put((byte)0x0A,"sftp://")						.put((byte)0x0B,"smb://")
    		.put((byte)0x0C,"nfs://")	.put((byte)0x0D,"ftp://")						.put((byte)0x0E,"dav://")
    		.put((byte)0x0F,"news:")	.put((byte)0x10,"telnet://")					.put((byte)0x11,"imap:").build();
	
}