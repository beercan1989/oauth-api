package com.sbgcore.oauth.api.feeders

import io.gatling.core.session.Expression

object Customers {

  object Expressions {

    val username: Expression[String] = session => session("username").validate[String]

    val password: Expression[String] = session => session("password").validate[String]

  }

  object Feeders {

    private def customer(username: String, password: String) = Map("username" -> username, "password" -> password)

    val customers: Array[Map[String, String]] = Array(
      customer("aardvark", "121212"),
      customer("badger", "212121"),
      customer("elephant", "122112")
    )

  }

}
