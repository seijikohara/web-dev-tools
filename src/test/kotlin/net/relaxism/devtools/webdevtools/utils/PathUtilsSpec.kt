package net.relaxism.devtools.webdevtools.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class PathUtilsSpec :
    FunSpec({

        test("concatenate should handle various path combinations") {
            forAll(
                row(arrayOf<String>(), "", "empty paths"),
                row(arrayOf("/"), "/", "single root path"),
                row(arrayOf("api"), "api", "single path without slash"),
                row(arrayOf("/api"), "/api", "single path with leading slash"),
                row(arrayOf("api/"), "api/", "single path with trailing slash"),
                row(arrayOf("api", "v1"), "api/v1", "two simple paths"),
                row(arrayOf("/api", "v1"), "/api/v1", "paths with leading slash"),
                row(arrayOf("api", "v1/"), "api/v1/", "paths with trailing slash"),
                row(arrayOf("/api/", "/path"), "/api/path", "user example 1: /api/, /path -> /api/path"),
                row(arrayOf("api/", "/", "/path"), "api/path", "user example 2: api/, /, /path -> api/path"),
                row(arrayOf("api", "path/"), "api/path/", "user example 3: api, path/ -> api/path/"),
                row(arrayOf("/", "api", "v1"), "/api/v1", "root path with other paths"),
                row(arrayOf("api//v1", "users///123"), "api/v1/users/123", "multiple consecutive slashes"),
                row(arrayOf("/api/", "/v1/", "/users/"), "/api/v1/users/", "paths with both leading and trailing slashes"),
                row(arrayOf("/api", "v1/", "users", "/123/"), "/api/v1/users/123/", "mixed path styles"),
            ) { paths, expected, description ->
                PathUtils.concatenate(*paths) shouldBe expected
            }
        }

        test("concatenate should handle empty strings in paths") {
            PathUtils.concatenate("api", "", "v1") shouldBe "api/v1"
        }
    })
