package com.jp.Kintai.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.jp.Kintai.enumClass.LogLevel;
import com.jp.Kintai.util.LoggerUtil;
import com.jp.Kintai.util.MessageUtil;
import jakarta.annotation.PostConstruct;

/**
 * 祝日判定のビジネスロジックが記載されているクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Service
public class HolidayService {

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private LoggerUtil loggerUtil;

	/** CSV のパス（resources 以下） */
	private static final String HOLIDAY_CSV = "static/syukujitsu.csv";

	/** 祝日が見つからない場合にnotFoundを返却 */
	private static final String NOT_FOUND = "notFound";

	/** CSV内の,で区切られるかの判定に使用 */
	private static final String COMMA = ",";

	/**  */
	private static final int MUINUS_ONE = -1;

	/** リストで0番目を取得するために使用 */
	private static final int LIST_USE_ZERO = 0;

	/** リストで1番目を取得するために使用 */
	private static final int LIST_USE_ONE = 1;

	/** 長さを2に固定する */
	private static final int MULTIPLE_USE_TWO = 2;

	/** メッセージID：EMK_033 */
	private static final String EMK033 = "EMK_033";

	/** 文字コード */
	private static final String SJIS = "MS932";

	private final Map<String, String> holidayMap = new HashMap<>();

	/**
	 * アプリ起動時に CSV を読み込んで祝日セットを作成
	 */
	@PostConstruct
	public void loadHolidays() throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new ClassPathResource(HOLIDAY_CSV).getInputStream(), Charset.forName(SJIS)))) {
			reader.readLine();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] cols = line.split(COMMA, MUINUS_ONE);
				if (cols.length < MULTIPLE_USE_TWO || cols[LIST_USE_ZERO].isBlank())
					continue;
				String date = cols[LIST_USE_ZERO].trim();
				String holidayName = cols[LIST_USE_ONE].trim();
				holidayMap.put(date, holidayName);
			}
		} catch (Exception e) {
			String errorMessage = e.toString();
			loggerUtil.LogOutput(LogLevel.FATAL, messageUtil.getErrorMessage(EMK033), errorMessage);
		}
	}

	/**
	 * 指定日が祝日かどうかを返す
	 * 
	 * @param date 判定日
	 * @return CSVに含まれている祝日
	 */
	public String judgeHoliday(String date) {
		// 比較用に同じフォーマットの文字列を作成
		if (holidayMap.containsKey(date)) {
			return holidayMap.get(date);
		} else {
			return NOT_FOUND;
		}
	}
}
