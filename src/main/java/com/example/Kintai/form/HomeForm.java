package com.example.Kintai.form;

import lombok.Getter;
import lombok.Setter;

/**
 * homeにて使用されるFormを管理するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Setter
@Getter
public class HomeForm {

	// email
	private String email;
	// 出勤状態 出勤中=clockIn 退勤中=clockOut 休憩中=breakIn
	private String clockStatus;
	private String clockStatusMessage;
}