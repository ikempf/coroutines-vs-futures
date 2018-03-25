import coroutines.PersonServiceC
import futures.PersonServiceF
import kotlinx.coroutines.experimental.CommonPool
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine


fun main(args: Array<String>) {
    findPersonTest()
    findPersonsTest()
}


fun findPersonTest() {
    showF({ PersonServiceF.findPerson("personId") })

    showC({ PersonServiceC.findPerson("personId") })
}

fun findPersonsTest() {
    showF({ PersonServiceF.findPersons(listOf("personId1", "personId2", "personId3")) })

    showC({ PersonServiceC.findPerson("personId") })
}

fun <A> showF(coroutine: () -> CompletableFuture<A>) {
    show("Future", { coroutine().get() })
}

fun <A> showC(coroutine: suspend () -> A) {
    show("Coroutine", { CompletableFutureCoroutine<A>(CommonPool, coroutine).get() })
}

fun <A> show(type: String, block: () -> A) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    println("$type ${end - start} ms")

}

class CompletableFutureCoroutine<T>(override val context: CoroutineContext, coroutine: suspend () -> T) : CompletableFuture<T>(), Continuation<T> {
    init {
        coroutine.startCoroutine(this)
    }
    override fun resume(value: T) { complete(value) }
    override fun resumeWithException(exception: Throwable) { completeExceptionally(exception) }
}