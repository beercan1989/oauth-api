package uk.co.baconi.session

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SpaceDelimitedSerializer : KSerializer<Set<String>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SpaceDelimited", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Set<String> = decoder.decodeString().split(" ").toSet()
    override fun serialize(encoder: Encoder, value: Set<String>) = encoder.encodeString(value.joinToString(" "))
}