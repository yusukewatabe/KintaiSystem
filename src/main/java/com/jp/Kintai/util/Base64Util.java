package com.jp.Kintai.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jp.Kintai.enumClass.LogLevel;

/**
 * Base64エンコード、デコードに関するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Service
public class Base64Util {

	@Autowired
	private LoggerUtil loggerUtil;

	@Autowired
	private MessageUtil messageUtil;

	/** メッセージID：EMK_030 */
	private static final String EMK030 = "EMK_030";

	/** 空文字 */
	private static final String EMPTY = "";

	/**
	 * base64Encodeを行うメソッド
	 * @param beforeEncode エンコード前文字列
	 * @return エンコード文字列
	 */
	public String base64Encode(String beforeEncode) {
		Base64.Encoder encoder = Base64.getEncoder();
		String encoded = encoder.encodeToString(beforeEncode.getBytes(StandardCharsets.UTF_8));
		return encoded;
	}

	/**
	 * base64Decodeを行うメソッド
	 * @param beforeDecode デコード前文字列
	 * @return デコード文字列
	 */
	public String base64Decode(String beforeDecode) {
		Base64.Decoder decoder = Base64.getDecoder();
		String decoded = null;
		try {
			decoded = new String(decoder.decode(beforeDecode.getBytes(StandardCharsets.UTF_8)));
		} catch (IllegalArgumentException e) {
			String errorMessage = e.toString();
			loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK030), errorMessage);
			decoded = EMPTY;
		}
		return decoded;
	}
}
