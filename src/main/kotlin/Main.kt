import common.CompletableFutureCoroutine
import coroutines.PersonServiceC
import futures.PersonServiceF
import kotlinx.coroutines.experimental.CommonPool
import java.util.concurrent.CompletableFuture


fun main(args: Array<String>) {
    println("------------------------")
    // Basic async
    findPersonTest()

    // Multiple async operations
    findPersonsTest()

    // Async with control flow
    findPersonsRetryTest()
    println("------------------------")
}

fun findPersonTest() {
    showF("Basic Future", { PersonServiceF.findPerson("personId") })
    showC("Basic Coroutines", { PersonServiceC.findPerson("personId") })
}

fun findPersonsTest() {
    showF("Multiple Futures", { PersonServiceF.findPersons(listOf("personId1", "personId2", "personId3")) })
    showC("Multiple Coroutines", { PersonServiceC.findPersons(listOf("personId1", "personId2", "personId3")) })
    showC("Multiple Coroutines async", { PersonServiceC.findPersonsParallel(listOf("personId1", "personId2", "personId3")) })
}

fun findPersonsRetryTest() {
    showF("Control Future", { PersonServiceF.findPersonRetry("personId1") })
    showC("Control Coroutine", { PersonServiceC.findPersonRetry("personId1") })
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
