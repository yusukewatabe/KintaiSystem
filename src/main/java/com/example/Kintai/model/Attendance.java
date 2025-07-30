package com.example.Kintai.model;

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
	private String workDate;

	@Column(name = "clock_in_time")
	private String clockInTime;

	@Column(name = "clock_out_time")
	private String clockOutTime;

	@Column(name = "break_start")
	private String breakStart;

	@Column(name = "break_end")
	private String breakEnd;
}
