package com.jp.Kintai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.jp.Kintai.constant.DateFormatConstant;
import com.jp.Kintai.constant.FormConstant;
import com.jp.Kintai.constant.HomeConstant;
import com.jp.Kintai.constant.MappingPathNameConstant;
import com.jp.Kintai.constant.NewUidConstant;
import com.jp.Kintai.constant.ViewNameConstant;
import com.jp.Kintai.form.NewUidForm;
import com.jp.Kintai.form.IndexForm;
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

	/** メッセージID：EMK_029 */
	private static final String EMK029 = "EMK_029";

	/**
	 * index以外からhomeへ遷移が行われた際に使用されるメソッド
	 * 
	 * @param id    メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.HOME_PATH)
	public String backHome(String userId, Model model) {
		DateTimeFormatter fmtWorkDate = DateTimeFormatter.ofPattern(DateFormatConstant.DATETIME_FORMAT_YYYY_MM_DD);
		LocalDate workDate = LocalDate.now();
		String today = workDate.format(fmtWorkDate);
		IndexForm indexForm = new IndexForm();
		indexForm.setEmail(userId);

		// 勤怠ステータスを確認
		userService.clockStatusCheck(userId, today, model);
		model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
		return ViewNameConstant.HOME_VIEW;
	}

	/**
	 * 新しいパスワードを再設定するメソッド
	 * 
	 * @param id         メールアドレス
	 * @param password   パスワード
	 * @param repassword 再入力パスワード
	 * @param model      Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.SET_FORGET_PASSWORD_PATH)
	public String setForgetPassWord(@RequestParam String id, @RequestParam String password,
			@RequestParam String repassword, Model model) {
		String passEncode = null;
		String idDecode = null;
		NewUidForm newUidForm = new NewUidForm();
		IndexForm indexForm = new IndexForm();

		if (password.equals(repassword)) {
			if (password == null || password.isEmpty() || repassword == null || repassword.isEmpty()) {
				newUidForm.setForgetPassErrorFlg(true);
				newUidForm.setEncodeEmail(id);
				newUidForm.setForgetPassErrorMessage(messageUtil.getErrorMessage(EMK027));
				model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
				model.addAttribute(HomeConstant.MONTH_VIEW, true);
				model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
				return ViewNameConstant.REPASS_VIEW;
			} else {
				// passwordをエンコード
				passEncode = passwordEncoder.encode(password);

				// idをデコード
				idDecode = base64Util.base64Decode(id);

				// トークン情報をDBへ保存
				if (!userService.overridePassword(idDecode, passEncode)) {
					// パスワードの上書きに失敗した場合
					newUidForm.setForgetPassErrorFlg(true);
					newUidForm.setEncodeEmail(id);
					newUidForm.setForgetPassErrorMessage(messageUtil.getErrorMessage(EMK029));
					model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
					model.addAttribute(HomeConstant.MONTH_VIEW, true);
					model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
					return ViewNameConstant.REPASS_VIEW;
				}

				indexForm.setEmail(id);
				indexForm.setTransitionLink(NewUidConstant.TRANSITIONLINK_FORGET);
				model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
				model.addAttribute(HomeConstant.MONTH_VIEW, true);
				model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
				return ViewNameConstant.SUCCESS_PATH;
			}
		} else {
			newUidForm.setForgetPassErrorFlg(true);
			newUidForm.setEncodeEmail(id);
			newUidForm.setForgetPassErrorMessage(messageUtil.getErrorMessage(EMK028));
			model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
			model.addAttribute(HomeConstant.MONTH_VIEW, true);
			model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
			return ViewNameConstant.REPASS_VIEW;
		}
	}
}
