package com.jp.Kintai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jp.Kintai.constant.DateFormatConstant;
import com.jp.Kintai.constant.FormConstant;
import com.jp.Kintai.constant.MappingPathNameConstant;
import com.jp.Kintai.constant.ViewNameConstant;
import com.jp.Kintai.form.NewUidForm;
import com.jp.Kintai.service.UserService;
import com.jp.Kintai.util.Base64Util;
import com.jp.Kintai.util.MessageUtil;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

/**
 * Home.htmlからのリクエストを処理するクラスです。
 * 
 * @author Watabe Yusuke
 * @version 0.1
 **/
@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private Base64Util base64Util;

	/** メッセージID：EMK_027 */
	private static final String EMK027 = "EMK_027";

	/** メッセージID：EMK_028 */
	private static final String EMK028 = "EMK_028";

	/**
	 * index以外からhomeへ遷移が行われた際に使用されるメソッド
	 * 
	 * @param id メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.HOME_PATH)
	public String backHome(String userId, Model model) {
		DateTimeFormatter fmtWorkDate = DateTimeFormatter.ofPattern(DateFormatConstant.DATETIME_FORMAT_YYYY_MM_DD);
		LocalDate workDate = LocalDate.now();
		String today = workDate.format(fmtWorkDate);

		// 勤怠ステータスを確認
		userService.clockStatusCheck(userId, today, model);
		return ViewNameConstant.HOME_VIEW;
	}

	/**
	 * 新しいパスワードを再設定するメソッド
	 * 
	 * @param id メールアドレス
	 * @param password パスワード
	 * @param repassword 再入力パスワード
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.SET_FORGET_PASSWORD_PATH)
	public String setForgetPassWord(@RequestParam String id, @RequestParam String password,
			@RequestParam String repassword, Model model) {
		String passEncode = null;
		String idDecode = null;
		NewUidForm newUidForm = new NewUidForm();

		if(password.equals(repassword)){
			if(password == null || password.isEmpty() || repassword == null || repassword.isEmpty()){
				newUidForm.setForgetPassErrorFlg(true);
				newUidForm.setEncodeEmail(id);
				newUidForm.setForgetPassErrorMessage(messageUtil.getErrorMessage(EMK027));
				model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
				return ViewNameConstant.REPASS_VIEW;
			} else {
				// passwordをエンコード
				passEncode = passwordEncoder.encode(password);
				// idをデコード
				try {
					idDecode = base64Util.base64Decode(id);
				} catch (Exception e) {
					e.printStackTrace();
					// TODO:エラーを表示
					return "error";
				}

				// トークン情報をDBへ保存
				if(!userService.overridePassword(idDecode, passEncode)){
					// TODO:エラーを表示
					return "error";
				}

				// TODO formにuseridを入れる
				model.addAttribute("userId", id);
				// TODO homeではなく、登録成功の画面へ飛ばす(flgで文言変更)
				return ViewNameConstant.MAIL_VERIFISATION_SUCCESS_VIEW;
			}
		} else {
			newUidForm.setForgetPassErrorFlg(true);
			newUidForm.setEncodeEmail(id);
			newUidForm.setForgetPassErrorMessage(messageUtil.getErrorMessage(EMK028));
			model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
			return ViewNameConstant.REPASS_VIEW;
		}
	}
}
