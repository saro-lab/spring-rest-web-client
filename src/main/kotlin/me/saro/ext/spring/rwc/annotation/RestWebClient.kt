package me.saro.ext.spring.rwc.annotation

import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class RestWebClient(
    val uri: String,
    val environmentAliases: Array<String> = [],
)
