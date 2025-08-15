package com.example.Kintai.service;

import com.example.Kintai.constant.FormConstant;
import com.example.Kintai.constant.HomeConstant;
import com.example.Kintai.form.HomeForm;
import com.example.Kintai.form.IndexForm;
import com.example.Kintai.model.Attendance;
import com.example.Kintai.model.User;
import com.example.Kintai.repository.AttendanceRepository;
import com.example.Kintai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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

	/**
	 * パスワードがdbの値と一致するか判定するメソッド
	 * @param id ユーザーID
	 * @param pass パスワード
	 * @return true or false
	 */
	public boolean authenticate(String id, String pass) {
		Optional<User> user = userRepository.findById(id);

		String decoded = null;
		// Base64デコード
		Base64.Encoder encoder = Base64.getEncoder();
		decoded = encoder.encodeToString(pass.getBytes(StandardCharsets.UTF_8));
		return user.isPresent() && user.get().getPassword().equals(decoded);
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