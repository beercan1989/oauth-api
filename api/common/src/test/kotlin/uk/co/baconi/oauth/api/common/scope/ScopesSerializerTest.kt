package uk.co.baconi.oauth.api.common.scope

import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.scope.Scope.*

class ScopesSerializerTest {

    private val underTest = ScopesSerializer

    private val json = Json { encodeDefaults = true }
    private fun encode(scope: Set<Scope>): String = json.encodeToString(underTest, scope)
    private fun decode(data: String): Set<Scope> = json.decodeFromString(underTest, data)

    @Test
    fun `should be able to encode an empty set of scopes`() {
        encode(emptySet()) shouldBe "\"\""
    }

    @Test
    fun `should be able to encode a singleton set of scopes`() {
        encode(setOf(Basic)) shouldBe "\"basic\""
    }

    @Test
    fun `should be able to encode a full set of scopes`() {
        encode(setOf(Basic, ProfileRead, ProfileWrite)) shouldBe "\"basic profile::read profile::write\""
    }

    @Test
    fun `should be able to decode an empty set of scopes`() {
        decode("\"\"") should beEmpty()
    }

    @Test
    fun `should be able to decode a singleton set of scopes`() {
        decode("\"basic\"") should containExactly(Basic)
    }

    @Test
    fun `should be able to decode a full set of scopes`() {
        decode("\"basic profile::read profile::write\"") should containExactly(Basic, ProfileRead, ProfileWrite)
    }

    @Test
    fun `should be able to handle decoding an aardvark`() {
        decode("\"aardvark\"") should beEmpty()
    }
}