package com.ihomziak.bankingapp.api.users.service;

import java.util.List;

import com.ihomziak.bankingapp.api.users.entity.AuditLog;

public interface AuditLogService {

	List<AuditLog> getAllAuditLogs();
}
