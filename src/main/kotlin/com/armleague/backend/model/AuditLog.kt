package com.armleague.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "audit_log")
data class AuditLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    val id: Int? = null,

    @Column(name = "table_name", nullable = false)
    val tableName: String,

    @Column(name = "record_id", nullable = false)
    val recordId: Int,

    @Column(name = "action_type", nullable = false)
    val actionType: String, // INSERT, UPDATE, DELETE

    @Column(name = "old_value", columnDefinition = "TEXT")
    val oldValue: String? = null,

    @Column(name = "new_value", columnDefinition = "TEXT")
    val newValue: String? = null,

    @Column(name = "changed_by")
    val changedBy: Int? = null,

    @Column(name = "changed_at")
    val changedAt: LocalDateTime = LocalDateTime.now()
)