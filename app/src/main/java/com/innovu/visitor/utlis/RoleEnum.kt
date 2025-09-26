package com.innovu.visitor.utlis
enum class UserRole(val id: Int) {
    SUPER_ADMIN(1),
    ADMIN(2),
    PRINCIPAL(3),
    TEACHER(4),
    SECURITY(5);

    companion object {
        fun fromId(id: Int): UserRole? = values().find { it.id == id }
        fun fromName(name: String): UserRole? = values().find { it.name.equals(name, ignoreCase = true) }
    }
}