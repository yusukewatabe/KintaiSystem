package com.example.Kintai.controller;

import com.example.Kintai.constant.HomeConstant;
import com.example.Kintai.form.HomeForm;
import com.example.Kintai.model.Attendance;
import com.example.Kintai.model.User;
import com.example.Kintai.repository.AttendanceRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.Optional;
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
	private UserRepository userRepository;
	@Autowired
	private AttendanceRepository attendanceRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;

	/**
	 * 初期画面を表示するメソッド
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("status", false);
		// index.htmlに遷移
		return "index";
	}

	/**
	 * 新規ユーザー発行のリンクがクリックされた処理をするメソッド
	 * 
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping("/sendMailForm")
	public String sendMailForm(Model model) {
		model.addAttribute("transitionLink", "newUid");
		return "mail/sendMail";
	}

	/**
	 * パスワード忘れのリンクがクリックされた処理をするメソッド
	 * 
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping("/forgetPass")
	public String forgetPassForm(Model model) {
		model.addAttribute("transitionLink", "forget");
		return "mail/sendMail";
	}

	/**
	 * メールアドレスが正しいか判定し、該当メールアドレスに新規ユーザー登録のメールを送信するメソッド
	 * 
	 * @param id メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
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

	/**
	 * メールアドレスが正しいか判定し、該当メールアドレスにパスワード忘れのメールを送信するメソッド
	 * 
	 * @param id メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
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

	/**
	 * id,passが正しいかdbへ参照し、正しいid,passか判定するメソッド
	 * 
	 * @param id メールアドレス
	 * @param pass パスワード
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping("/login")
	public String login(@RequestParam String id, @RequestParam String pass, Model model) {
		// 日本時間を取得
		ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
		Timestamp timestamp = Timestamp.valueOf(tokyoTime.toLocalDateTime());
		DateTimeFormatter fmtWorkDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate workDate = LocalDate.now();
		String today = workDate.format(fmtWorkDate);

		HomeForm homeForm = new HomeForm();

		// idとpassが一致しているか確認
		if (userService.authenticate(id, pass)) {
			setClockStatus(id, today, model);
			// dbに最終ログイン時刻を保存
			User user = userRepository.findById(id).orElseThrow();
			user.setLastlogin(timestamp);
			userRepository.save(user);
			// emailをhidden項目にセット
			homeForm.setEmail(id);
			// html側に値を渡す
			model.addAttribute("homeForm", homeForm);
			return "html/home"; // ログイン成功時のリダイレクト
		} else {
			model.addAttribute("status", false);
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", "IDもしくはパスワードが正しくありません"); // エラーメッセージを設定

			// ログイン失敗時の再表示
			return "index";
		}
	}

	/**
	 * index以外からhomeへ遷移が行われた際に使用されるメソッド
	 * 
	 * @param id メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping("/home")
	public String backHome(String userId, Model model) {
		DateTimeFormatter fmtWorkDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate workDate = LocalDate.now();
		String today = workDate.format(fmtWorkDate);

		// 勤怠ステータスを確認
		setClockStatus(userId, today, model);
		return "html/home";
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

	/**
	 * 勤務状況を確認し、formにステータスをセットするメソッド
	 * 
	 * @param id メールアドレス
	 * @param today 現在日付
	 * @param model Spring MVC のモデルオブジェクト
	 */
	private void setClockStatus(String id, String today, Model model) {
		Optional<Attendance> optAtt = attendanceRepository.findByUser_IdAndWorkDate(id, today);

		HomeForm homeForm = new HomeForm();
		// TODO:ステータスの有無検討
		model.addAttribute("status", true);
		// レコードの有無確認
		if (optAtt.isPresent()) {
			Attendance attendance = optAtt.get();
			// 出勤済みかどうか
			if (attendance.getClockInTime() != null) {
				homeForm.setClockStatus(HomeConstant.CLOCK_IN);
			}
			if (attendance.getClockOutTime() != null) {
				homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
			}
			if (attendance.getBreakStart() != null && attendance.getBreakEnd() == null) {
				homeForm.setClockStatus(HomeConstant.BREAK_START);
			}
			if (attendance.getClockOutTime() == null && attendance.getBreakEnd() != null) {
				if (attendance.getClockInTime() != null) {
					homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				} else {
					homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
				}
			}
		} else {
			homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
			homeForm.setEmail(id);
		}
		model.addAttribute("homeForm", homeForm);
	}
}
