package com.example.Kintai.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users") // データベースのテーブル名を指定
@Getter
@Setter
public class User {

	@Id
	@Column(name = "id", length = 128, nullable = false, unique = true)
	private String id; // 文字列型のID（主キー）

	@Column(name = "pass", length = 32, nullable = false)
	private String password; // カラム名 `pass` に対応

	@Column(name = "firstname", nullable = false)
	private String firstname;

	@Column(name = "lastname", nullable = false)
	private String lastname;
}