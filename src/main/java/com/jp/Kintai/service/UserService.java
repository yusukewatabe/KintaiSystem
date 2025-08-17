package com.jp.Kintai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.jp.Kintai.constant.FormConstant;
import com.jp.Kintai.constant.HomeConstant;
import com.jp.Kintai.form.HomeForm;
import com.jp.Kintai.form.IndexForm;
import com.jp.Kintai.model.Attendance;
import com.jp.Kintai.model.User;
import com.jp.Kintai.repository.AttendanceRepository;
import com.jp.Kintai.repository.UserRepository;

import java.util.Optional;

/**
 * パスワードのエンコード、デコードに関するビジネスロジックが記載されているクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * パスワードがdbの値と一致するか判定するメソッド
	 * @param id ユーザーID
	 * @param pass パスワード
	 * @return true or false
	 */
	public boolean authenticate(String id, String pass) {
		Optional<User> user = userRepository.findById(id);
		boolean passCheck = false;
		if(user.isPresent()){
			String storedHash = user.get().getPassword();
			if(passwordEncoder.matches(pass, storedHash)){
				passCheck = true;
			} else {
				passCheck = false;
			}
		} else {
			passCheck = false;
		}

		return passCheck;
	}

	/**
	 * パスワード忘れの際にパスワードを上書きするメソッド
	 * @param id ユーザーID
	 * @param pass パスワード
	 * @return
	 */
	public boolean overridePassword(String id, String pass) {
		Optional<User> optionalUser = userRepository.findById(id);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			// トークン情報をDBへ保存
			user.setPassword(pass);
			userRepository.save(user);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 勤務状況を確認し、formにステータスをセットするメソッド
	 * 
	 * @param id メールアドレス
	 * @param today 現在日付
	 * @param model Spring MVC のモデルオブジェクト
	 */
	public void clockStatusCheck(String id, String today, Model model) {
		Optional<Attendance> optAtt = attendanceRepository.findByUser_IdAndWorkDate(id, today);

		HomeForm homeForm = new HomeForm();
		IndexForm indexForm = new IndexForm();
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
			indexForm.setEmail(id);
		}
		model.addAttribute(FormConstant.ATTRIBUTE_HOMEFORM, homeForm);
	}
}