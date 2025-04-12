package me.saro.ext.spring.rwc

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(RestWebClientRegistrar::class)
annotation class EnableRestWebClient(
    val basePackages: Array<String> = [],
)
