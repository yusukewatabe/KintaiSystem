package com.example.Kintai.controller;

import com.example.Kintai.model.Attendance;
import com.example.Kintai.model.User;
import com.example.Kintai.form.HomeForm;
import com.example.Kintai.form.IndexForm;
import com.example.Kintai.constant.DateFormatConstant;
import com.example.Kintai.constant.FormConstant;
import com.example.Kintai.constant.HomeConstant;
import com.example.Kintai.constant.MappingPathNameConstant;
import com.example.Kintai.constant.ViewNameConstant;
import com.example.Kintai.repository.AttendanceRepository;
import com.example.Kintai.repository.UserRepository;
import com.example.Kintai.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AttendanceController {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MessageUtil messageUtil;

	/** メッセージID：EMK_017 */
	private static final String EMK017 = "EMK_017";

	/** メッセージID：EMK_018 */
	private static final String EMK018 = "EMK_018";

	/** メッセージID：EMK_019 */
	private static final String EMK019 = "EMK_019";

	/** メッセージID：EMK_020 */
	private static final String EMK020 = "EMK_020";

	/** メッセージID：EMK_021 */
	private static final String EMK021 = "EMK_021";

	/** メッセージID：EMK_022 */
	private static final String EMK022 = "EMK_022";

	/** メッセージID：EMK_023 */
	private static final String EMK023 = "EMK_023";

	/** メッセージID：EMK_024 */
	private static final String EMK024 = "EMK_024";

	/** メッセージID：EMK_025 */
	private static final String EMK025 = "EMK_025";

	/** メッセージID：EMK_026 */
	private static final String EMK026 = "EMK_026";

	/**
	 * Home画面から押されたボタンを判定し、dbに時間を登録するメソッド
	 * 
	 * @param userId ユーザーID
	 * @param action 勤怠ステータス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.ATTENDANCE_CLOCK_PATH)
	public String handleClockAction(@RequestParam String userId, @RequestParam String action,
			Model model) {
		// workDay初期値設定
		DateTimeFormatter fmtWorkDate = DateTimeFormatter.ofPattern(DateFormatConstant.DATETIME_FORMAT_YYYY_MM_DD);
		LocalDate workDate = LocalDate.now();
		String today = workDate.format(fmtWorkDate);

		// 出勤、退勤時間等の初期値設定
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DateFormatConstant.DATETIME_FORMAT_HH_MM);
		String currentTime = fmt.format(now);

		Attendance attendance;

		// 出勤、退勤時間等の初期値設定
		Date date = new Date();
		HomeForm homeForm = new HomeForm();
		IndexForm indexForm = new IndexForm();
		// 画面に出力するための日付のフォーマット指定
		SimpleDateFormat nowToday = new SimpleDateFormat(DateFormatConstant.DATETIME_FORMAT_MM_DD_HH_MM);
		// emailをhidden項目にセット
		indexForm.setEmail(userId);

		// userテーブルからuserIdを検索
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			// TODO エラー画面に飛ばす？
			model.addAttribute("error", "ユーザーが見つかりません");
			return ViewNameConstant.HOME_VIEW;
		}

		User user = userOpt.get();

		Optional<Attendance> attendanceOpt = attendanceRepository.findByUser_IdAndWorkDate(userId, today);

		if (attendanceOpt.isEmpty()) {
			if (!HomeConstant.CLOCK_IN.equals(action)) {
				homeForm.setClockStatusMessage(messageUtil.getErrorMessage(EMK017));
				model.addAttribute(FormConstant.ATTRIBUTE_HOMEFORM, homeForm);
				model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
				return ViewNameConstant.HOME_VIEW;
			}

			attendance = new Attendance();
			attendance.setUser(user);
			attendance.setWorkDate(today);
			attendance.setClockInTime(currentTime);
			attendanceRepository.save(attendance);
			homeForm.setClockStatus(HomeConstant.CLOCK_IN);
			homeForm.setClockStatusMessage(nowToday.format(date) + messageUtil.getErrorMessage(EMK018));
			model.addAttribute(FormConstant.ATTRIBUTE_HOMEFORM, homeForm);
			model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
			return ViewNameConstant.HOME_VIEW;

		} else {
			attendance = attendanceOpt.get();

			if (HomeConstant.CLOCK_IN.equals(action) && attendance.getClockInTime() == null) {
				attendance.setClockInTime(currentTime);
				homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				homeForm.setClockStatusMessage(nowToday.format(date) + messageUtil.getErrorMessage(EMK018));

			} else if (HomeConstant.CLOCK_IN.equals(action) && attendance.getClockInTime() != null) {
				homeForm.setClockStatus(HomeConstant.CLOCK_IN);
				homeForm.setClockStatusMessage(messageUtil.getErrorMessage(EMK019));

			} else if (HomeConstant.CLOCK_OUT.equals(action) && attendance.getClockOutTime() == null) {
				attendance.setClockOutTime(currentTime);
				homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
				homeForm.setClockStatusMessage(nowToday.format(date) + messageUtil.getErrorMessage(EMK020));

			} else if (HomeConstant.CLOCK_OUT.equals(action) && attendance.getClockOutTime() != null) {
				homeForm.setClockStatus(HomeConstant.CLOCK_OUT);
				homeForm.setClockStatusMessage(messageUtil.getErrorMessage(EMK021));

			} else if (HomeConstant.BREAK_START.equals(action) && attendance.getBreakStart() == null) {
				attendance.setBreakStart(currentTime);
				homeForm.setClockStatus(HomeConstant.BREAK_START);
				homeForm.setClockStatusMessage(nowToday.format(date) + messageUtil.getErrorMessage(EMK022));

			} else if (HomeConstant.BREAK_START.equals(action) && attendance.getBreakStart() != null) {
				homeForm.setClockStatus(HomeConstant.BREAK_START);
				homeForm.setClockStatusMessage(messageUtil.getErrorMessage(EMK023));

			} else if (HomeConstant.BREAK_END.equals(action) && attendance.getBreakEnd() == null) {
				attendance.setBreakEnd(currentTime);

				if (attendance.getClockInTime() != null && attendance.getClockOutTime() == null) {
					// レコードに出勤が打刻されているかつ退勤が打刻されていない場合にステータスをclockInにセット
					homeForm.setClockStatus(HomeConstant.CLOCK_IN);

				} else if (attendance.getClockInTime() != null && attendance.getClockOutTime() != null) {
					// レコードに出勤が打刻されているかつ退勤が打刻されている場合にステータスをclockOutにセット
					homeForm.setClockStatus(HomeConstant.CLOCK_OUT);

				}
				homeForm.setClockStatusMessage(nowToday.format(date) + messageUtil.getErrorMessage(EMK024));

			} else if (HomeConstant.BREAK_END.equals(action) && attendance.getBreakEnd() != null) {
				if (attendance.getClockInTime() != null && attendance.getClockOutTime() == null) {
					// レコードに出勤が打刻されているかつ退勤が打刻されていない場合にステータスをclockInにセット
					homeForm.setClockStatus(HomeConstant.CLOCK_IN);

				} else if (attendance.getClockInTime() != null && attendance.getClockOutTime() != null) {
					// レコードに出勤が打刻されているかつ退勤が打刻されている場合にステータスをclockOutにセット
					homeForm.setClockStatus(HomeConstant.CLOCK_OUT);

				}
				homeForm.setClockStatusMessage(messageUtil.getErrorMessage(EMK025));

			} else {
				homeForm.setClockStatus(HomeConstant.ERROR);
				homeForm.setClockStatusMessage(messageUtil.getErrorMessage(EMK026));
				model.addAttribute(FormConstant.ATTRIBUTE_HOMEFORM, homeForm);
				model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
				return ViewNameConstant.HOME_VIEW;
			}

			model.addAttribute(FormConstant.ATTRIBUTE_HOMEFORM, homeForm);
			model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
			attendanceRepository.save(attendance);
			return ViewNameConstant.HOME_VIEW;
		}
	}
}