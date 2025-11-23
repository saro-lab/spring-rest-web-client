package me.saro.ext.spring.rwc

import me.saro.ext.spring.rwc.annotation.EnableRestWebClient
import me.saro.ext.spring.rwc.annotation.RestWebClient
import me.saro.ext.spring.rwc.wrapper.EnvironmentWrapper
import me.saro.ext.spring.rwc.proxy.RestWebClientProxy
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.classreading.MetadataReader
import java.util.stream.Stream


class RestWebClientRegistrar(
    private val environment: Environment,
): ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        Stream.of(*findBasePackages(importingClassMetadata))
            .toResources()
            .toMetadataReader()
            .filterUnRegistrarInterface(registry)
            .filterRestWebClient()
            .toRestWebClientProxy(environment)
            .registerBean(registry)
    }

    companion object {

        private val log: Log = LogFactory.getLog(RestWebClientRegistrar::class.java)

        @Suppress("UNCHECKED_CAST")
        fun findBasePackages(importingClassMetadata: AnnotationMetadata): Array<String> =
            importingClassMetadata
                .getAnnotationAttributes(EnableRestWebClient::class.java.name)
                ?.run { this["basePackages"] as Array<String> }
                ?.also { if (it.isEmpty()) { log.warn("does not defined basePackages in @EnableRestWebClient") } }
                ?: emptyArray()

        fun Stream<String>.toResources(): Stream<Resource> {
            val resourceLoader = PathMatchingResourcePatternResolver()
            val regexBasePackage = Regex("^[a-z][a-z\\d.*]*$", RegexOption.IGNORE_CASE)

            return this.flatMap { basePackage ->
                if (regexBasePackage.matches(basePackage)) {
                    Stream.of(*resourceLoader.getResources("classpath*:${basePackage.replace(".", "/")}/**/*.class"))
                } else {
                    throw IllegalArgumentException("$basePackage is invalid basePackage in @EnableRestWebClient")
                }
            }.distinct()
        }

        fun Stream<Resource>.toMetadataReader(): Stream<MetadataReader> {
            val metadataReaderFactory = ClassPathScanningCandidateComponentProvider(false).metadataReaderFactory

            return this.filter { it.exists() && it.isFile }
                .distinct()
                .map { resource -> metadataReaderFactory.getMetadataReader(resource) }
        }

        fun Stream<MetadataReader>.filterUnRegistrarInterface(registry: BeanDefinitionRegistry): Stream<MetadataReader> =
            this.filter { metadata ->
                metadata.classMetadata.isInterface && !registry.isBeanNameInUse(metadata.classMetadata.className)
            }

        fun Stream<MetadataReader>.filterRestWebClient(): Stream<MetadataReader> {
            val annotationName = RestWebClient::class.java.name
            return this.filter { metadata -> metadata.annotationMetadata.hasAnnotation(annotationName) }
        }

        fun Stream<MetadataReader>.toRestWebClientProxy(environment: Environment): Stream<RestWebClientProxy> {
            val environmentWrapper = EnvironmentWrapper(environment)
            return this.map { RestWebClientProxy.of(it, environmentWrapper) }
        }

        fun Stream<RestWebClientProxy>.registerBean(registry: BeanDefinitionRegistry) {
            this.forEach { proxy -> registry.registerBeanDefinition(proxy.beanName(), proxy.beanDefinition) }
        }
    }
}
