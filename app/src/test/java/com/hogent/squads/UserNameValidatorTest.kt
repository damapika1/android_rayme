package com.hogent.squads

import com.hogent.squads.model.domain.CredentialsValidator
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class UserNameValidatorTest(
    private val userName: String?,
    private val valid: Boolean
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: isValid({0})={1}")
        fun data(): Collection<Array<Any?>> {
            return listOf(
                arrayOf("banaan", true),
                arrayOf("banaaaaaaaaaaaaaaaan", false),
                arrayOf("", false),
                arrayOf(null, false)
            )
        }
    }

    @Test
    fun shouldValidateUsername() {
        assertEquals(CredentialsValidator.validateUsername(userName), valid)
    }
}
