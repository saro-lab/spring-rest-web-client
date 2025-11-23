package me.saro.ext.spring.rwc.proxy

import me.saro.ext.spring.rwc.annotation.RestWebClient
import me.saro.ext.spring.rwc.wrapper.EnvironmentWrapper
import me.saro.ext.spring.rwc.wrapper.HttpMethodMappingWrapper
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.core.type.classreading.MetadataReader
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.function.Supplier

class RestWebClientProxy private constructor(
    metadataReader: MetadataReader,
    environmentWrapper: EnvironmentWrapper
): InvocationHandler {
    private val resource = metadataReader.resource
    private val classMetadata = metadataReader.classMetadata
    private val annotationMetadata = metadataReader.annotationMetadata
    private val className: String = classMetadata.className
    private val clazz: Class<*> = Class.forName(className)
    private val instant = Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), this)
    val uri: String

    val beanDefinition: BeanDefinition = GenericBeanDefinition().apply {
            setBeanClass(clazz)
            instanceSupplier = Supplier { instant }
        }
    private val methodProxyMap: Map<Method, RestWebClientMethodProxy>

    init {
        val restWebClient: Map<String, Any?> = annotationMetadata.getAnnotationAttributes(RestWebClient::class.java.name)!!
        @Suppress("UNCHECKED_CAST")
        val envWebClient = environmentWrapper.bindAliases(restWebClient["environmentAliases"] as Array<String>)
        this.uri = envWebClient.replacePattern(restWebClient["uri"] as String)
            .also { if (it.isEmpty()) { throw IllegalArgumentException("@RestWebClient(uri) is empty $className") } }
        this.methodProxyMap = clazz.methods.associateWith { method ->
            RestWebClientMethodProxy(this, HttpMethodMappingWrapper(method, envWebClient))
        }
    }

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        return methodProxyMap[method]?.invoke(proxy, method, args) ?: when (method.name) {
            "toString" -> return toString()
            "hashCode" -> return hashCode()
            "equals" -> equals(proxy)
            else -> null
        }
    }

    override fun hashCode(): Int {
        return className.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return className == other
    }

    override fun toString(): String {
        return className
    }

    fun beanName(): String = className.replace(".", "DOT")

    companion object {
        fun of(
            metadataReader: MetadataReader, environmentWrapper: EnvironmentWrapper
        ): RestWebClientProxy {
            return RestWebClientProxy(metadataReader, environmentWrapper)
        }

    }
}
