package com.armleague.backend.repository

import com.armleague.backend.model.AuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditRepository : JpaRepository<AuditLog, Int>