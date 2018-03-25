import coroutines.PersonServiceC
import futures.PersonServiceF
import kotlinx.coroutines.experimental.CommonPool
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine


fun main(args: Array<String>) {
    println("------------------------")
    // Basic async
    findPersonTest()

    // Multiple async operations
    findPersonsTest()
    println("------------------------")
}

fun findPersonTest() {
    showF("Basic Future", { PersonServiceF.findPerson("personId") })

    showC("Basic Coroutines", { PersonServiceC.findPerson("personId") })
}

fun findPersonsTest() {
    showF("Multiple Futures", { PersonServiceF.findPersons(listOf("personId1", "personId2", "personId3", "personId4")) })

    showC("Multiple Coroutines", { PersonServiceC.findPersons(listOf("personId1", "personId2", "personId3", "personId4")) })
    showC("Multiple Coroutines aync", { PersonServiceC.findPersonsParallel(listOf("personId1", "personId2", "personId3", "personId4")) })
}

fun <A> showF(name: String, coroutine: () -> CompletableFuture<A>) {
    show(name, { coroutine().get() })
}

fun <A> showC(name: String, coroutine: suspend () -> A) {
    show(name, { CompletableFutureCoroutine(CommonPool, coroutine).get() })
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

    override fun resume(value: T) {
        complete(value)
    }

    override fun resumeWithException(exception: Throwable) {
        completeExceptionally(exception)
    }
}