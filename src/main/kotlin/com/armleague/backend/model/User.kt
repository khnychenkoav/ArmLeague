package com.armleague.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.security.MessageDigest

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Int? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER,

    @Column(name = "full_name", nullable = false)
    var fullName: String,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
) {

    companion object {
        fun hashPassword(password: String): String {
            val bytes = password.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}