package uk.co.baconi.oauth.api.common.authentication

import io.kotest.assertions.assertSoftly
import kotlinx.coroutines.runBlocking
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure.Reason
import uk.co.baconi.oauth.api.common.authentication.CustomerState.*

class CustomerAuthenticationServiceTest {

    private val credentialRepo = mockk<CustomerCredentialRepository> {
        val username = slot<String>()
        every { findByUsername(capture(username)) } answers { CustomerCredential(username.captured, "hashed") }
    }

    private val statusRepo = mockk<CustomerStatusRepository> {
        val username = slot<String>()
        every { findByUsername(capture(username)) } answers { CustomerStatus(username.captured, Active) }
    }

    private val checkPassword = mockk<(String, CharArray) -> Boolean> {
        every { this@mockk.invoke("hashed", "valid".toCharArray()) } returns true
    }

    private val underTest = CustomerAuthenticationService(credentialRepo, statusRepo, checkPassword)

    @Nested
    inner class Authenticate {

        @Test
        fun `should return failure of mismatch when no customer credentials found`(): Unit = runBlocking {

            every { credentialRepo.findByUsername("missing") } returns null
            every { checkPassword.invoke("", "valid".toCharArray()) } returns false

            assertSoftly(underTest.authenticate("missing", "valid".toCharArray())) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Mismatched
            }
        }

        @Test
        fun `should return failure of mismatch when customer credentials fail to match`(): Unit = runBlocking {

            every { checkPassword.invoke("hashed", "invalid".toCharArray()) } returns false

            assertSoftly(underTest.authenticate("mismatch", "invalid".toCharArray())) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Mismatched
            }
        }

        @Test
        fun `should return failure of missing when no customer status found`(): Unit = runBlocking {

            every { statusRepo.findByUsername("no-status") } returns null

            assertSoftly(underTest.authenticate("no-status", "valid".toCharArray())) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Missing
            }
        }

        @Test
        fun `should return failure of closed when the customer status is closed`(): Unit = runBlocking {

            every { statusRepo.findByUsername("closed") } returns CustomerStatus("closed", Closed)

            assertSoftly(underTest.authenticate("closed", "valid".toCharArray())) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Closed
            }
        }

        @Test
        fun `should return failure of suspended when the customer status is suspended`(): Unit = runBlocking {

            every { statusRepo.findByUsername("suspended") } returns CustomerStatus("suspended", Suspended)

            assertSoftly(underTest.authenticate("suspended", "valid".toCharArray())) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Suspended
            }
        }

        @Test
        fun `should return failure of locked when the customer status is locked`(): Unit = runBlocking {

            every { statusRepo.findByUsername("locked") } returns CustomerStatus("locked", Locked)

            assertSoftly(underTest.authenticate("locked", "valid".toCharArray())) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Locked
            }
        }

        @Test
        fun `should return success when the customer status is active`(): Unit = runBlocking {

            assertSoftly(underTest.authenticate("aardvark", "valid".toCharArray())) {
                shouldBeInstanceOf<CustomerAuthentication.Success>()
                username shouldBe AuthenticatedUsername("aardvark")
            }
        }
    }
}