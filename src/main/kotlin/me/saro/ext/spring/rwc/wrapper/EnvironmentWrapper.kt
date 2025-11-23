package me.saro.ext.spring.rwc.wrapper

import org.springframework.core.env.Environment

class EnvironmentWrapper(
    private val environment: Environment,
    private val alias: Map<String, String> = emptyMap(),
) {
    fun bindAliases(aliases: Array<String>): EnvironmentWrapper {
        val alias = aliases.map { it.split("=") }
            .onEach {
                if (it.size != 2) {
                    throw IllegalArgumentException("invalid alias format(key=value): $it")
                }
            }
            .associate {
                val k = it[0].trim()
                val v = environment.getProperty(it[1].trim())?.trim()
                    ?: throw IllegalArgumentException("Environment variable not found: ${it[1]}")
                it[0].trim() to v
            }
        return EnvironmentWrapper(environment, alias)
    }

    fun replacePattern(value: String): String {
        return regexSpringValue.replace(value) { matchResult ->
            val key = matchResult.groupValues[1]
            alias[key] ?: environment.getProperty(key)
                ?: throw IllegalArgumentException("Environment variable not found: $key")
        }
    }

    fun get(key: String): String? {
        return alias[key] ?: environment.getProperty(key)
    }

    companion object {
        private val regexSpringValue = Regex("\\$\\{(.*?)}")
    }
}
