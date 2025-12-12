package com.armleague.backend.service

import com.armleague.backend.model.AuditLog
import com.armleague.backend.repository.AuditRepository
import org.springframework.stereotype.Service
import org.springframework.scheduling.annotation.Async

@Service
class AuditService(
    private val auditRepository: AuditRepository
) {
    fun log(tableName: String, recordId: Int, action: String, oldVal: String?, newVal: String?) {
        val logEntry = AuditLog(
            tableName = tableName,
            recordId = recordId,
            actionType = action,
            oldValue = oldVal,
            newValue = newVal,
            changedBy = 1
        )
        auditRepository.save(logEntry)
    }
}