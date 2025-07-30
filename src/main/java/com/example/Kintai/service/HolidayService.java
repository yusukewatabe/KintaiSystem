package com.example.Kintai.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class HolidayService {

	// CSV のパス（resources 以下）
	private static final String HOLIDAY_CSV = "static/syukujitsu.csv";

	private final Map<String, String> holidayMap = new HashMap<>();

	/**
	 * アプリ起動時に CSV を読み込んで祝日セットを作成
	 */
	@PostConstruct
	public void loadHolidays() throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new ClassPathResource(HOLIDAY_CSV).getInputStream()))) {
			reader.readLine();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] cols = line.split(",", -1);
				if (cols.length < 2 || cols[0].isBlank())
					continue;
				String date = cols[0].trim();
				String holidayName = cols[1].trim();
				holidayMap.put(date, holidayName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定日が祝日かどうかを返す
	 * 
	 * @param date 判定したい日
	 * @return CSVに含まれている祝日
	 */
	public String judgeHoliday(String date) {
		// 比較用に同じフォーマットの文字列を作成
		// String key = date.format(CSV_FMT);
		System.out.println("[DEBUG] lookup key='" + date + "'");
		if (holidayMap.containsKey(date)) {
			return holidayMap.get(date);
		} else {
			return "notFound";
		}
	}
}
