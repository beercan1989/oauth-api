package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Supports only serialisation as we don't intend for the Kotlin/JVM to deserialise a request
 * containing a pre-authenticated username.
 */
actual object AuthenticatedUsernameSerializer : KSerializer<AuthenticatedUsername> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuthenticatedUsername", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: AuthenticatedUsername) {
        when {
            value.value.isBlank() -> throw SerializationException("Invalid authenticated username [${value.value}]")
            value.value.trim() != value.value ->  throw SerializationException("Invalid authenticated username [${value.value}]")
            else -> encoder.encodeString(value.value)
        }
    }

    override fun deserialize(decoder: Decoder): AuthenticatedUsername {
        throw SerializationException("Deserialize unsupported for AuthenticatedUsername in Kotlin/JVM")
    }
}