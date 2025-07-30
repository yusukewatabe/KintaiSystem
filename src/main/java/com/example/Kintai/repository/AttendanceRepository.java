package com.example.Kintai.repository;

import com.example.Kintai.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Optional<Attendance> findByUser_IdAndWorkDate(String userId, String workDate);
}
