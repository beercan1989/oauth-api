package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.client.*
import com.sbgcore.oauth.api.client.ClientId.*
import com.sbgcore.oauth.api.client.ClientType.Confidential
import com.sbgcore.oauth.api.client.ClientType.Public
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ClientAuthenticationServiceTest {

    private val consumerZConfiguration = mockk<ClientConfiguration> {
        every { id } returns ConsumerZ
        every { type } returns Confidential
        every { isConfidential } returns true
        every { isPublic } returns false
    }

    private val consumerZSecret = mockk<ClientSecret> {
        every { clientId } returns ConsumerZ
        every { secret } returns "consumer-z-secret"
    }

    private val consumerYConfiguration = mockk<ClientConfiguration> {
        every { id } returns ConsumerY
        every { type } returns Public
        every { isConfidential } returns false
        every { isPublic } returns true
    }

    private val consumerYSecret = mockk<ClientSecret> {
        every { clientId } returns ConsumerY
        every { secret } returns "consumer-y-secret"
    }

    private val clientSecretRepository = mockk<ClientSecretRepository> {
        every { findAllByClientId(any<String>()) } returns emptySequence()
    }

    private val clientConfigurationRepository = mockk<ClientConfigurationRepository> {
        every { findByClientId(any<ClientId>()) } returns null
        every { findByClientId(any<String>()) } returns null
    }

    private val checkPassword = mockk<(String, CharArray) -> Boolean> {
        every { this@mockk.invoke(any(), any()) } returns false
    }

    private val underTest = ClientAuthenticationService(
        clientSecretRepository,
        clientConfigurationRepository,
        checkPassword
    )

    @Nested
    inner class ConfidentialClientTest {

        @Test
        fun `return a ConfidentialClient when secret matches and its a confidential client`() {

            every { clientSecretRepository.findAllByClientId(any<String>()) } returns sequenceOf(consumerZSecret)
            every { checkPassword.invoke(any(), any()) } returns true
            every { clientConfigurationRepository.findByClientId(any<ClientId>()) } returns consumerZConfiguration

            val client = underTest.confidentialClient("consumer-z", "consumer-z-secret")
            client.shouldNotBeNull()
            assertSoftly(client) {
                id shouldBe ConsumerZ
                configuration.id shouldBe ConsumerZ
                configuration.type shouldBe Confidential
            }
        }

        @Test
        fun `return null when client secret does not exist`() {

            underTest.confidentialClient("consumer-z", "consumer-z-secret") shouldBe null
        }

        @Test
        fun `return null when client secret does not match`() {

            every { clientSecretRepository.findAllByClientId(any<ClientId>()) } returns sequenceOf(consumerZSecret)
            every { checkPassword.invoke(any(), any()) } returns false

            underTest.confidentialClient("consumer-z", "consumer-y-secret") shouldBe null
        }

        @Test
        fun `return null when client configuration does not exist`() {

            every { clientSecretRepository.findAllByClientId(any<ClientId>()) } returns sequenceOf(consumerZSecret)
            every { checkPassword.invoke(any(), any()) } returns true

            underTest.confidentialClient("consumer-z", "consumer-z-secret") shouldBe null
        }

        @Test
        fun `return null when client is not confidential`() {

            every { clientSecretRepository.findAllByClientId(any<String>()) } returns sequenceOf(consumerYSecret)
            every { checkPassword.invoke(any(), any()) } returns true
            every { clientConfigurationRepository.findByClientId(any<ClientId>()) } returns consumerYConfiguration

            underTest.confidentialClient("consumer-y", "consumer-y-secret") shouldBe null
        }
    }

    @Nested
    inner class PublicClientTest {

        @Test
        fun `return a PublicClient when its a public client`() {

            every { clientConfigurationRepository.findByClientId(any<String>()) } returns consumerYConfiguration

            val client = underTest.publicClient("consumer-y")
            client.shouldNotBeNull()

            assertSoftly(client) {
                id shouldBe ConsumerY
                configuration.id shouldBe ConsumerY
                configuration.type shouldBe Public
            }
        }

        @Test
        fun `return null when client configuration does not exist`() {

            underTest.publicClient("consumer-x") shouldBe null
        }

        @Test
        fun `return null when client is not public`() {

            every { clientConfigurationRepository.findByClientId(any<String>()) } returns consumerZConfiguration

            underTest.publicClient("consumer-z") shouldBe null
        }

        @Test
        fun `return null when client id is null`() {

            underTest.publicClient(null) shouldBe null
        }
    }
}