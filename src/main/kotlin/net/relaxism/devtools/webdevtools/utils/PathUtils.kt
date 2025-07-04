package net.relaxism.devtools.webdevtools.utils

object PathUtils {
    fun concatenate(vararg paths: String): String {
        if (paths.isEmpty()) return ""

        val cleanPaths = paths.map { it.trim('/') }
        val result = cleanPaths.joinToString("/")

        return buildString {
            if (paths.first().startsWith('/')) append('/')
            append(result)
            if (paths.last().endsWith('/')) append('/')
        }
    }
}
