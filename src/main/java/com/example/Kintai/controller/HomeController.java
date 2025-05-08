package com.example.Kintai.controller;

import com.example.Kintai.form.HomeForm;
import com.example.Kintai.model.User;
import com.example.Kintai.repository.UserRepository;
import com.example.Kintai.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Home.htmlからのリクエストを管理するクラスです。
 * 
 * @author Watabe Yusuke
 * @version 0.1
 **/
@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
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

	/**
	 * 新規ユーザー発行のリンクがクリックされた時
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/sendMailForm")
	public String sendMailForm(Model model) {
		model.addAttribute("transitionLink", "newUid");
		return "mail/sendMail";
	}

	/**
	 * パスワード忘れのリンクがクリックされた時
	 * 
	 * @param model
	 * @return
	 */
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

	// @PostMapping("/error")
	// public String error() {
	// return "error"; // error.html にマッピング
	// }

	/**
	 * index画面からid,passが正しいかチェック
	 * 
	 * @param id
	 * @param pass
	 * @param model
	 * @return
	 */
	@PostMapping("/login")
	public String login(@RequestParam String id, @RequestParam String pass, Model model) {

		// idとpassが一致しているか確認
		if (userService.authenticate(id, pass)) {
			model.addAttribute("status", true);
			// 日本時間を取得
			ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
			Timestamp timestamp = Timestamp.valueOf(tokyoTime.toLocalDateTime());
			// dbに最終ログイン時刻を保存
			User user = userRepository.findById(id).orElseThrow();
			user.setLastlogin(timestamp);
			userRepository.save(user);
			HomeForm homeForm = new HomeForm();
			homeForm.setEmail(id);
			model.addAttribute("homeForm", homeForm);
			model.addAttribute("userId", id);
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
			@RequestParam String repassword, Model model) {
		String passEncode = null;
		// passwordエンコード
		Base64.Encoder encoder = Base64.getEncoder();
		passEncode = encoder.encodeToString(password.getBytes(StandardCharsets.UTF_8));
		// デコード
		Base64.Decoder decoder = Base64.getDecoder();
		String idDecode = new String(decoder.decode(id.getBytes(StandardCharsets.UTF_8)));
		// トークン情報をDBへ保存
		userService.overridePassword(idDecode, passEncode);
		model.addAttribute("userId", id);
		return "html/home";
	}
}
