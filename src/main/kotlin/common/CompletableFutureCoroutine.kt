package common

import java.util.concurrent.CompletableFuture
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine

class CompletableFutureCoroutine<T>(override val context: CoroutineContext, coroutine: suspend () -> T) : CompletableFuture<T>(), Continuation<T> {
    init {
        coroutine.startCoroutine(this)
    }

    override fun resume(value: T) {
        complete(value)
    }

    override fun resumeWithException(exception: Throwable) {
        completeExceptionally(exception)
    }
}