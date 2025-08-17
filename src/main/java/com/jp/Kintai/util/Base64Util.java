package com.jp.Kintai.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;

/**
 * Base64エンコード、デコードに関するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Service
public class Base64Util {

	/**
	 * base64Encodeを行うメソッド
	 * @param beforeEncode エンコード前文字列
	 * @return エンコード文字列
	 */
	public String base64Encode(String beforeEncode) throws Exception {
		Base64.Encoder encoder = Base64.getEncoder();
		String encoded = encoder.encodeToString(beforeEncode.getBytes(StandardCharsets.UTF_8));
		return encoded;
	}

	/**
	 * base64Decodeを行うメソッド
	 * @param beforeDecode デコード前文字列
	 * @return デコード文字列
	 */
	public String base64Decode(String beforeDecode) throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();
		String decoded = new String(decoder.decode(beforeDecode.getBytes(StandardCharsets.UTF_8)));
		return decoded;
	}
}
