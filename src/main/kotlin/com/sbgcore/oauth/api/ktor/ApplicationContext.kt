package com.sbgcore.oauth.api.ktor

import io.ktor.application.*
import io.ktor.util.pipeline.*

typealias ApplicationContext = PipelineContext<*, ApplicationCall>