package net.relaxism.devtools.webdevtools.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class PathUtilsSpec : StringSpec() {
    init {
        "concatenate" {
            forAll(
                table(
                    headers("value", "expected"),
                    row(emptyArray(), ""),
                    row(arrayOf("a", "b", "c"), "a/b/c"),
                    row(arrayOf("/a", "/b", "/c"), "/a/b/c"),
                    row(arrayOf("a/", "b/", "c/"), "a/b/c/"),
                    row(arrayOf("/a/", "/b/", "/c/"), "/a/b/c/"),
                ),
            ) { value: Array<String>, expected: String ->
                PathUtils.concatenate(*value) shouldBe expected
            }
        }
    }
}
