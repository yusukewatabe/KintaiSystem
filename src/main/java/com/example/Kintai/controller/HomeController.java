package com.example.Kintai.controller;

import com.example.Kintai.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.commons.validator.routines.EmailValidator;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("status", false);
		// index.htmlに遷移
		return "index";
	}

	@GetMapping("html/newUid")
	public String showNewUidForm(Model model) {
		// newUid.htmlに遷移
		model.addAttribute("errorPass", false);
		return "html/newUid";
	}

	// 新規ユーザー発行のリンクがクリックされた時
	@GetMapping("/sendMailForm")
	public String sendMailForm(Model model) {
		model.addAttribute("transitionLink", "newUid");
		return "mail/sendMail";
	}

	// パスワード忘れのリンクがクリックされた時
	@GetMapping("/forgetPass")
	public String forgetPassForm(Model model) {
		model.addAttribute("transitionLink", "forget");
		return "mail/sendMail";
	}

	@PostMapping("/sendMail")
	public String sendMail(@RequestParam String id, Model model) {
		if (id.isEmpty() || id == null) {
			model.addAttribute("errorId", true);
			model.addAttribute("errorIdMessage", "メールアドレスが入力されていません。");
			return "mail/sendMail";
		} else {
			if (EmailValidator.getInstance().isValid(id)) {
				emailService.sendVerificationEmail(id);
				return "mail/sendMailResult";
			} else {
				model.addAttribute("errorId", true);
				model.addAttribute("errorIdMessage", "メールアドレスの型が一致しません。");
				return "mail/sendMail";
			}
		}
	}

	@PostMapping("/sendMailForget")
	public String sendMailForget(@RequestParam String id, Model model) {
		if (id.isEmpty() || id == null) {
			model.addAttribute("errorId", true);
			model.addAttribute("errorIdMessage", "メールアドレスが入力されていません。");
			return "mail/sendMail";
		} else {
			if (EmailValidator.getInstance().isValid(id)) {
				emailService.forgetPassSendEmail(id);
				return "mail/sendMailResult";
			} else {
				model.addAttribute("errorId", true);
				model.addAttribute("errorIdMessage", "メールアドレスの型が一致しません。");
				return "mail/sendMail";
			}
		}
	}

	@GetMapping("/error")
	public String error() {
		return "error"; // error.html にマッピング
	}

	@GetMapping("/login")
	public String login(@RequestParam String id, @RequestParam String pass, Model model) {

		// idとpassが一致しているか確認
		if (userService.authenticate(id, pass)) {
			model.addAttribute("status", true);
			return "html/home"; // ログイン成功時のリダイレクト
		} else {
			model.addAttribute("status", false);
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", "IDもしくはパスワードが正しくありません"); // エラーメッセージを設定

			// ログイン失敗時の再表示
			return "index";
		}
	}

	@PostMapping("/setForgetPassWord")
	public String setForgetPassWord(@RequestParam String id, @RequestParam String password,
			@RequestParam String repassword) {
		String passEncode = null;
		// passwordエンコード
		Base64.Encoder encoder = Base64.getEncoder();
		passEncode = encoder.encodeToString(password.getBytes(StandardCharsets.UTF_8));
		// デコード
		Base64.Decoder decoder = Base64.getDecoder();
		String idDecode = new String(decoder.decode(id.getBytes(StandardCharsets.UTF_8)));
		// トークン情報をDBへ保存
		userService.overridePassword(idDecode, passEncode);
		return "html/home";
	}
}
