package com.example.Kintai.model;

import java.time.LocalDate;
import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attendances")
@Getter
@Setter
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "work_date")
	private LocalDate workDate;

	@Column(name = "clock_in_time")
	private Timestamp clockInTime;

	@Column(name = "clock_out_time")
	private Timestamp clockOutTime;

	@Column(name = "break_start")
	private Timestamp breakStart;

	@Column(name = "break_end")
	private Timestamp breakEnd;
}
