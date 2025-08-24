package com.jp.Kintai.util;

import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jp.Kintai.enumClass.LogLevel;

/**
 * keyをもとにメッセージを取得するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */

@Service
public class MessageUtil {

	@Autowired
	private LoggerUtil loggerUtil;

	/** プロパティファイルのパス（resources 以下） */
	private static final String MESSAGE_PROPERTIES = "static/messages";

	/** メッセージID：EMK_034 */
	private static final String EMK034 = "MessageUtil.java/getErrorMessageにてエラーが発生しました。";

	/** 空文字 */
	private static final String EMPTY = "";

	/**
	 * プロパティファイルからvalueを取得するメソッド
	 * @param key キー
	 * @return メッセージ内容
	 */
	public String getErrorMessage(String key){
		String message = EMPTY;
		try{
			ResourceBundle rb = ResourceBundle.getBundle(MESSAGE_PROPERTIES);
			message = rb.getString(key);
		} catch(Exception e){
			String errorMessage = e.toString();
			loggerUtil.LogOutput(LogLevel.ERROR, EMK034, errorMessage);
		}
		return message;
	}
}
