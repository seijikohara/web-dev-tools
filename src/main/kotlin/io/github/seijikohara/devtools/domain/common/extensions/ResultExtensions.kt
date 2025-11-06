package io.github.seijikohara.devtools.domain.common.extensions

/**
 * Transforms a list of Result values into a Result of a list.
 *
 * If all results are successful, returns a successful Result containing the list of values.
 * If any result is a failure, returns the first failure encountered.
 *
 * This function is useful for composing multiple Result-producing operations
 * in a functional programming style.
 *
 * @receiver List of Result values to sequence
 * @return Result containing list of values if all succeeded, or first failure
 */
fun <T> List<Result<T>>.sequence(): Result<List<T>> =
    fold(Result.success(emptyList())) { acc, result ->
        acc.flatMap { list ->
            result.map { item -> list + item }
        }
    }

/**
 * Flat maps a Result value using the given transform function.
 *
 * If the Result is successful, applies the transform function to the value.
 * If the Result is a failure, returns the failure.
 *
 * @receiver The Result to flat map
 * @param transform Function that transforms the value to a new Result
 * @return Result from the transform function, or the original failure
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(
        onSuccess = transform,
        onFailure = { Result.failure(it) },
    )
