package com.hogent.squads.model.domain

class CredentialsValidator {

    companion object {
        fun validateUsername(username: String?): Boolean {
            if (username == null || username.trim { it <= ' ' }
                .isEmpty() || username.isEmpty() || username.length >= 20
            ) {
                return false
            }
            return true
        }

        fun validatePassword(password: String?): Boolean {
            if (password == null || password.trim { it <= ' ' }
                .isEmpty() || password.length <= 4 || password.length >= 20
            ) {
                return false
            }
            return true
        }
    }
}
