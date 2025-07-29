package com.github.seijikohara.devtools.webdevtools.utils

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
                    .filter(String::isNotEmpty)
                    .map { it.trim('/') }
                    .filter(String::isNotEmpty)
                    .toList()
                    .takeIf(List<String>::isNotEmpty)
                    ?.joinToString("/")
                    ?.let { joinedPath ->
                        buildList {
                            if (hasLeadingSlash) add("/")
                            add(joinedPath)
                            if (hasTrailingSlash) add("/")
                        }.joinToString("")
                    } ?: if (hasLeadingSlash) "/" else ""
            } ?: ""
}
