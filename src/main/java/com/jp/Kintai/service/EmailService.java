package com.jp.Kintai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.jp.Kintai.enumClass.LogLevel;
import com.jp.Kintai.util.Base64Util;
import com.jp.Kintai.util.LoggerUtil;
import com.jp.Kintai.util.MessageUtil;
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

	@Autowired
	private Base64Util base64Util;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private LoggerUtil loggerUtil;

	/** メールアドレスに添付するURL */
	private static final String URL_NEWUID_LINK = "http://localhost:8081/api/auth/verify?token=";

	private static final String URL_FORGET_LINK = "http://localhost:8081/api/auth/verify/password?token=";

	/** メールアドレスのタイトル(新規ユーザー登録) */
	private static final String MAIL_TITLE_NEWUSER = "勤怠管理システムからメール認証のお願い";

	/** メールアドレスのタイトル(パスワード忘れ) */
	private static final String MAIL_TITLE_FORGET_PASSWORD = "勤怠管理システムからパスワード再入力のお願い";

	/** メールコンテンツ内のタイトル */
	private static final String MAIL_CONTENT_TITILE_NEWUSER = "<p>以下のリンクをクリックして認証を完了してください</p>";

	/** メールコンテンツ内のタイトル */
	private static final String MAIL_CONTENT_TITILE_FORGETPASSWORD = "<p>以下のリンクをクリックしてパスワードの再発行を完了してください</p>";

	/** メールコンテンツ内の最初のbody */
	private static final String MAIL_CONTENT_BODY_FIRST = "<p><a href='";

	/** メールコンテンツ内の最初のbody */
	private static final String MAIL_CONTENT_BODY_SECOND = "'>認証リンク</a></p>";

	/** メッセージID：EMK_031 */
	private static final String EMK031 = "EMK_031";

	/** メッセージID：EMK_032 */
	private static final String EMK032 = "EMK_032";

	/**
	 * 新規ユーザーのメール認証送信するメソッド
	 * 
	 * @param toEmail メールアドレス
	 * @return true or false
	 */
	public void sendVerificationEmail(String toEmail) {

		// エンコード
		String base64Encode = base64Util.base64Encode(toEmail);

		String link = URL_NEWUID_LINK + base64Encode;
		String content = MAIL_CONTENT_TITILE_NEWUSER + MAIL_CONTENT_BODY_FIRST + 
						link + MAIL_CONTENT_BODY_SECOND;

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toEmail);
			helper.setSubject(MAIL_TITLE_NEWUSER);
			helper.setText(content, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			String errorMessage = e.toString();
			loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK031), errorMessage);
		}
	}

	/**
	 * パスワード忘れのメール認証送信するメソッド
	 * 
	 * @param toEmail メールアドレス
	 * @return true or false
	 */
	public void forgetPassSendEmail(String toEmail) {

		// エンコード
		String base64Encode = base64Util.base64Encode(toEmail);

		String link = URL_FORGET_LINK + base64Encode;
		String content = MAIL_CONTENT_TITILE_FORGETPASSWORD + MAIL_CONTENT_BODY_FIRST + 
						link + MAIL_CONTENT_BODY_SECOND;

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toEmail);
			helper.setSubject(MAIL_TITLE_FORGET_PASSWORD);
			helper.setText(content, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			String errorMessage = e.toString();
			loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK032), errorMessage);
		}
	}
}
