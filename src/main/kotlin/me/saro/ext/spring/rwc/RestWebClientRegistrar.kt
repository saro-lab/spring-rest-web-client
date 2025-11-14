package me.saro.ext.spring.rwc

import me.saro.ext.spring.rwc.model.EnvironmentWrapper
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.env.Environment
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.classreading.MetadataReader
import java.util.stream.Stream


class RestWebClientRegistrar(
    environment: Environment,
): ImportBeanDefinitionRegistrar {

    private val environmentWrapper = EnvironmentWrapper(environment)
    private val classProvider = ClassPathScanningCandidateComponentProvider(false)
    private val metadataReaderFactory = classProvider.metadataReaderFactory

    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        val config: MutableMap<String, Any?> = importingClassMetadata.getAnnotationAttributes(EnableRestWebClient::class.java.name)
            ?: return

        @Suppress("UNCHECKED_CAST")
        val basePackages: Array<String> = (config["basePackages"] as Array<String>)
            .map { it.replace(".", "/") }
            .distinct()
            .let { it.ifEmpty { listOf("") } }
            .toTypedArray()

        findAllRestWebClient(registry, basePackages)
            .map { RestWebClientProxy.of(it, environmentWrapper) }
            .forEach { proxy -> registry.registerBeanDefinition(proxy.beanName(), proxy.beanDefinition) }
    }

    private fun findAllRestWebClient(registry: BeanDefinitionRegistry, basePackages: Array<String>): List<MetadataReader> {
        val resourceLoader = PathMatchingResourcePatternResolver()
        val resourceNameSet: MutableSet<String> = HashSet()

        return Stream.of(*basePackages)
            .parallel()
            .flatMap { basePackage -> Stream.of(*resourceLoader.getResources("classpath*:$basePackage/**/*.class")) }
            .filter { it.exists() && it.isFile }
            .sequential()
            // I don't know, it is thread-safe: change to sequential
            .map { resource -> metadataReaderFactory.getMetadataReader(resource) }
            // not registered RestWebClient interfaces
            .filter { metadata ->
                metadata.classMetadata.isInterface
                        && !registry.isBeanNameInUse(metadata.classMetadata.className)
                        && metadata.annotationMetadata.hasAnnotation(RestWebClient::class.java.name)
            }
            // distinct className
            .filter { metadata ->
                if (resourceNameSet.contains(metadata.classMetadata.className)) {
                    return@filter false
                }
                resourceNameSet.add(metadata.classMetadata.className)
                true
            }
            .toList()
    }
}
