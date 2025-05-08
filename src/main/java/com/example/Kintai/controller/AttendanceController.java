package com.example.Kintai.controller;

import com.example.Kintai.model.Attendance;
import com.example.Kintai.model.User;
import com.example.Kintai.form.HomeForm;
import com.example.Kintai.constant.HomeConstant;
import com.example.Kintai.repository.AttendanceRepository;
import com.example.Kintai.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
// import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

	private final AttendanceRepository attendanceRepository;
	private final UserRepository userRepository;

	/**
	 * Home画面から押されたボタンを判定し、dbに時間を登録するメソッド
	 * 
	 * @param userId
	 * @param action
	 * @param model
	 * @param request
	 * @return
	 */
	@PostMapping("/attendance/clock")
	public String handleClockAction(@RequestParam String userId, @RequestParam String action,
			Model model, HttpServletRequest request) {
		// 初期値設定
		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();
		Attendance attendance;
		Date date = new Date();
		HomeForm homeForm = new HomeForm();
		// 日付のフォーマット指定
		SimpleDateFormat nowToday = new SimpleDateFormat("MM/dd HH:mm");
		// emailをhidden項目にセット
		homeForm.setEmail(userId);

		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			model.addAttribute("error", "ユーザーが見つかりません");
			return "html/home";
		}

		User user = userOpt.get();

		Optional<Attendance> attendanceOpt = attendanceRepository.findByUser_IdAndWorkDate(userId, today);

		if (attendanceOpt.isEmpty()) {
			if (!HomeConstant.CLOCK_IN.equals(action)) {
				model.addAttribute("clockStatusMessage", "初回は出勤ボタンを押してください");
				return "html/home";
			}
			attendance = new Attendance();
			attendance.setUser(user);
			attendance.setWorkDate(today);
			attendance.setClockInTime(Timestamp.valueOf(now));
			attendanceRepository.save(attendance);
			homeForm.setClockStatus(HomeConstant.CLOCK_IN);
			model.addAttribute("clockStatusMessage", nowToday.format(date) + "に出勤しました。");
			model.addAttribute("homeForm", homeForm);
			return "html/home";
		} else {
			attendance = attendanceOpt.get();
			if (HomeConstant.CLOCK_IN.equals(action)) {
				attendance.setClockInTime(Timestamp.valueOf(now));
				homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				model.addAttribute("clockStatusMessage", nowToday.format(date) + "に出勤しました。");
			} else if (HomeConstant.CLOCK_OUT.equals(action)) {
				attendance.setClockOutTime(Timestamp.valueOf(now));
				homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
				model.addAttribute("clockStatusMessage", nowToday.format(date) + "に退勤しました。");
			} else if (HomeConstant.BREAK_START.equals(action)) {
				attendance.setBreakStart(Timestamp.valueOf(now));
				homeForm.setClockStatus(HomeConstant.BREAK_START);
				model.addAttribute("clockStatusMessage", nowToday.format(date) + "に休憩開始しました。");
			} else if (HomeConstant.BREAK_END.equals(action)) {
				attendance.setBreakEnd(Timestamp.valueOf(now));
				homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				model.addAttribute("clockStatusMessage", nowToday.format(date) + "に休憩終了しました。");
			} else {
				model.addAttribute("clockStatus", HomeConstant.ERROR);
				model.addAttribute("clockStatusMessage", "不明なアクションです");
				model.addAttribute("homeForm", homeForm);
				return "html/home";
			}
			model.addAttribute("homeForm", homeForm);
			attendanceRepository.save(attendance);
			return "html/home";
		}
	}
}
