package com.thelittlefireman.appkillermanager

sealed class Either<out A, out B> {
    inline fun <reified A> identity(a: A): A = a

    /**
     * Returns `true` if this is a [Right], `false` otherwise.
     * Used only for performance instead of fold.
     */
    internal abstract val isRight: Boolean

    /**
     * Returns `true` if this is a [Left], `false` otherwise.
     * Used only for performance instead of fold.
     */
    internal abstract val isLeft: Boolean

    fun isLeft(): Boolean = isLeft

    fun isRight(): Boolean = isRight

    /**
     * Applies `ifLeft` if this is a [Left] or `ifRight` if this is a [Right].
     *
     * Example:
     * ```
     * val result: Either<Exception, Value> = possiblyFailingOperation()
     * result.fold(
     *      { log("operation failed with $it") },
     *      { log("operation succeeded with $it") }
     * )
     * ```
     *
     * @param ifLeft the function to apply if this is a [Left]
     * @param ifRight the function to apply if this is a [Right]
     * @return the results of applying the function
     */
    inline fun <C> fold(ifLeft: (A) -> C, ifRight: (B) -> C): C = when (this) {
        is Right -> ifRight(b)
        is Left -> ifLeft(a)
    }

    /**
     * If this is a `Left`, then return the left value in `Right` or vice versa.
     *
     * Example:
     * ```
     * Left("left").swap()   // Result: Right("left")
     * Right("right").swap() // Result: Left("right")
     * ```
     */
    fun swap(): Either<B, A> = fold({ Right(it) }, { Left(it) })

    /**
     * The given function is applied if this is a `Right`.
     *
     * Example:
     * ```
     * Right(12).map { "flower" } // Result: Right("flower")
     * Left(12).map { "flower" }  // Result: Left(12)
     * ```
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <C> map(f: (B) -> C): Either<A, C> =
            flatMap { Right(f(it)) }

    /**
     * The given function is applied if this is a `Left`.
     *
     * Example:
     * ```
     * Right(12).mapLeft { "flower" } // Result: Right(12)
     * Left(12).mapLeft { "flower" }  // Result: Left("flower)
     * ```
     */
    inline fun <C> mapLeft(f: (A) -> C): Either<C, B> =
            fold({ Left(f(it)) }, { Right(it) })

    /**
     * Map over Left and Right of this Either
     */
    inline fun <C, D> bimap(leftOperation: (A) -> C, rightOperation: (B) -> D): Either<C, D> =
            fold({ Left(leftOperation(it)) }, { Right(rightOperation(it)) })

    /**
     * Returns `false` if [Left] or returns the result of the application of
     * the given predicate to the [Right] value.
     *
     * Example:
     * ```
     * Right(12).exists { it > 10 } // Result: true
     * Right(7).exists { it > 10 }  // Result: false
     *
     * val left: Either<Int, Int> = Left(12)
     * left.exists { it > 10 }      // Result: false
     * ```
     */
    inline fun exists(predicate: (B) -> Boolean): Boolean =
            fold({ false }, { predicate(it) })

    /**
     * The left side of the disjoint union, as opposed to the [Right] side.
     */
    @Suppress("DataClassPrivateConstructor")
    data class Left<out A> @PublishedApi internal constructor(val a: A) : Either<A, Nothing>() {
        override val isLeft
            get() = true
        override val isRight
            get() = false

        companion object {
            operator fun <A> invoke(a: A): Either<A, Nothing> = Left(a)
        }
    }

    /**
     * The right side of the disjoint union, as opposed to the [Left] side.
     */
    @Suppress("DataClassPrivateConstructor")
    data class Right<out B> @PublishedApi internal constructor(val b: B) : Either<Nothing, B>() {
        override val isLeft = false
        override val isRight = true

        companion object {
            operator fun <B> invoke(b: B): Either<Nothing, B> = Right(b)
        }
    }

    companion object {
        fun <L> left(left: L): Either<L, Nothing> = Left(left)

        fun <R> right(right: R): Either<Nothing, R> = Right(right)
    }
}

/**
 * Binds the given function across [Either.Right].
 *
 * @param f The function to bind across [Either.Right].
 */
inline fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
        when (this) {
            is Either.Right -> f(b)
            is Either.Left -> this
        }

/**
 * Returns the value from this [Either.Right] or the given argument if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).getOrElse(17) // Result: 12
 * Left(12).getOrElse(17)  // Result: 17
 * ```
 */
inline fun <reified B> Either<*, B>.getOrElse(default: () -> B): B =
        fold({ default() }, ::identity)

/**
 * Returns the value from this [Either.Right] or null if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).orNull() // Result: 12
 * Left(12).orNull()  // Result: null
 * ```
 */
inline fun <reified B> Either<*, B>.orNull(): B? =
        getOrElse { null }

/**
 * Returns the value from this [Either.Right] or allows clients to transform [Either.Left] to [Either.Right] while providing access to
 * the value of [Either.Left].
 *
 * Example:
 * ```
 * Right(12).getOrHandle { 17 } // Result: 12
 * Left(12).getOrHandle { it + 5 } // Result: 17
 * ```
 */
inline fun <reified A, reified B> Either<A, B>.getOrHandle(default: (A) -> B): B =
        fold({ default(it) }, ::identity)

/**
 * * Returns [Either.Right] with the existing value of [Either.Right] if this is a [Either.Right] and the given
 * predicate holds for the right value.
 * * Returns `Left(default({right}))` if this is a [Either.Right] and the given predicate does not
 * hold for the right value. Useful for error handling where 'default' returns a message with context on why the value
 * did not pass the filter
 * * Returns [Either.Left] with the existing value of [Either.Left] if this is a [Either.Left].
 *
 * Example:
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * import arrow.core.*
 *
 * Right(12).filterOrOther({ it > 10 }, { -1 })
 * ```
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * Right(7).filterOrOther({ it > 10 }, { "Value '$it' not greater than 10" })
 * ```
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * val left: Either<Int, Int> = Left(12)
 * left.filterOrOther({ it > 10 }, { -1 })
 * ```
 */
inline fun <A, B> Either<A, B>.filterOrOther(predicate: (B) -> Boolean, default: (B) -> A): Either<A, B> =
        flatMap {
            if (predicate(it)) Either.Right(it)
            else Either.Left(default(it))
        }

/**
 * * Returns [Either.Right] with the existing value of [Either.Right] if this is an [Either.Right] with a non-null value.
 * The returned Either.Right type is not nullable.
 * * Returns `Left(default())` if this is an [Either.Right] and the existing value is null
 * * Returns [Either.Left] with the existing value of [Either.Left] if this is an [Either.Left].
 *
 * Example:
 * ```
 * Right(12).leftIfNull({ -1 })   // Result: Right(12)
 * Right(null).leftIfNull({ -1 }) // Result: Left(-1)
 *
 * Left(12).leftIfNull({ -1 })    // Result: Left(12)
 * ```
 */
inline fun <A, B> Either<A, B?>.leftIfNull(crossinline default: () -> A): Either<A, B> =
        flatMap { it.rightIfNotNull { default() } }


fun <A> A.left(): Either<A, Nothing> = Either.Left(this)

fun <A> A.right(): Either<Nothing, A> = Either.Right(this)

/**
 * Returns [Either.Right] if the value of type B is not null, otherwise the specified A value wrapped into an
 * [Either.Left].
 *
 * Example:
 * ```
 * "value".rightIfNotNull { "left" } // Right(b="value")
 * null.rightIfNotNull { "left" }    // Left(a="left")
 * ```
 */
inline fun <A, B> B?.rightIfNotNull(default: () -> A): Either<A, B> = when (this) {
    null -> Either.Left(default())
    else -> Either.Right(this)
}