package com.jp.Kintai.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jp.Kintai.constant.DateFormatConstant;
import com.jp.Kintai.constant.MappingPathNameConstant;
import com.jp.Kintai.constant.ViewNameConstant;
import com.jp.Kintai.model.Attendance;
import com.jp.Kintai.model.User;
import com.jp.Kintai.repository.AttendanceRepository;
import com.jp.Kintai.repository.UserRepository;
import com.jp.Kintai.service.BulkEditService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * bulkEdit.htmlからリクエストされた処理をするクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Controller
public class BulkEditController {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private BulkEditService bulkEditService;

	@Autowired
	private UserRepository userRepository;

	/**
	 * 一括編集画面へ遷移するメソッド
	 * 
	 * @param userId メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.BULKEDIT_PATH)
	public String editPreview(String userId, Model model) {

		if(!bulkEditService.bulkEditAndPreview(userId, attendanceRepository, model)){
			return "error";
		}
		
		return ViewNameConstant.BULKEDIT_HTML_PATH;
	}

	/**
	 * bulkEdit.htmlに入力された値をDBに保存するメソッド
	 * 
	 * @param userId ユーザーID
	 * @param model Spring MVC のモデルオブジェクト
	 * @param clockInTimeList 出勤時間のリスト(1~31)
	 * @param clockOutTimeList 退勤時間のリスト(1~31)
	 * @param breakStartList 休憩開始時間のリスト(1~31)
	 * @param breakEndList 休憩終了時間のリスト(1~31)
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.BULKEDITPREVIEW_DATABASESET_PATH)
	public String clockListDbSubmit(String userId, Model model, @RequestParam List<String> clockInTimeList,
			@RequestParam List<String> clockOutTimeList, @RequestParam List<String> breakStartList,
			@RequestParam List<String> breakEndList) {

		YearMonth ym = YearMonth.now(ZoneId.of(DateFormatConstant.TIMEZONE_ASIA_TOKYO));

		Attendance attendance = new Attendance();

		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			// TODO エラー処理へ遷移
			model.addAttribute("error", "ユーザーが見つかりません");
			return "error";
		}

		// 月の一覧を取得
		List<String> workDateList = bulkEditService.listDates(ym.getYear(), ym.getMonthValue());
		String workDate = null;
		for (int i = 0; i < workDateList.size(); i++) {

			String clockIn = clockInTimeList.get(i);
			String clockOut = clockOutTimeList.get(i);
			String breakStart = breakStartList.get(i);
			String breakEnd = breakEndList.get(i);

			workDate = workDateList.get(i);

			Optional<Attendance> attendanceOpt = attendanceRepository.findByUser_IdAndWorkDate(userId, workDate);
			if (attendanceOpt.isPresent()) {
				attendance = attendanceOpt.get();
			} else {
				// インスタンスを使いまわさないように新規レコードを作成
				attendance = new Attendance();
				attendance.setUser(userOpt.get());
				attendance.setWorkDate(workDate);
			}
			// AttendanceテーブルへbulkEditから取得した時刻をdbへ格納
			// 時刻がなにもない場合は次のループへ
			if (emptyAndNullCheck(clockIn) && emptyAndNullCheck(clockOut) && emptyAndNullCheck(breakStart)
					&& emptyAndNullCheck(breakEnd)) {
				continue;
			}
			if (emptyAndNullCheck(attendance.getClockInTime())
					|| !attendance.getClockInTime().equals(clockInTimeList.get(i))) {
				attendance.setClockInTime(clockInTimeList.get(i));
			}
			if (emptyAndNullCheck(attendance.getClockOutTime())
					|| !attendance.getClockOutTime().equals(clockOutTimeList.get(i))) {
				attendance.setClockOutTime(clockOutTimeList.get(i));
			}
			if (emptyAndNullCheck(attendance.getBreakStart())
					|| !attendance.getBreakStart().equals(breakStartList.get(i))) {
				attendance.setBreakStart(breakStartList.get(i));
			}
			if (emptyAndNullCheck(attendance.getBreakEnd()) || !attendance.getBreakEnd().equals(breakEndList.get(i))) {
				attendance.setBreakEnd(breakEndList.get(i));
			}

			attendanceRepository.save(attendance);
		}

		// dbからテーブルの値を取得
		bulkEditService.bulkEditAndPreview(userId, attendanceRepository, model);
		return ViewNameConstant.BULKEDITPREVIEW_HTML_PATH;
	}

	/**
	 * null及び空文字チェックをするメソッド
	 * 
	 * @param check チェックする文字列
	 * @return true or false
	 */
	private boolean emptyAndNullCheck(String check) {
		return check == null || check.isBlank();
	}
}
