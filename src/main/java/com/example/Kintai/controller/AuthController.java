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

@Controller
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	/**
	 * 新規ユーザー登録のコントローラー
	 * 
	 * @param token
	 * @param model
	 * @return
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
	 * パスワード忘れのコントローラー
	 * 
	 * @param token
	 * @param model
	 * @return
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
	 * newUid画面の入力チェックのコントローラー
	 * 
	 * @param token
	 * @param model
	 * @return
	 */
	@PostMapping("/registerUser")
	public String registerUser(@RequestParam String id, @RequestParam String pass, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String repass, @RequestParam String authInfo,
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
	 * 新規ユーザーのDB登録用コントローラー
	 * 
	 * @param token
	 * @param model
	 * @return
	 */
	@GetMapping("/submitUser")
	public String submitUser(@RequestParam String nrtfevah, @RequestParam String okbjrein,
			@RequestParam String reabtseg,
			@RequestParam String vsvbrebb) {
		// 日本時間を取得
		ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
		Timestamp timestamp = Timestamp.valueOf(tokyoTime.toLocalDateTime());
		// 入力情報をDBへ保存
		User user = new User();
		user.setId(nrtfevah);
		user.setPassword(okbjrein);
		user.setFirstname(reabtseg);
		user.setLastname(vsvbrebb);
		user.setCreatedate(timestamp);
		userRepository.save(user);
		// TODO:homeではなくユーザー登録成功画面へ遷移に修正
		return "html/home";
	}

	@GetMapping("/registerUserFixes")
	public String submitUser(@RequestParam String nrtfevah, @RequestParam String reabtseg,
			@RequestParam String vsvbrebb, Model model) {

		model.addAttribute("id", nrtfevah);
		model.addAttribute("firstName", reabtseg);
		model.addAttribute("lastName", vsvbrebb);
		model.addAttribute("auth", "fixes");
		return "html/newUid";
	}
}