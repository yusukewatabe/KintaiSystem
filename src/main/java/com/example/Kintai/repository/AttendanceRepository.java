package com.example.Kintai.repository;

import com.example.Kintai.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * AttendanceテーブルへuserId、workDateをキーにして検索(INSERT)するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Optional<Attendance> findByUser_IdAndWorkDate(String userId, String workDate);
}
