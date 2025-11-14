package com.weap.gd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "batch_job_execution")
public class EjecucionTarea {
	@Id
	@Column(name = "job_execution_id")
	private Long id;
	
	private String status;

}
