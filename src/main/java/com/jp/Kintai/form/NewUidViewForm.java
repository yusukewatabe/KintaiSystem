package com.jp.Kintai.form;

import lombok.Getter;
import lombok.Setter;

/**
 * newUidからControllerに渡すgetter,setterを管理するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Setter
@Getter
public class NewUidViewForm {

	// 入力されたメールアドレス
	private String inputEmail;

	// 入力されたパスワード
	private String inputPass;

	// 入力された再パスワード
	private String inputRePass;

	// 入力された苗字
	private String inputFirstName;

	// 入力された名前
	private String inputLastName;
}
