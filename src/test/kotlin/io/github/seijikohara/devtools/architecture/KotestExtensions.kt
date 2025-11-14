package io.github.seijikohara.devtools.architecture

import io.kotest.core.test.TestScope

/**
 * Kotest extension to get the test name for use with Konsist's testName parameter.
 *
 * This extension provides a convenient way to pass Kotest test names to Konsist assertions,
 * enabling better error messages that include the test name in validation failures.
 *
 * Usage:
 * ```
 * test("should have correct naming") {
 *     Konsist.scopeFromProject()
 *         .classes()
 *         .assertTrue(testName = koTestName) { ... }
 * }
 * ```
 */
val TestScope.koTestName: String
    get() = this.testCase.name.name
