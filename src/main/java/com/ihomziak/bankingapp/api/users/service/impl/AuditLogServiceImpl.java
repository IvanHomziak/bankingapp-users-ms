package com.ihomziak.bankingapp.api.users.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ihomziak.bankingapp.api.users.entity.AuditLog;
import com.ihomziak.bankingapp.api.users.repository.AuditLogRepository;
import com.ihomziak.bankingapp.api.users.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

	private final AuditLogRepository auditLogRepository;

	@Autowired
	public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	@Override
	public List<AuditLog> getAllAuditLogs() {
		return auditLogRepository.findAll();
	}
}
