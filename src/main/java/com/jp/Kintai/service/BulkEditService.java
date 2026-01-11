package com.jp.Kintai.service;

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
import com.jp.Kintai.constant.DateFormatConstant;
import com.jp.Kintai.constant.FormConstant;
import com.jp.Kintai.enumClass.LogLevel;
import com.jp.Kintai.form.BulkForm;
import com.jp.Kintai.form.HomeForm;
import com.jp.Kintai.form.IndexForm;
import com.jp.Kintai.model.Attendance;
import com.jp.Kintai.repository.AttendanceRepository;
import com.jp.Kintai.util.LoggerUtil;
import com.jp.Kintai.util.MessageUtil;

/**
 * BulkEditに関するビジネスロジックが記載されているクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Service
public class BulkEditService {

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private LoggerUtil loggerUtil;

	/** 初日を判定や正常終了を示す */
	private static final int MULTIPLE_USE_ZERO = 0;

	/** 末日を判定や異常終了を示す */
	private static final int MULTIPLE_USE_ONE = 1;

	/** リストサイズ数 */
	private static final int LISTSIZE_TWO = 2;

	/** 休日 */
	private static final String HOLIDAY = "休日";

	/** 平日 */
	private static final String WEEKDAYS = "平日";

	/** bulkday1~31のセッター */
	private static final String SETTER_BULKDAY = "setBulkday";

	/** kinmuKubun1~31のセッター */
	private static final String SETTER_KINMUKUBUN = "setKinmuKubun";

	/** AtType1~31のセッター */
	private static final String SETTER_ATTYPE = "setAtType";

	/** clockInTime1~31のセッター */
	private static final String SETTER_CLOCKINTIME = "setClockInTime";

	/** kinmuKubun1~31のセッター */
	private static final String SETTER_CLOCKOUTTIME = "setClockOutTime";

	/** kinmuKubun1~31のセッター */
	private static final String SETTER_BREAKSTART = "setBreakStart";

	/** kinmuKubun1~31のセッター */
	private static final String SETTER_BREAKEND = "setBreakEnd";

	/** remarksS1~31のセッター */
	private static final String SETTER_REMARKS = "setRemarks";

	/** 土曜日を判定 */
	private static final String SATURDAY_JUDGEMENT = "(土)";

	/** 日曜日を判定 */
	private static final String SUNDAY_JUDGEMENT = "(日)";

	/** formに空文字をセット */
	private static final String SET_FORM_EMPTY = "";

	/** メッセージID：EMK_011 */
	private static final String EMK011 = "EMK_011";

	/** メッセージID：EMK_012 */
	private static final String EMK012 = "EMK_012";

	/** メッセージID：EMK_013 */
	private static final String EMK013 = "EMK_013";

	/** メッセージID：EMK_014 */
	private static final String EMK014 = "EMK_014";

	/** メッセージID：EMK_015 */
	private static final String EMK015 = "EMK_015";

	/** メッセージID：EMK_016 */
	private static final String EMK016 = "EMK_016";

	/** メッセージID：EMK_037 */
	private static final String EMK037 = "EMK_037";

	/** メッセージID：EMK_038 */
	private static final String EMK038 = "EMK_038";

	/**
	 * 月の一覧をformに格納するメソッド
	 * 
	 * @param userId               ユーザーID
	 * @param attendanceRepository AttendanceRepository
	 * @param model                Spring MVC のモデルオブジェクト
	 * @return true or false
	 */
	public void bulkEditAndPreview(String userId, AttendanceRepository attendanceRepository, Model model) {

		BulkForm bulkform = new BulkForm();
		HomeForm homeForm = new HomeForm();
		IndexForm indexForm = new IndexForm();
		YearMonth ym = YearMonth.now(ZoneId.of(DateFormatConstant.TIMEZONE_ASIA_TOKYO));
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
				String errorMessage = e.toString();
				loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK011) + bulkdayMethod, errorMessage);
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
			String atTypeMethod = SETTER_ATTYPE + (i + 1);
			String clockInMethod = SETTER_CLOCKINTIME + (i + 1);
			String clockOutMethod = SETTER_CLOCKOUTTIME + (i + 1);
			String breakStartMethod = SETTER_BREAKSTART + (i + 1);
			String breakEndMethod = SETTER_BREAKEND + (i + 1);
			String remarksMethod = SETTER_REMARKS + (i + 1);

			// レコードから取得
			String recordAtType;
			String recordClockInTime;
			String recordClockOutTime;
			String recordBreakStart;
			String recordBreakEnd;
			String recordremarks;
			if (atts.isPresent()) {
				Attendance attendance = atts.get();
				recordAtType = attendance.getAtType();
				recordClockInTime = attendance.getClockInTime();
				recordClockOutTime = attendance.getClockOutTime();
				recordBreakStart = attendance.getBreakStart();
				recordBreakEnd = attendance.getBreakEnd();
				recordremarks = attendance.getRemarks();
				if (recordAtType != null) {
					// if (recordAtType.equals("有給")) {
					// recordAtType = "yukyu";
					// }
					// atTypeX の setter を動的に呼び出し
					// 出勤時間がnullでない場合
					try {
						Method mTime = BulkForm.class.getMethod(atTypeMethod, String.class);
						mTime.invoke(bulkform, recordAtType);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK037) + atTypeMethod,
								errorMessage);
					}
				} else {
					// 勤怠種別がnullの場合
					try {
						Method mTime = BulkForm.class.getMethod(atTypeMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK037) + atTypeMethod,
								errorMessage);
					}
				}
				if (recordClockInTime != null) {
					// clockInTimeX の setter を動的に呼び出し
					// 出勤時間がnullでない場合
					try {
						Method mTime = BulkForm.class.getMethod(clockInMethod, String.class);
						mTime.invoke(bulkform, recordClockInTime);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK012) + clockInMethod,
								errorMessage);
					}
				} else {
					// 出勤時間がnullの場合
					try {
						Method mTime = BulkForm.class.getMethod(clockInMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK012) + clockInMethod,
								errorMessage);
					}
				}
				if (recordClockOutTime != null) {
					// clockOutTimeX の setter を動的に呼び出し
					// 退勤時間がnullでない場合dbから取得した値をformに格納
					try {
						Method mTime = BulkForm.class.getMethod(clockOutMethod, String.class);
						mTime.invoke(bulkform, recordClockOutTime);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK013) + clockOutMethod,
								errorMessage);
					}
				} else {
					// 退勤時間がnullの場合空文字をFormに格納
					try {
						Method mTime = BulkForm.class.getMethod(clockOutMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK013) + clockOutMethod,
								errorMessage);
					}
				}
				if (recordBreakStart != null) {
					// breakStartX の setter を動的に呼び出し
					// 休憩時間がnullでない場合dbから取得した値をformに格納
					try {
						Method mTime = BulkForm.class.getMethod(breakStartMethod, String.class);
						mTime.invoke(bulkform, recordBreakStart);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK014) + breakStartMethod,
								errorMessage);
					}
				} else {
					// 休憩時間がnullの場合空文字をFormに格納
					try {
						Method mTime = BulkForm.class.getMethod(breakStartMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK014) + breakStartMethod,
								errorMessage);
					}
				}
				if (recordBreakEnd != null) {
					// breakEndX の setter を動的に呼び出し
					// 休憩終了時間がnullでない場合dbから取得した値をformに格納
					try {
						Method mTime = BulkForm.class.getMethod(breakEndMethod, String.class);
						mTime.invoke(bulkform, recordBreakEnd);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK015) + breakEndMethod,
								errorMessage);
					}
				} else {
					// 休憩終了時間がnullの場合空文字をFormに格納
					try {
						Method mTime = BulkForm.class.getMethod(breakEndMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK015) + breakEndMethod,
								errorMessage);
					}
				}
				if (recordremarks != null) {
					// remarksX の setter を動的に呼び出し
					// 出勤時間がnullでない場合
					try {
						Method mTime = BulkForm.class.getMethod(remarksMethod, String.class);
						mTime.invoke(bulkform, recordremarks);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK038) + remarksMethod,
								errorMessage);
					}
				} else {
					// 出勤時間がnullの場合
					try {
						Method mTime = BulkForm.class.getMethod(remarksMethod, String.class);
						mTime.invoke(bulkform, SET_FORM_EMPTY);
					} catch (Exception e) {
						String errorMessage = e.toString();
						loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK038) + remarksMethod,
								errorMessage);
					}
				}
			} else {
				// レコードがない（未出勤など）の場合空文字を格納
				try {
					Method mTime = BulkForm.class.getMethod(clockInMethod, String.class);
					mTime.invoke(bulkform, SET_FORM_EMPTY);
				} catch (Exception e) {
					String errorMessage = e.toString();
					loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK012) + clockInMethod,
							errorMessage);
				}
				try {
					Method mTime = BulkForm.class.getMethod(clockOutMethod, String.class);
					mTime.invoke(bulkform, SET_FORM_EMPTY);
				} catch (Exception e) {
					String errorMessage = e.toString();
					loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK013) + clockOutMethod,
							errorMessage);

				}
				try {
					Method mTime = BulkForm.class.getMethod(breakStartMethod, String.class);
					mTime.invoke(bulkform, SET_FORM_EMPTY);
				} catch (Exception e) {
					String errorMessage = e.toString();
					loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK014) + breakStartMethod,
							errorMessage);
				}
				try {
					Method mTime = BulkForm.class.getMethod(breakEndMethod, String.class);
					mTime.invoke(bulkform, SET_FORM_EMPTY);
				} catch (Exception e) {
					String errorMessage = e.toString();
					loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK015) + breakEndMethod,
							errorMessage);
				}
			}

			// kinmuKubunX の setter を動的に呼び出し
			String kinmuMethod = SETTER_KINMUKUBUN + (i + 1);
			try {
				Method mKinmu = BulkForm.class.getMethod(kinmuMethod, String.class);
				mKinmu.invoke(bulkform, kinmuKubun);
			} catch (Exception e) {
				String errorMessage = e.toString();
				loggerUtil.LogOutput(LogLevel.ERROR, messageUtil.getErrorMessage(EMK016) + kinmuMethod, errorMessage);
			}
		}
		indexForm.setEmail(userId);

		model.addAttribute(FormConstant.ATTRIBUTE_HOMEFORM, homeForm);
		model.addAttribute(FormConstant.ATTRIBUTE_INDEXFORM, indexForm);
		model.addAttribute(FormConstant.ATTRIBUTE_BULKFORM, bulkform);
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
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DateFormatConstant.DATETIME_FORMAT_YYYY_MM_DD,
				Locale.JAPANESE);

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
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DateFormatConstant.DATE_FORMAT_DAYOFWEEK, Locale.JAPANESE);

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
	 * @param year  西暦年
	 * @param month 月
	 * @return 月の初日、末日のリスト
	 */
	private static List<String> listMonth(int year, int month) {
		YearMonth ym = YearMonth.of(year, month);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DateFormatConstant.DATETIME_FORMAT_YYYY_MM_DD,
				Locale.JAPANESE);

		List<String> result = new ArrayList<>(LISTSIZE_TWO);

		LocalDate firstDate = ym.atDay(MULTIPLE_USE_ONE);
		LocalDate lastDate = ym.atEndOfMonth();
		// 月の初日
		result.add(firstDate.format(fmt));
		// 月の末日
		result.add(lastDate.format(fmt));
		return result;
	}
}
