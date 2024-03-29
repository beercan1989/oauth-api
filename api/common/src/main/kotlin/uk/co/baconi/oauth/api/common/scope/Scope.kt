package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.Serializable

@Serializable(with = ScopeSerializer::class)
data class Scope(internal val value: String)