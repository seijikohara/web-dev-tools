package net.relaxism.devtools.webdevtools.utils

object PathUtils {
    fun concatenate(vararg paths: String): String =
        when {
            paths.isEmpty() -> ""
            else ->
                buildString {
                    val cleanPaths = paths.map { it.trim('/') }
                    if (paths.first().startsWith('/')) append('/')
                    append(cleanPaths.joinToString("/"))
                    if (paths.last().endsWith('/')) append('/')
                }
        }
}
