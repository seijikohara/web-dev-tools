/**
 * Extension functions for functional programming with the [Result] type.
 */
package io.github.seijikohara.devtools.domain.common.extensions

/**
 * Transforms a list of [Result] values into a [Result] of a list.
 *
 * Returns [Result.success] with all values if all results succeed.
 * Returns the first [Result.failure] if any result fails.
 *
 * @param T The type of values contained in the results
 * @receiver [List] of [Result] values to sequence
 * @return [Result.success] containing list of values if all succeeded,
 *         or the first [Result.failure] if any failed
 */
fun <T> List<Result<T>>.sequence(): Result<List<T>> =
    fold(Result.success(emptyList())) { acc, result ->
        acc.flatMap { list ->
            result.map { item -> list + item }
        }
    }

/**
 * Flat maps a [Result] value using the given transform function.
 *
 * Applies the transform function if the [Result] is successful.
 * Returns the original failure if the [Result] is a failure.
 *
 * @param T The type of the value in the input result
 * @param R The type of the value in the output result
 * @receiver The [Result] to flat map
 * @param transform Function that transforms the value to a new [Result]
 * @return [Result] from the transform function if successful,
 *         or the original [Result.failure] if failed
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(
        onSuccess = transform,
        onFailure = { Result.failure(it) },
    )
