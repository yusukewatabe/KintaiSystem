package com.jp.Kintai.controller;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.jp.Kintai.constant.DateFormatConstant;
import com.jp.Kintai.constant.FormConstant;
import com.jp.Kintai.constant.HomeConstant;
import com.jp.Kintai.constant.MappingPathNameConstant;
import com.jp.Kintai.constant.NewUidConstant;
import com.jp.Kintai.constant.ViewNameConstant;
import com.jp.Kintai.form.IndexForm;
import com.jp.Kintai.form.NewUidForm;
import com.jp.Kintai.form.NewUidViewForm;
import com.jp.Kintai.model.User;
import com.jp.Kintai.repository.UserRepository;
import com.jp.Kintai.util.Base64Util;
import com.jp.Kintai.util.MessageUtil;
import org.springframework.stereotype.Controller;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * 新規ユーザー登録及びパスワード忘れに関するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Controller
@RequestMapping(MappingPathNameConstant.REQUEST_API_AUTH_PATH)
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private Base64Util base64Util;

	/** メッセージID：EMK_001 */
	private static final String EMK001 = "EMK_001";

	/** メッセージID：EMK_002 */
	private static final String EMK002 = "EMK_002";

	/** メッセージID：EMK_003 */
	private static final String EMK003 = "EMK_003";

	/** メッセージID：EMK_004 */
	private static final String EMK004 = "EMK_004";

	/** メッセージID：EMK_005 */
	private static final String EMK005 = "EMK_005";

	/** メッセージID：EMK_006 */
	private static final String EMK006 = "EMK_006";

	/** メッセージID：EMK_007 */
	private static final String EMK007 = "EMK_007";

	/** メッセージID：EMK_008 */
	private static final String EMK008 = "EMK_008";

	/**
	 * ユーザー登録のリンクをクリックした際にメールアドレスが正しい形式か判定、
	 * 正しい際は入力画面へ遷移。不正な場合はエラー画面へ遷移
	 * 
	 * @param token Base64エンコードされたメールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping(MappingPathNameConstant.VERIFY_PATH)
	public String verifyToken(@RequestParam String token, Model model) {
		String decodeString = null;
		boolean validatorCheckFlg = false;
		IndexForm indexForm = new IndexForm();
		NewUidForm newUidForm = new NewUidForm();

		if (!token.isEmpty() && token != null) {
			decodeString = base64Util.base64Decode(token);
			if (EmailValidator.getInstance().isValid(decodeString)) {
				validatorCheckFlg = true;
			} else {
				validatorCheckFlg = false;
			}

			// emailのバリデーションチェックがOKかNGか
			if (validatorCheckFlg) {
				newUidForm.setEmail(decodeString);
				newUidForm.setAuthFlg(false);
				model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
				model.addAttribute(HomeConstant.MONTH_VIEW, true);
				model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
				return ViewNameConstant.NEWUID_VIEW;
			} else {
				newUidForm.setEmail(decodeString);
				indexForm.setTransitionLink(NewUidConstant.TRANSITIONLINK_NEWUID);
				model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
				model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
				model.addAttribute(HomeConstant.MONTH_VIEW, true);
				model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
				return ViewNameConstant.MAIL_VERIFISATION_FAILED_VIEW;
			}

		} else {
			model.addAttribute(HomeConstant.MONTH_VIEW, true);
			model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
			return ViewNameConstant.MAIL_VERIFISATION_FAILED_VIEW;
		}
	}

	/**
	 * パスワード忘れのリンクをクリックした際にメールアドレスが正しい形式か判定し、
	 * 正しい際は入力画面へ遷移。不正な場合はエラー画面へ遷移
	 * 
	 * @param token Base64エンコードされたメールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping(MappingPathNameConstant.VERIFY_PASSWORD_PATH)
	public String verifyTokenRepass(@RequestParam String token, Model model) {
		String decodeString = null;
		boolean validatorCheckFlg = false;
		IndexForm indexForm = new IndexForm();
		NewUidForm newUidForm = new NewUidForm();

		if (!token.isEmpty() && token != null) {
			// Base64デコード
			decodeString = base64Util.base64Decode(token);
			if (EmailValidator.getInstance().isValid(decodeString)) {
				validatorCheckFlg = true;
			} else {
				validatorCheckFlg = false;
			}

			// emailのバリデーションチェックがOKかNGか
			if (validatorCheckFlg) {
				newUidForm.setEncodeEmail(token);
				model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
				model.addAttribute(HomeConstant.MONTH_VIEW, true);
				model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
				return ViewNameConstant.REPASS_VIEW;
			} else {
				indexForm.setTransitionLink(NewUidConstant.TRANSITIONLINK_FORGET);
				model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
				model.addAttribute(HomeConstant.MONTH_VIEW, true);
				model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
				return ViewNameConstant.MAIL_VERIFISATION_FAILED_VIEW;
			}

		} else {
			model.addAttribute(HomeConstant.MONTH_VIEW, true);
			model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
			return ViewNameConstant.MAIL_VERIFISATION_FAILED_VIEW;
		}
	}

	/**
	 * newUid.htmlにて入力された内容を入力チェックし、
	 * 正しい場合次の画面へ遷移、入力チェックに失敗した場合再度newUidへ遷移
	 * 
	 * @param id        メールアドレス
	 * @param pass      パスワード
	 * @param firstName 名字
	 * @param lastName  名前
	 * @param repass    再入力パスワード
	 * @param email     リンク内のパラメーター
	 * @param model     Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.REGISTER_USER_PATH)
	public String registerUser(@RequestParam String id, @RequestParam String pass, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String repass,
			@RequestParam String email, Model model) {

		boolean checkPass = false;
		boolean checkRepass = false;
		NewUidForm newUidForm = new NewUidForm();
		NewUidViewForm newUidViewForm = new NewUidViewForm();

		// 引数が空かnullか判定
		if (id.isEmpty() || id == null) {
			newUidForm.setErrorEmailFlg(true);
			newUidForm.setErrorEmailMessage(messageUtil.getErrorMessage(EMK001));
		}
		if (pass.isEmpty() || pass == null) {
			checkPass = true;
			newUidForm.setErrorPassFlg(true);
			newUidForm.setErrorPassMessage(messageUtil.getErrorMessage(EMK002));
		}
		if (repass.isEmpty() || repass == null) {
			checkRepass = true;
			newUidForm.setErrorRePassFlg(true);
			newUidForm.setErrorRePassMessage(messageUtil.getErrorMessage(EMK003));
		}
		if (firstName.isEmpty() || firstName == null) {
			newUidForm.setErrorFirstNameFlg(true);
			newUidForm.setErrorFirstNameMessage(messageUtil.getErrorMessage(EMK004));
		}
		if (lastName.isEmpty() || lastName == null) {
			newUidForm.setErrorLastNameFlg(true);
			newUidForm.setErrorLastNameMessage(messageUtil.getErrorMessage(EMK005));
		}
		if (!id.equals(email)) {
			newUidForm.setErrorEmailFlg(true);
			newUidForm.setErrorEmailMessage(messageUtil.getErrorMessage(EMK006));
			newUidForm.setAuthFlg(true);
			newUidViewForm.setInputEmail(id);
			newUidViewForm.setInputFirstName(firstName);
			newUidViewForm.setInputLastName(lastName);
			model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
			model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDVIEWFORM, newUidViewForm);
			model.addAttribute(HomeConstant.MONTH_VIEW, true);
			model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
			return ViewNameConstant.NEWUID_VIEW;
		}
		if (userRepository.findById(id).isPresent()) {
			newUidForm.setErrorEmailFlg(true);
			newUidForm.setErrorEmailMessage(messageUtil.getErrorMessage(EMK007));
			newUidForm.setAuthFlg(true);
			newUidViewForm.setInputEmail(id);
			newUidViewForm.setInputFirstName(firstName);
			newUidViewForm.setInputLastName(lastName);
			model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
			model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDVIEWFORM, newUidViewForm);
			model.addAttribute(HomeConstant.MONTH_VIEW, true);
			model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
			return ViewNameConstant.NEWUID_VIEW;
		}

		if (pass.equals(repass) && checkPass == false && checkRepass == false) {
			String idEncode = null;
			String passEncode = null;
			String firstNameEncode = null;
			String lastNameEncode = null;
			// BCryptPasswordEncoderにてパスワードをエンコード
			passEncode = passwordEncoder.encode(pass);
			idEncode = base64Util.base64Encode(id);
			firstNameEncode = base64Util.base64Encode(firstName);
			lastNameEncode = base64Util.base64Encode(lastName);

			newUidViewForm.setInputEmail(id);
			newUidViewForm.setInputPass(passEncode);
			newUidViewForm.setInputFirstName(firstName);
			newUidViewForm.setInputLastName(lastName);
			newUidForm.setEncodeId(idEncode);
			newUidForm.setEncodeFirstName(firstNameEncode);
			newUidForm.setEncodeLastName(lastNameEncode);
			model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDVIEWFORM, newUidViewForm);
			model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
			model.addAttribute(HomeConstant.MONTH_VIEW, true);
			model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
			return ViewNameConstant.NEWUID_RESULT_VIEW;
		} else if (checkRepass != true) {
			newUidForm.setErrorPassFlg(true);
			newUidForm.setErrorPassMessage(messageUtil.getErrorMessage(EMK008));
		}
		newUidViewForm.setInputEmail(id);
		newUidViewForm.setInputFirstName(firstName);
		newUidViewForm.setInputLastName(lastName);
		newUidForm.setAuthFlg(true);
		newUidForm.setEmail(email);
		model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
		model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDVIEWFORM, newUidViewForm);
		model.addAttribute(HomeConstant.MONTH_VIEW, true);
		model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
		return ViewNameConstant.NEWUID_VIEW;
	}

	/**
	 * newUidResult.htmlにて確定ボタンが押された際にDBに保存するメソッド
	 * 
	 * @param userId    メールアドレス
	 * @param passWord  パスワード
	 * @param firstName 名字
	 * @param lastName  名前
	 * @param model     Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping(MappingPathNameConstant.SUBMIT_USER_PATH)
	public String submitUser(@RequestParam("nrtfevah") String userId, @RequestParam("okbjrein") String passWord,
			@RequestParam("reabtseg") String firstName,
			@RequestParam("vsvbrebb") String lastName, Model model) {
		// 日本時間を取得
		ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of(DateFormatConstant.TIMEZONE_ASIA_TOKYO));
		Timestamp timestamp = Timestamp.valueOf(tokyoTime.toLocalDateTime());
		IndexForm indexForm = new IndexForm();

		String idDecode = base64Util.base64Decode(userId);
		String firstNameDecode = base64Util.base64Decode(firstName);
		String lastNameDecode = base64Util.base64Decode(lastName);

		// 入力情報をDBへ保存
		User user = new User();
		user.setId(idDecode);
		user.setPassword(passWord);
		user.setFirstname(firstNameDecode);
		user.setLastname(lastNameDecode);
		user.setCreatedate(timestamp);
		user.setLastlogin(timestamp);
		userRepository.save(user);
		indexForm.setEmail(idDecode);
		indexForm.setTransitionLink(NewUidConstant.TRANSITIONLINK_NEWUID);
		model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
		model.addAttribute(HomeConstant.MONTH_VIEW, true);
		model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
		return ViewNameConstant.SUCCESS_PATH;
	}

	/**
	 * newUidResult.htmlで修正ボタンが押された場合newUid.htmlに必要な情報を渡すメソッド
	 * 
	 * @param id        メールアドレス
	 * @param firstName 名字
	 * @param lastName  名前
	 * @param model     Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping(MappingPathNameConstant.REGISTER_USER_FIXES_PATH)
	public String registerUserFixes(@RequestParam("nrtfevah") String userId, @RequestParam("reabtseg") String firstName,
			@RequestParam("vsvbrebb") String lastName, Model model) {

		NewUidForm newUidForm = new NewUidForm();
		NewUidViewForm newUidViewForm = new NewUidViewForm();

		String idDecode = base64Util.base64Decode(userId);
		String firstNameDecode = base64Util.base64Decode(firstName);
		String lastNameDecode = base64Util.base64Decode(lastName);

		newUidViewForm.setInputEmail(idDecode);
		newUidViewForm.setInputFirstName(firstNameDecode);
		newUidViewForm.setInputLastName(lastNameDecode);
		newUidForm.setAuthFlg(true);
		newUidForm.setEmail(idDecode);
		model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDFORM, newUidForm);
		model.addAttribute(FormConstant.ATTRIBUTE_NEWUIDVIEWFORM, newUidViewForm);
		model.addAttribute(HomeConstant.MONTH_VIEW, true);
		model.addAttribute(HomeConstant.LOGOUT_VIEW, true);
		return ViewNameConstant.NEWUID_VIEW;
	}
}