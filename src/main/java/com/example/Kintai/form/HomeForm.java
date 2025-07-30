package com.example.Kintai.form;

public class HomeForm {

	// email
	private String email;
	// 出勤状態 出勤中=clockIn 退勤中=clockOut 休憩中=breakIn
	private String clockStatus;
	private String clockStatusMessage;

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setClockStatus(String clockStatus) {
		this.clockStatus = clockStatus;
	}

	public String getClockStatus() {
		return clockStatus;
	}

	public void setClockStatusMessage(String clockStatusMessage) {
		this.clockStatusMessage = clockStatusMessage;
	}

	public String getClockStatusMessage() {
		return clockStatusMessage;
	}
}