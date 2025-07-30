package com.example.Kintai.service;

import org.springframework.stereotype.Service;

import com.example.Kintai.model.Attendance;
import com.example.Kintai.repository.AttendanceRepository;

@Service
public class AttendanceService {
	private final AttendanceRepository attendanceRepository;

	public AttendanceService(AttendanceRepository attendanceRepository) {
		this.attendanceRepository = attendanceRepository;
	}

	public Attendance getTodayAttendance(String userId, String workDay) {
		return attendanceRepository
				.findByUser_IdAndWorkDate(userId, workDay)
				.orElse(null); // レコードがなければ null、あるいは Optional のまま返す
	}
}
