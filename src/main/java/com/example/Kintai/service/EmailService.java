package com.example.Kintai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 新規ユーザー、パスワード忘れに伴うメール送信に関するビジネスロジックが記載されているクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	/**
	 * 新規ユーザーのメール認証送信するメソッド
	 * 
	 * @param toEmail メールアドレス
	 */
	public void sendVerificationEmail(String toEmail) {
		String base64Encode = null;
		try {
			// エンコード
			Base64.Encoder encoder = Base64.getEncoder();
			base64Encode = encoder.encodeToString(toEmail.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String link = "http://localhost:8081/api/auth/verify?token=" + base64Encode;
		String subject = "勤怠管理システムからメール認証のお願い";
		String content = "<p>以下のリンクをクリックして認証を完了してください</p>"
				+ "<p><a href='" + link + "'>認証リンク</a></p>";

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(content, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * パスワード忘れのメール認証送信するメソッド
	 * 
	 * @param toEmail メールアドレス
	 */
	public void forgetPassSendEmail(String toEmail) {
		String base64Encode = null;
		try {
			// エンコード
			Base64.Encoder encoder = Base64.getEncoder();
			base64Encode = encoder.encodeToString(toEmail.getBytes(StandardCharsets.UTF_8));

		} catch (Exception e) {
			e.printStackTrace();
		}

		String link = "http://localhost:8081/api/auth/verify/password?token=" + base64Encode;
		String subject = "勤怠管理システムからパスワード再入力のお願い";
		String content = "<p>以下のリンクをクリックしてパスワードの再発行を完了してください</p>"
				+ "<p><a href='" + link + "'>認証リンク</a></p>";

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(content, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
