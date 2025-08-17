package com.jp.Kintai.controller;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jp.Kintai.constant.FormConstant;
import com.jp.Kintai.constant.MappingPathNameConstant;
import com.jp.Kintai.constant.ViewNameConstant;
import com.jp.Kintai.form.IndexForm;
import com.jp.Kintai.service.EmailService;
import com.jp.Kintai.util.MessageUtil;

/**
 * sendMail.htmlからのリクエストを処理するクラスです。
 * 
 * @author Watabe Yusuke
 * @version 0.1
 **/
@Controller
public class SendMailController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private MessageUtil messageUtil;

	/**  メッセージID：EMK_001 */
	private static final String EMK001 = "EMK_001";

	/**  メッセージID：EMK_010 */
	private static final String EMK010 = "EMK_010";

	/**
	 * メールアドレスが正しいか判定し、該当メールアドレスに新規ユーザー登録のメールを送信するメソッド
	 * 
	 * @param id メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.SENDMAIL_NEWUID_PATH)
	public String sendMail(@RequestParam String id, Model model) {
		IndexForm indexForm = new IndexForm();

		if (id.isEmpty() || id == null) {
			indexForm.setSendMailErrorFlg(true);
			indexForm.setSendMailErrorMessage(messageUtil.getErrorMessage(EMK001));
			model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
			return ViewNameConstant.MAIL_SENDMAIL_VIEW;
		} else {
			if (EmailValidator.getInstance().isValid(id)) {
				if(!emailService.sendVerificationEmail(id)){
					return "error";
				}
				return ViewNameConstant.MAIL_SENDMAIL_RESULT_VIEW;
			} else {
				indexForm.setSendMailErrorFlg(true);
				indexForm.setSendMailErrorMessage(messageUtil.getErrorMessage(EMK010));
				model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
				return ViewNameConstant.MAIL_SENDMAIL_VIEW;
			}
		}
	}

	/**
	 * メールアドレスが正しいか判定し、該当メールアドレスにパスワード忘れのメールを送信するメソッド
	 * 
	 * @param id メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.SENDMAIL_FORGET_PATH)
	public String sendMailForget(@RequestParam String id, Model model) {
		IndexForm indexForm = new IndexForm();

		if (id.isEmpty() || id == null) {
			indexForm.setSendMailErrorFlg(true);
			indexForm.setSendMailErrorMessage(messageUtil.getErrorMessage(EMK001));
			model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
			return ViewNameConstant.MAIL_SENDMAIL_VIEW;
		} else {
			if (EmailValidator.getInstance().isValid(id)) {
				if(!emailService.forgetPassSendEmail(id)){
					return "error";
				}
				return ViewNameConstant.MAIL_SENDMAIL_RESULT_VIEW;
			} else {
				indexForm.setSendMailErrorFlg(true);
				indexForm.setSendMailErrorMessage(messageUtil.getErrorMessage(EMK010));
				model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
				return ViewNameConstant.MAIL_SENDMAIL_VIEW;
			}
		}
	}
}
