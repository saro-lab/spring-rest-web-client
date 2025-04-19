package me.saro.ext.spring.rwc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.saro.ext.spring.rwc.model.HttpMethodMappingWrapper
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.BindParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyValueWithType
import reactor.core.publisher.Mono
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URLEncoder
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction


class RestWebClientMethodProxy(
    private val restWebClientProxy: RestWebClientProxy,
    private val httpMethodMappingWrapper: HttpMethodMappingWrapper,
) {
    private val log: Log = LogFactory.getLog(RestWebClientProxy::class.java)

    private val method: Method = httpMethodMappingWrapper.method
    private val httpMethod: HttpMethod = httpMethodMappingWrapper.httpMethod
    private val uri: String = restWebClientProxy.uri + httpMethodMappingWrapper.path

    // webClient
    private val webClient: WebClient = WebClient.builder()
        .baseUrl(uri)
        .apply {
            httpMethodMappingWrapper.staticHeaders.forEach { (k, v) -> it.defaultHeader(k, v) }
        }.build()
    private val dynamicHeaders: Map<String, String> = httpMethodMappingWrapper.dynamicHeaders
    private val returnProxy: (WebClient.RequestBodyUriSpec) -> Any
    private val parameterProxy: Array<(WebClient.RequestBodyUriSpec, MutableMap<String, String>, MultiValueMap<String, String>, Any?) -> Unit>
    private val fullName: String = method.declaringClass.name + "." + method.name
    private val formUrlEncoded: Boolean = httpMethodMappingWrapper.formUrlEncoded

    init {
        log.debug("""
$fullName
$httpMethod: $uri
staticHeaders: ${httpMethodMappingWrapper.staticHeaders}
dynamicHeaders: $dynamicHeaders
        """.trimIndent())


        val returnType: KType = method.kotlinFunction!!.returnType
        val returnJavaType = returnType.javaType
        // return
        returnProxy = when {
            returnJavaType is ParameterizedType && returnJavaType.rawType == Mono::class.java -> {
                val returnMonoType: Type = returnType.arguments.first().type!!.javaType
                val typeReference = object : ParameterizedTypeReference<Any>() {
                    override fun getType(): Type = returnMonoType
                }
                ({ req -> req.retrieve().bodyToMono(typeReference) })
            }
            returnJavaType is WebClient -> throw IllegalArgumentException("Use ReturnType WebClient.RequestBodyUriSpec or WebClient.ResponseSpec instead of WebClient: $fullName")
            returnJavaType is WebClient.RequestBodyUriSpec -> ({ req -> req })
            returnJavaType is WebClient.ResponseSpec -> ({ req -> req.retrieve() })
            else -> {
                val typeReference = object : ParameterizedTypeReference<Any>() {
                    override fun getType(): Type = returnJavaType
                }
                ({ req -> req.retrieve().bodyToMono(typeReference).block()!! })
            }
        }

        // arguments
        parameterProxy = method.parameters.mapIndexed<Parameter, (WebClient.RequestBodyUriSpec, MutableMap<String, String>, MultiValueMap<String, String>, Any?) -> Unit> { index, parameter ->
            val name = parameter.name

            val header = parameter.getAnnotation(RequestHeader::class.java)
            val param = parameter.getAnnotation(RequestParam::class.java)
            val body = parameter.getAnnotation(RequestBody::class.java)
            val bindParam = parameter.getAnnotation(BindParam::class.java)

            if (listOfNotNull(header, param, body).size > 1) {
                throw IllegalArgumentException("Only one of @RequestHeader, @RequestParam, @RequestBody, @BindParam is allowed: $fullName.${method.name}.$name")
            }

            if (header != null) {
                { req, vars, params, arg ->
                    if (arg != null || header.required) {
                        val hName = listOfNotNull(header.name, header.value, name).first(String::isNotEmpty)
                        req.headers { h -> h.set(hName, arg?.toString() ?: header.defaultValue) }
                    }
                }
            } else if (param != null) {
                { req, vars, params, arg ->
                    if (arg != null || param.required) {
                        val hName = listOfNotNull(param.name, param.value, name).first(String::isNotEmpty)
                        val value = listOfNotNull(arg, param.defaultValue).first()
                        when {
                            isBasicType(value) -> params.add(hName, value.toString())
                            value is Array<*> -> params.addAll(hName, value.map { it.toString() })
                            value is Collection<*> -> params.addAll(hName, value.map { it.toString() })
                            else -> {
                                mapper.convertValue(value, trMap).forEach { (k, v) ->
                                    when (v) {
                                        is Array<*> -> params.addAll(k, v.map { it.toString() })
                                        is Collection<*> -> params.addAll(k, v.map { it.toString() })
                                        else -> params.add(k, v.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (body != null) {
                { req, vars, params, arg -> if (arg != null) { req.bodyValueWithType(arg) } }
            } else {
                val bindName = bindParam?.value ?: name
                { req, vars, params, arg -> arg?.also { vars[bindName] = it.toString() } }
            }
        }.toTypedArray()
    }


    fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        val client = webClient
            .method(httpMethod)

        val params: MultiValueMap<String, String> = MultiValueMap.fromMultiValue(mutableMapOf<String, List<String>>())
        val vars = mutableMapOf<String, String>()
        args?.forEachIndexed { index, arg -> parameterProxy[index](client, vars, params, arg) }

        if (formUrlEncoded) {
            if (params.isNotEmpty()) {
                val body: String = params.map { (k, v) ->
                    val ek = URLEncoder.encode(k, Charsets.UTF_8)
                    v.joinToString("&") { vi -> ek + '=' + URLEncoder.encode(vi, Charsets.UTF_8) }
                }.joinToString("&")
                client.bodyValue(body)
            }
        }

        client.uri {
            if (!formUrlEncoded) {
                it.queryParams(params)
            }
            it.build(vars)
        }

        if (dynamicHeaders.isNotEmpty()) {
            client.headers { h -> dynamicHeaders.forEach { (k, v) -> h.set(k, vars[v] ?: v) } }
        }

        return returnProxy(client)
    }



    companion object {
        private val varPattern = Regex("\\{(.*?)}")
        private val mapper = ObjectMapper()
        private val trMap = object : TypeReference<Map<String, Any>>() {}

        fun hasVarPattern(value: String): Boolean = varPattern.containsMatchIn(value)

        fun isBasicType(type: Any): Boolean =
            when (type) {
                is Byte, is Short, is Int, is Long,
                is UByte, is UShort, is UInt, is ULong,
                is Float, is Double,
                is Char, is Boolean,
                is String -> true
                else -> false
            }
    }
}
