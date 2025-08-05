package com.example.Kintai.service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import com.example.Kintai.form.BulkForm;
import com.example.Kintai.form.HomeForm;
import com.example.Kintai.model.Attendance;
import com.example.Kintai.repository.AttendanceRepository;

/**
 * BulkEditに関するビジネスロジックが記載されているクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Service
public class BulkEditService {

	/** 初日を判定や正常終了を示す */
	private static final int MULTIPLE_USE_ZERO = 0;

	/** 末日を判定や異常終了を示す */
	private static final int MULTIPLE_USE_ONE = 1;

	/** 休日 */
	private static final String HOLIDAY = "休日";

	/** 平日 */
	private static final String WEEKDAYS = "平日";

	/** bulkday1~31のセッター */
	private static final String SETTER_BULKDAY = "setBulkday";

	/** kinmuKubun1~31のセッター */
	private static final String SETTER_KINMUKUBUN = "setKinmuKubun";

	/** clockInTime1~31のセッター */
	private static final String SETTER_CLOCKINTIME = "setClockInTime";

	/** kinmuKubun1~31のセッター */
	private static final String SETTER_CLOCKOUTTIME = "setClockOutTime";

	/** kinmuKubun1~31のセッター */
	private static final String SETTER_BREAKSTART = "setBreakStart";

	/** kinmuKubun1~31のセッター */
	private static final String SETTER_BREAKEND = "setBreakEnd";

	/** bulkFormのAttribute用の定数 */
	private static final String ATTRIBUTE_BULKFORM = "bulkForm";

	/** homeFormのAttribute用の定数 */
	private static final String ATTRIBUTE_HOMEFORM = "homeForm";

	/** 土曜日を判定 */
	private static final String SATURDAY_JUDGEMENT = "(土)";

	/** 日曜日を判定 */
	private static final String SUNDAY_JUDGEMENT = "(日)";

	/** タイムゾーンをアジア/東京 */
	private static final String TIMEZONE_ASIA_TOKYO = "Asia/Tokyo";

	/** formに空文字をセット */
	private static final String SET_FORM_EMPTY = "";

	/** 日付フォーマット（月、日、曜日） */
	private static final String DATE_FORMAT_DAYOFWEEK = "MM/dd (E)";

	/** 日付フォーマット（年、月、日） */
	private static final String DATE_FORMAT_YYYY_MM_DD = "yyyy/MM/dd";

	@Autowired
	private HolidayService holidayService;

	/**
	 * 月の一覧をformに格納するメソッド
	 * 
	 * @param userId ユーザーID
	 * @param attendanceRepository AttendanceRepository
	 * @param model Spring MVC のモデルオブジェクト
	 */
	public void bulkEditAndPreview(String userId, AttendanceRepository attendanceRepository, Model model) {

		BulkForm bulkform = new BulkForm();
		HomeForm homeForm = new HomeForm();
		YearMonth ym = YearMonth.now(ZoneId.of(TIMEZONE_ASIA_TOKYO));
		List<String> monthFormatList = listDatesWithWeekdays(ym.getYear(), ym.getMonthValue());
		List<String> monthList = listDates(ym.getYear(), ym.getMonthValue());

		// 月の初日、末日を取得
		List<String> monthBounds = listMonth(ym.getYear(), ym.getMonthValue());
		String firstDay = monthBounds.get(MULTIPLE_USE_ZERO);
		String lastDay = monthBounds.get(MULTIPLE_USE_ONE);
		bulkform.setFirstDay(firstDay);
		bulkform.setLastDay(lastDay);

		// 1～monthFormatList.size() 分の setter をループで呼び出し
		for (int i = 0; i < monthFormatList.size(); i++) {
			// bulkday の setter 呼び出し
			String bulkdayMethod = SETTER_BULKDAY + (i + 1);
			String bulkdayValue = monthFormatList.get(i);
			try {
				Method mDay = BulkForm.class.getMethod(bulkdayMethod, String.class);
				mDay.invoke(bulkform, bulkdayValue);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("BulkForm setter (Bulkday) 呼び出し失敗: " + bulkdayMethod, e);
			}

			// dbから出勤、退勤、休憩開始時間、休憩終了時間を取得し、formに格納
			String workDate = monthList.get(i);

			// 勤務区分を判定（土曜日、日曜日、祝日）
			String kinmuKubun;
			if (bulkdayValue.contains(SATURDAY_JUDGEMENT) || bulkdayValue.contains(SUNDAY_JUDGEMENT)) {
				kinmuKubun = HOLIDAY;
			} else {
				String holiday = holidayService.judgeHoliday(workDate);
				if (holiday == "notFound") {
					kinmuKubun = WEEKDAYS;
				} else {
					kinmuKubun = holiday;
				}
			}
			Optional<Attendance> atts = attendanceRepository.findByUser_IdAndWorkDate(userId, workDate);
			String clockInMethod = SETTER_CLOCKINTIME + (i + 1);
			String clockOutMethod = SETTER_CLOCKOUTTIME + (i + 1);
			String breakStartMethod = SETTER_BREAKSTART + (i + 1);
			String breakEndMethod = SETTER_BREAKEND + (i + 1);

			// レコードから取得
			String recordClockInTime;
			String recordClockOutTime;
			String recordBreakStart;
			String recordBreakEnd;
			if (atts.isPresent()) {
				Attendance attendance = atts.get();
				recordClockInTime = attendance.getClockInTime();
				recordClockOutTime = attendance.getClockOutTime();
				recordBreakStart = attendance.getBreakStart();
				recordBreakEnd = attendance.getBreakEnd();
				if (recordClockInTime != null) {
					// clockInTimeX の setter を動的に呼び出し
					// 出勤時間がnullでない場合
					try {
						Method mTime = BulkForm.class.getMethod(clockInMethod, String.class);
						mTime.invoke(bulkform, recordClockInTime);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("BulkForm setter (clockInTime) 呼び出し失敗: " + clockInMethod, e);
					}
				} else {
					// 出勤時間がnullの場合
					try {
						Method mTime = BulkForm.class.getMethod(clockInMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("BulkForm setter (clockInTime) 呼び出し失敗: " + clockInMethod, e);
					}
				}
				if (recordClockOutTime != null) {
					// clockOutTimeX の setter を動的に呼び出し
					// 退勤時間がnullでない場合dbから取得した値をformに格納
					try {
						Method mTime = BulkForm.class.getMethod(clockOutMethod, String.class);
						mTime.invoke(bulkform, recordClockOutTime);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("BulkForm setter (clockOutTime) 呼び出し失敗: " + clockOutMethod, e);
					}
				} else {
					// 退勤時間がnullの場合空文字をFormに格納
					try {
						Method mTime = BulkForm.class.getMethod(clockOutMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("BulkForm setter (clockOutTime) 呼び出し失敗: " + clockOutMethod, e);
					}
				}
				if (recordBreakStart != null) {
					// breakStartX の setter を動的に呼び出し
					// 休憩時間がnullでない場合dbから取得した値をformに格納
					try {
						Method mTime = BulkForm.class.getMethod(breakStartMethod, String.class);
						mTime.invoke(bulkform, recordBreakStart);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("BulkForm setter (breakStart) 呼び出し失敗: " + breakStartMethod, e);
					}
				} else {
					// 休憩時間がnullの場合空文字をFormに格納
					try {
						Method mTime = BulkForm.class.getMethod(breakStartMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("BulkForm setter (breakStart) 呼び出し失敗: " + breakStartMethod, e);
					}
				}
				if (recordBreakEnd != null) {
					// breakEndX の setter を動的に呼び出し
					// 休憩終了時間がnullでない場合dbから取得した値をformに格納
					try {
						Method mTime = BulkForm.class.getMethod(breakEndMethod, String.class);
						mTime.invoke(bulkform, recordBreakEnd);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("BulkForm setter (breakEnd) 呼び出し失敗: " + breakEndMethod, e);
					}
				} else {
					// 休憩終了時間がnullの場合空文字をFormに格納
					try {
						Method mTime = BulkForm.class.getMethod(breakEndMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("BulkForm setter (breakEnd) 呼び出し失敗: " + breakEndMethod, e);
					}
				}
			} else {
				// レコードがない（未出勤など）の場合空文字を格納
				try {
					Method mTime = BulkForm.class.getMethod(clockInMethod, String.class);
					mTime.invoke(bulkform, SET_FORM_EMPTY);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("BulkForm setter (clockInTime) 呼び出し失敗: " + clockInMethod, e);
				}
				try {
					Method mTime = BulkForm.class.getMethod(clockOutMethod, String.class);
					mTime.invoke(bulkform, SET_FORM_EMPTY);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("BulkForm setter (clockOutTime) 呼び出し失敗: " + clockOutMethod, e);
				}
				try {
					Method mTime = BulkForm.class.getMethod(breakStartMethod, String.class);
					mTime.invoke(bulkform, SET_FORM_EMPTY);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("BulkForm setter (breakStart) 呼び出し失敗: " + breakStartMethod, e);
				}
				try {
					Method mTime = BulkForm.class.getMethod(breakEndMethod, String.class);
					mTime.invoke(bulkform, SET_FORM_EMPTY);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("BulkForm setter (breakEnd) 呼び出し失敗: " + breakEndMethod, e);
				}
			}

			// kinmuKubunX の setter を動的に呼び出し
			String kinmuMethod = SETTER_KINMUKUBUN + (i + 1);
			try {
				Method mKinmu = BulkForm.class.getMethod(kinmuMethod, String.class);
				mKinmu.invoke(bulkform, kinmuKubun);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("BulkForm setter (KinmuKubun) 呼び出し失敗: " + kinmuMethod, e);
			}
		}
		homeForm.setEmail(userId);

		model.addAttribute(ATTRIBUTE_HOMEFORM, homeForm);
		model.addAttribute(ATTRIBUTE_BULKFORM, bulkform);
	}

	/**
	 * 指定した年・月の日付を「yyyy/MM/dd」形式で返す
	 *
	 * @param year  西暦年
	 * @param month 月（1～12）
	 * @return 月(yyyy/MM/dd)のリスト
	 */
	public List<String> listDates(int year, int month) {
		YearMonth ym = YearMonth.of(year, month);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD, Locale.JAPANESE);

		List<String> result = new ArrayList<>(ym.lengthOfMonth());
		for (int i = 1; i <= ym.lengthOfMonth(); i++) {
			LocalDate date = ym.atDay(i);
			result.add(date.format(fmt));
		}
		return result;
	}

	/**
	 * 指定した年・月の日付と曜日を「M/d (E)」形式で返す
	 *
	 * @param year  西暦年
	 * @param month 月（1～12）
	 * @return 日付、曜日(M/d (E))のリスト
	 */
	private List<String> listDatesWithWeekdays(int year, int month) {
		YearMonth ym = YearMonth.of(year, month);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DATE_FORMAT_DAYOFWEEK, Locale.JAPANESE);

		List<String> result = new ArrayList<>(ym.lengthOfMonth());
		for (int i = 1; i <= ym.lengthOfMonth(); i++) {
			LocalDate date = ym.atDay(i);
			result.add(date.format(fmt));
		}
		return result;
	}

	/**
	 * 初日と末日を取得するメソッド
	 * 
	 * @param year 西暦年
	 * @param month 月
	 * @return 月の初日、末日のリスト
	 */
	private static List<String> listMonth(int year, int month) {
		YearMonth ym = YearMonth.of(year, month);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD, Locale.JAPANESE);

		List<String> result = new ArrayList<>(2);

		LocalDate firstDate = ym.atDay(1);
		LocalDate lastDate = ym.atEndOfMonth();
		// 月の初日
		result.add(firstDate.format(fmt));
		// 月の末日
		result.add(lastDate.format(fmt));
		return result;
	}
}
