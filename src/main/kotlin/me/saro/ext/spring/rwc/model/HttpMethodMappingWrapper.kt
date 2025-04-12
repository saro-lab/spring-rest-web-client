package me.saro.ext.spring.rwc.model

import me.saro.ext.spring.rwc.RestWebClientMethodProxy
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import java.lang.reflect.Method

class HttpMethodMappingWrapper(
    val method: Method,
    environmentWrapper: EnvironmentWrapper,
) {
    val httpMethod: HttpMethod
    val path: String
    val staticHeaders: Map<String, String>
    val dynamicHeaders: Map<String, String>

    init {
        val className = method.declaringClass.name
        var httpMethod: HttpMethod = HttpMethod.GET
        var path: Array<String> = emptyArray()
        var consumes: Array<String> = emptyArray()
        var headers: Array<String> = emptyArray()
        val staticHeaders: MutableMap<String, String> = mutableMapOf()
        val dynamicHeaders: MutableMap<String, String> = mutableMapOf()
        method.annotations.filter {
            when (it) {
                is RequestMapping -> { throw IllegalArgumentException("RequestMapping is not allowed. Use @[Get/Post/Put/Delete/Patch]Mapping instead.") }
                is GetMapping -> {
                    httpMethod = HttpMethod.GET; path = it.value + it.path; consumes = it.consumes; headers = it.headers
                }
                is PostMapping -> {
                    httpMethod = HttpMethod.POST; path = it.value + it.path; consumes = it.consumes; headers = it.headers
                }
                is PutMapping -> {
                    httpMethod = HttpMethod.PUT; path = it.value + it.path; consumes = it.consumes; headers = it.headers
                }
                is PatchMapping -> {
                    httpMethod = HttpMethod.PATCH; path = it.value + it.path; consumes = it.consumes; headers = it.headers
                }
                is DeleteMapping -> {
                    httpMethod = HttpMethod.DELETE; path = it.value + it.path; consumes = it.consumes; headers = it.headers
                }
                else -> return@filter false
            }
            true
        }.run {
            if (isEmpty()) {
                throw IllegalArgumentException("No HTTP method mapping annotation found: $className.${method.name}")
            } else if (size > 1) {
                throw IllegalArgumentException("Only one @RequestMapping is allowed: $className.${method.name}")
            }
        }

        consumes
            .also { if (it.size > 1) { throw IllegalArgumentException("@RequestMapping has just one consume: $className.${method.name}") } }
            .takeIf { it.isNotEmpty() }
            ?.let { environmentWrapper.replacePattern(it.first()) }
            ?.also {
                if (RestWebClientMethodProxy.hasVarPattern(it)) {
                    dynamicHeaders[HttpHeaders.ACCEPT] = it
                } else {
                    staticHeaders[HttpHeaders.ACCEPT] = it
                }
            }

        headers
            .map { it.split("=") }
            .onEach { if (it.size != 2) throw IllegalArgumentException("header format must key=value: $className.${method.name}") }
            .forEach {
                val k = environmentWrapper.replacePattern(it[0].trim())
                val v = environmentWrapper.replacePattern(it[1].trim())
                if (RestWebClientMethodProxy.hasVarPattern(k+v)) {
                    dynamicHeaders[k] = v
                } else {
                    staticHeaders[k] = v
                }
            }

        this.httpMethod = httpMethod
        this.path = path
            .also { if (it.size != 1) { throw IllegalArgumentException("@RequestMapper need one path/value: $className.${method.name}") } }
            .run { environmentWrapper.replacePattern(first()) }
        this.staticHeaders = staticHeaders
        this.dynamicHeaders = dynamicHeaders
    }
}
