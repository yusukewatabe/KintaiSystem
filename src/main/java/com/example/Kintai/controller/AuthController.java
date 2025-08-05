package com.example.Kintai.controller;

import com.example.Kintai.model.User;
import com.example.Kintai.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import org.springframework.stereotype.Controller;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * 新規ユーザー登録及びパスワード忘れに関するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Controller
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	/**
	 * ユーザー登録のリンクをクリックした際にメールアドレスが正しい形式か判定、
	 * 正しい際は入力画面へ遷移。不正な場合はエラー画面へ遷移
	 * 
	 * @param token Base64エンコードされたメールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping("/verify")
	public String verifyToken(@RequestParam String token, Model model) {
		String decoded = null;
		boolean validatorCheckFlg = false;

		if (!token.isEmpty() && token != null) {
			try {
				// Base64デコード
				Base64.Decoder decoder = Base64.getDecoder();
				decoded = new String(decoder.decode(token.getBytes(StandardCharsets.UTF_8)));
				if (EmailValidator.getInstance().isValid(decoded)) {
					validatorCheckFlg = true;
				} else {
					validatorCheckFlg = false;
				}
			} catch (Exception e) {
				validatorCheckFlg = false;
				e.printStackTrace();
			}

			// emailのバリデーションチェックがOKかNGか
			if (validatorCheckFlg) {
				model.addAttribute("email", decoded);
				model.addAttribute("auth", "initial");
				return "html/newUid.html";
			} else {
				model.addAttribute("email", decoded);
				model.addAttribute("transitionLink", "newUid");
				return "mail/verificationFailed";
			}

		} else {
			return "mail/verificationFailed";
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
	@GetMapping("/verify/password")
	public String verifyTokenRepass(@RequestParam String token, Model model) {
		String decoded = null;
		boolean validatorCheckFlg = false;

		if (!token.isEmpty() && token != null) {
			try {
				// Base64デコード
				Base64.Decoder decoder = Base64.getDecoder();
				decoded = new String(decoder.decode(token.getBytes(StandardCharsets.UTF_8)));
				if (EmailValidator.getInstance().isValid(decoded)) {
					validatorCheckFlg = true;
				} else {
					validatorCheckFlg = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				validatorCheckFlg = false;
			}

			// emailのバリデーションチェックがOKかNGか
			if (validatorCheckFlg) {
				model.addAttribute("id", token);
				return "html/rePass.html";
			} else {
				model.addAttribute("transitionLink", "forget");
				return "mail/verificationFailed";
			}

		} else {
			return "mail/verificationFailed";
		}
	}

	/**
	 * newUid.htmlにて入力された内容を入力チェックし、
	 * 正しい場合次の画面へ遷移、入力チェックに失敗した場合再度newUidへ遷移
	 * 
	 * @param id メールアドレス
	 * @param pass パスワード
	 * @param firstName 名字
	 * @param lastName 名前
	 * @param repass 再入力パスワード
	 * @param email リンク内のパラメーター
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping("/registerUser")
	public String registerUser(@RequestParam String id, @RequestParam String pass, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String repass,
			@RequestParam String email, Model model) {

		boolean checkPass = false;
		boolean checkRepass = false;

		// 引数が空かnullか判定
		if (id.isEmpty() || id == null) {
			model.addAttribute("errorId", true);
			model.addAttribute("errorIdMessage", "メールアドレスが入力されていません。");
		}
		if (pass.isEmpty() || pass == null) {
			checkPass = true;
			model.addAttribute("errorPass", true);
			model.addAttribute("errorPassMessage", "パスワードが入力されていません。");
		}
		if (repass.isEmpty() || repass == null) {
			checkRepass = true;
			model.addAttribute("errorRepass", true);
			model.addAttribute("errorRepassMessage", "パスワード(再)が入力されていません。");
		}
		if (firstName.isEmpty() || firstName == null) {
			model.addAttribute("errorFirstName", true);
			model.addAttribute("errorFirstNameMessage", "名字が入力されていません。");
		}
		if (lastName.isEmpty() || lastName == null) {
			model.addAttribute("errorLastName", true);
			model.addAttribute("errorLastNameMessage", "名前が入力されていません。");
		}
		if (userRepository.findById(id).isPresent()) {
			model.addAttribute("errorId", true);
			model.addAttribute("errorIdMessage", "このメールアドレスは既に登録されています。");
			model.addAttribute("auth", "fixes");
			model.addAttribute("id", id);
			model.addAttribute("firstName", firstName);
			model.addAttribute("lastName", lastName);
			return "html/newUid";
		}
		if (!id.equals(email)) {
			model.addAttribute("errorId", true);
			model.addAttribute("errorIdMessage", "認証されたメールアドレスと誤りがあります。");
		}

		if (pass.equals(repass) && checkPass == false && checkRepass == false) {
			String base64PassEncode = null;
			// エンコード
			Base64.Encoder encoder = Base64.getEncoder();
			base64PassEncode = encoder.encodeToString(pass.getBytes(StandardCharsets.UTF_8));

			model.addAttribute("id", id);
			model.addAttribute("pass", base64PassEncode);
			model.addAttribute("firstName", firstName);
			model.addAttribute("lastName", lastName);
			return "html/newUidResult";
		} else if (checkRepass != true) {
			model.addAttribute("errorPass", true);
			model.addAttribute("errorPassMessage", "パスワードが一致しません。");
		}
		model.addAttribute("id", id);
		model.addAttribute("firstName", firstName);
		model.addAttribute("lastName", lastName);
		model.addAttribute("auth", "fixes");
		model.addAttribute("email", email);
		return "html/newUid";
	}

	/**
	 * newUidResult.htmlにて確定ボタンが押された際にDBに保存するメソッド
	 * 
	 * @param userId メールアドレス
	 * @param passWord パスワード
	 * @param firstName 名字
	 * @param lastName 名前
	 * @return 表示するビュー名
	 */
	@GetMapping("/submitUser")
	public String submitUser(@RequestParam("nrtfevah") String userId, @RequestParam("okbjrein") String passWord,
			@RequestParam("reabtseg") String firstName,
			@RequestParam("vsvbrebb") String lastName) {
		// 日本時間を取得
		ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
		Timestamp timestamp = Timestamp.valueOf(tokyoTime.toLocalDateTime());
		// 入力情報をDBへ保存
		User user = new User();
		user.setId(userId);
		user.setPassword(passWord);
		user.setFirstname(firstName);
		user.setLastname(lastName);
		user.setCreatedate(timestamp);
		user.setLastlogin(timestamp);
		userRepository.save(user);

		return "mail/verificationSuccess";
	}

	/**
	 * newUidResult.htmlで修正ボタンが押された場合newUid.htmlに必要な情報を渡すメソッド
	 * 
	 * @param id メールアドレス
	 * @param firstName 名字
	 * @param lastName 名前
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping("/registerUserFixes")
	public String submitUser(@RequestParam("nrtfevah") String id, @RequestParam("reabtseg") String firstName,
			@RequestParam("vsvbrebb") String lastName, Model model) {

		model.addAttribute("id", id);
		model.addAttribute("firstName", firstName);
		model.addAttribute("lastName", lastName);
		model.addAttribute("auth", "fixes");
		return "html/newUid";
	}
}