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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * bulkEdit.htmlで入力された時間をDBに保存するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Controller
@RequiredArgsConstructor
public class AttendanceController {

	private final AttendanceRepository attendanceRepository;
	private final UserRepository userRepository;

	/**
	 * Home画面から押されたボタンを判定し、dbに時間を登録するメソッド
	 * 
	 * @param userId ユーザーID
	 * @param action 勤怠ステータス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping("/attendance/clock")
	public String handleClockAction(@RequestParam String userId, @RequestParam String action,
			Model model) {
		// workDay初期値設定
		DateTimeFormatter fmtWorkDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate workDate = LocalDate.now();
		String today = workDate.format(fmtWorkDate);

		// 出勤、退勤時間等の初期値設定
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
		String currentTime = fmt.format(now);

		Attendance attendance;

		// 出勤、退勤時間等の初期値設定
		Date date = new Date();
		HomeForm homeForm = new HomeForm();
		// 画面に出力するための日付のフォーマット指定
		SimpleDateFormat nowToday = new SimpleDateFormat("MM/dd HH:mm");
		// emailをhidden項目にセット
		homeForm.setEmail(userId);

		// userテーブルからuserIdを検索
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			model.addAttribute("error", "ユーザーが見つかりません");
			return "html/home";
		}

		User user = userOpt.get();

		Optional<Attendance> attendanceOpt = attendanceRepository.findByUser_IdAndWorkDate(userId, today);

		if (attendanceOpt.isEmpty()) {
			if (!HomeConstant.CLOCK_IN.equals(action)) {
				homeForm.setClockStatusMessage("初回は出勤ボタンを押してください");
				model.addAttribute("homeForm", homeForm);
				return "html/home";
			}
			attendance = new Attendance();
			attendance.setUser(user);
			attendance.setWorkDate(today);
			attendance.setClockInTime(currentTime);
			attendanceRepository.save(attendance);
			homeForm.setClockStatus(HomeConstant.CLOCK_IN);
			homeForm.setClockStatusMessage(nowToday.format(date) + "に出勤しました。");
			model.addAttribute("homeForm", homeForm);
			return "html/home";
		} else {
			attendance = attendanceOpt.get();
			if (HomeConstant.CLOCK_IN.equals(action) && attendance.getClockInTime() == null) {
				attendance.setClockInTime(currentTime);
				homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				homeForm.setClockStatusMessage(nowToday.format(date) + "に出勤しました。");
			} else if (HomeConstant.CLOCK_IN.equals(action) && attendance.getClockInTime() != null) {
				homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				homeForm.setClockStatusMessage("すでに出勤済みです。");
			} else if (HomeConstant.CLOCK_OUT.equals(action) && attendance.getClockOutTime() == null) {
				attendance.setClockOutTime(currentTime);
				homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
				homeForm.setClockStatusMessage(nowToday.format(date) + "に退勤しました。");
			} else if (HomeConstant.CLOCK_OUT.equals(action) && attendance.getClockOutTime() != null) {
				homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
				homeForm.setClockStatusMessage("すでに退勤済みです。");
			} else if (HomeConstant.BREAK_START.equals(action) && attendance.getBreakStart() == null) {
				attendance.setBreakStart(currentTime);
				homeForm.setClockStatus(HomeConstant.BREAK_START);
				homeForm.setClockStatusMessage(nowToday.format(date) + "に休憩開始しました。");
			} else if (HomeConstant.BREAK_START.equals(action) && attendance.getBreakStart() != null) {
				homeForm.setClockStatus(HomeConstant.BREAK_START);
				homeForm.setClockStatusMessage("すでに休憩開始済みです。");
			} else if (HomeConstant.BREAK_END.equals(action) && attendance.getBreakEnd() == null) {
				attendance.setBreakEnd(currentTime);
				if (attendance.getClockInTime() != null && attendance.getClockOutTime() == null) {
					// レコードに出勤が打刻されているかつ退勤が打刻されていない場合にステータスをclockInにセット
					homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				} else if (attendance.getClockInTime() != null && attendance.getClockOutTime() != null) {
					// レコードに出勤が打刻されているかつ退勤が打刻されている場合にステータスをclockOutにセット
					homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
				}
				homeForm.setClockStatusMessage(nowToday.format(date) + "に休憩終了しました。");
			} else if (HomeConstant.BREAK_END.equals(action) && attendance.getBreakEnd() != null) {
				if (attendance.getClockInTime() != null && attendance.getClockOutTime() == null) {
					// レコードに出勤が打刻されているかつ退勤が打刻されていない場合にステータスをclockInにセット
					homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				} else if (attendance.getClockInTime() != null && attendance.getClockOutTime() != null) {
					// レコードに出勤が打刻されているかつ退勤が打刻されている場合にステータスをclockOutにセット
					homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
				}
				homeForm.setClockStatusMessage("すでに休憩終了済みです。");
			} else {
				homeForm.setClockStatus(HomeConstant.ERROR);
				homeForm.setClockStatusMessage("不明なアクションです");
				model.addAttribute("homeForm", homeForm);
				return "html/home";
			}

			model.addAttribute("homeForm", homeForm);
			attendanceRepository.save(attendance);
			return "html/home";
		}
	}
}