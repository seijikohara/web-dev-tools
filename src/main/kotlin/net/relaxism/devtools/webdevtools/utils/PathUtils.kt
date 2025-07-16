package net.relaxism.devtools.webdevtools.utils

object PathUtils {
    fun concatenate(vararg paths: String): String =
        paths
            .takeIf { it.isNotEmpty() }
            ?.let { pathArray ->
                val hasLeadingSlash = pathArray.first().startsWith('/')
                val hasTrailingSlash = pathArray.last().endsWith('/')

                pathArray
                    .asSequence()
                    .map { it.replace(Regex("/+"), "/") }
                    .filter { it.isNotEmpty() }
                    .map { it.trim('/') }
                    .filter { it.isNotEmpty() }
                    .toList()
                    .takeIf { it.isNotEmpty() }
                    ?.joinToString("/")
                    ?.let { joinedPath ->
                        listOfNotNull(
                            "/".takeIf { hasLeadingSlash },
                            joinedPath,
                            "/".takeIf { hasTrailingSlash },
                        ).joinToString("")
                    } ?: if (hasLeadingSlash) "/" else ""
            } ?: ""
}
