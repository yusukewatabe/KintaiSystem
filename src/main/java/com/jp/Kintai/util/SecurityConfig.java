package com.jp.Kintai.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * パスワードをエンコード設定に関するクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Configuration
public class SecurityConfig {

	/**
	 * BCryptを使用したエンコードメソッド
	 * @return PasswordEncoder PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(12);
	}
}
