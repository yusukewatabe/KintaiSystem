package com.example.Kintai.controller;

import com.example.Kintai.model.User;
import com.example.Kintai.repository.UserRepository;
import java.nio.charset.StandardCharsets;
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
				model.addAttribute("auth", "initial");
				return "html/newUid.html";
			} else {
				model.addAttribute("transitionLink", "newUid");
				return "mail/verificationFailed";
			}

		} else {
			return "mail/verificationFailed";
		}
	}

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

	@PostMapping("/registerUser")
	public String registerUser(@RequestParam String id, @RequestParam String pass, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String repass, @RequestParam String authInfo, Model model) {

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
		return "html/newUid";
	}

	@GetMapping("/submitUser")
	public String submitUser(@RequestParam String nrtfevah, @RequestParam String okbjrein,
			@RequestParam String reabtseg,
			@RequestParam String vsvbrebb) {
		// 入力情報をDBへ保存
		User user = new User();
		user.setId(nrtfevah);
		user.setPassword(okbjrein);
		user.setFirstname(reabtseg);
		user.setLastname(vsvbrebb);
		userRepository.save(user);
		// TODO 登録完了画面を表示
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