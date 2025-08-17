package com.jp.Kintai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jp.Kintai.model.User;

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