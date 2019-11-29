package com.sbgcore.oauth.api.wellknown

import kotlinx.serialization.Serializable

@Serializable
data class ProductConfiguration(val products: List<Product>)