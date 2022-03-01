package net.relaxism.devtools.webdevtools.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PathUtilsSpec : StringSpec() {

    init {
        "concatenate" {
            PathUtils.concatenate() shouldBe ""
            PathUtils.concatenate("a", "b", "c") shouldBe "a/b/c"
            PathUtils.concatenate("/a", "/b", "/c") shouldBe "/a/b/c"
            PathUtils.concatenate("a/", "b/", "c/") shouldBe "a/b/c/"
            PathUtils.concatenate("/a/", "/b/", "/c/") shouldBe "/a/b/c/"
        }
    }

}
