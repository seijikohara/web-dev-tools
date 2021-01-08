package net.relaxism.devtools.webdevtools.utils

object PathUtils {

    fun concatenate(vararg paths: String): String {
        if (paths.isEmpty()) {
            return "";
        }

        val hasSlashHead = paths[0].startsWith('/')
        val hasSlashTail = paths[paths.size - 1].endsWith('/')
        return (if (hasSlashHead) "/" else "") +
            paths.joinToString("/") { it.trim('/') } +
            (if (hasSlashTail) "/" else "")

    }

}
