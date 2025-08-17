package com.jp.Kintai.form;

import lombok.Getter;
import lombok.Setter;

/**
 * ControllerからnewUidに渡すgetter,setterを管理するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Setter
@Getter
public class NewUidForm {

	// デコード後のメールアドレス
	private String email;

	// 初期表示か修正表示か判定
	/** trueの場合修正表示、falseの場合初期表示 */
	private boolean isAuthFlg;

	// エンコードのメールアドレス
	private String encodeEmail;

	// エンコード済みのID
	private String encodeId;

	// エンコード済みの名字
	private String encodeFirstName;

	// エンコード済みの名前
	private String encodeLastName;

	// メールアドレスのエラーフラグ
	private boolean errorEmailFlg;

	// メールアドレスのエラーメッセージ
	private String errorEmailMessage;

	// パスワードのエラーフラグ
	private boolean errorPassFlg;

	// パスワードのエラーメッセージ
	private String errorPassMessage;

	// 再パスワードのエラーフラグ
	private boolean errorRePassFlg;

	// 再パスワードのエラーメッセージ
	private String errorRePassMessage;

	// 苗字のエラーフラグ
	private boolean errorFirstNameFlg;

	// 苗字のエラーメッセージ
	private String errorFirstNameMessage;

	// 名前のエラーフラグ
	private boolean errorLastNameFlg;

	// 名前のエラーメッセージ
	private String errorLastNameMessage;

	// パスワード忘れのエラーフラグ
	private boolean isForgetPassErrorFlg;

	// パスワード忘れのメッセージ
	private String forgetPassErrorMessage;
}
