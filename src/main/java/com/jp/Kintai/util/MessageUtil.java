package com.jp.Kintai.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * keyをもとにメッセージを取得するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */

@Service
public class MessageUtil {

	private static final Logger logger = LogManager.getLogger(MessageUtil.class);

	/** プロパティファイルのパス（resources 以下） */
	private static final String MESSAGE_PROPERTIES = "static/messages";

	/**
	 * プロパティファイルからvalueを取得するメソッド
	 * @param key キー
	 * @return メッセージ内容
	 */
	public String getErrorMessage(String key){
		String message = "";
		try{
			ResourceBundle rb = ResourceBundle.getBundle(MESSAGE_PROPERTIES);
			message = rb.getString(key);

		} catch(Exception e){
			// TODO:logger使う
			logger.error("[ERROR]エラーが発生しました。" + e);
			throw new MissingResourceException(message, message, key);
		}

		return message;
	}
}
