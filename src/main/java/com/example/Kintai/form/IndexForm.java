package com.example.Kintai.form;

import lombok.Getter;
import lombok.Setter;

/**
 * indexにて使用されるgetter,setterを管理するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Setter
@Getter
public class IndexForm {

	// email
	private String email;

	// newUidかforgetPassか判定
	private String transitionLink;

	// sendMaill.htmlにて使用するエラーフラグ
	private boolean sendMailErrorFlg;

	// sendMail.htmlにて使用するエラーフラグ
	private String sendMailErrorMessage;

	// エラーメッセージフラグ
	private boolean isErrorFlg;

	// エラーメッセージ
	private String errorMessage;
}
