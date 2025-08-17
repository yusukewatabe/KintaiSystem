package com.jp.Kintai.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jp.Kintai.constant.DateFormatConstant;
import com.jp.Kintai.constant.FormConstant;
import com.jp.Kintai.constant.MappingPathNameConstant;
import com.jp.Kintai.constant.NewUidConstant;
import com.jp.Kintai.constant.ViewNameConstant;
import com.jp.Kintai.form.IndexForm;
import com.jp.Kintai.model.User;
import com.jp.Kintai.repository.UserRepository;
import com.jp.Kintai.service.UserService;
import com.jp.Kintai.util.MessageUtil;

/**
 * index.htmlからのリクエストを処理するクラスです。
 * 
 * @author Watabe Yusuke
 * @version 0.1
 **/
@Controller
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private MessageUtil messageUtil;

	/**  メッセージID：EMK_009 */
	private static final String EMK009 = "EMK_009";

	/**
	 * 初期画面を表示するメソッド
	 * 
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping("/")
	public String index(Model model) {
		IndexForm indexForm = new IndexForm();

		indexForm.setErrorFlg(false);
		// TODO:ステータス使用有無
		model.addAttribute("status", false);
		// index.htmlに遷移
		model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
		return ViewNameConstant.INDEX_VIEW;
	}

	/**
	 * 新規ユーザー発行のリンクがクリックされた処理をするメソッド
	 * 
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping(MappingPathNameConstant.NEWUID_PATH)
	public String sendMailForm(Model model) {
		IndexForm indexForm = new IndexForm();
		indexForm.setSendMailErrorFlg(false);
		indexForm.setTransitionLink(NewUidConstant.TRANSITIONLINK_NEWUID);
		model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
		return ViewNameConstant.MAIL_SENDMAIL_VIEW;
	}

	/**
	 * パスワード忘れのリンクがクリックされた処理をするメソッド
	 * 
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@GetMapping(MappingPathNameConstant.FORGET_PATH)
	public String forgetPassForm(Model model) {
		IndexForm indexForm = new IndexForm();
		indexForm.setSendMailErrorFlg(false);
		indexForm.setTransitionLink(NewUidConstant.TRANSITIONLINK_FORGET);
		model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
		return ViewNameConstant.MAIL_SENDMAIL_VIEW;
	}

	/**
	 * id,passが正しいかdbへ参照し、正しいid,passか判定するメソッド
	 * 
	 * @param id メールアドレス
	 * @param pass パスワード
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.LOGIN_PATH)
	public String login(@RequestParam String id, @RequestParam String pass, Model model) {
		// 日本時間を取得
		ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of(DateFormatConstant.TIMEZONE_ASIA_TOKYO));
		Timestamp timestamp = Timestamp.valueOf(tokyoTime.toLocalDateTime());
		DateTimeFormatter fmtWorkDate = DateTimeFormatter.ofPattern(DateFormatConstant.DATETIME_FORMAT_YYYY_MM_DD);
		LocalDate workDate = LocalDate.now();
		String today = workDate.format(fmtWorkDate);
		IndexForm indexForm = new IndexForm();

		// idとpassが一致しているか確認
		if (userService.authenticate(id, pass)) {
			userService.clockStatusCheck(id, today, model);
			// dbに最終ログイン時刻を保存
			User user = userRepository.findById(id).orElseThrow();
			user.setLastlogin(timestamp);
			userRepository.save(user);
			// emailをhidden項目にセット
			indexForm.setEmail(id);
			// html側に値を渡す
			model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
			return ViewNameConstant.HOME_VIEW; // ログイン成功時のリダイレクト
		} else {
			// TODO ステータス使用検討
			model.addAttribute("status", false);
			indexForm.setErrorFlg(true);
			indexForm.setErrorMessage(messageUtil.getErrorMessage(EMK009));

			model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
			// ログイン失敗時の再表示
			return ViewNameConstant.INDEX_VIEW;
		}
	}

	/**
	 * ログイン画面へ戻る際に使用するメソッド
	 * 
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.BACK_INDEX_PATH)
	public String backIndex(Model model) {
		IndexForm indexForm = new IndexForm();
		indexForm.setErrorFlg(false);
		model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
		return ViewNameConstant.INDEX_VIEW;
	}
}
