package com.ihomziak.bankingapp.api.users.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AuditLog {

	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
	private Long id;
	private String action;
	private String userName;
	private Long noteId;
	private String noteContent;
	private LocalDateTime timestamp;
}
