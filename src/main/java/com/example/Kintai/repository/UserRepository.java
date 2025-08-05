package com.example.Kintai.repository;

import com.example.Kintai.model.User;
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
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findById(String id);
}