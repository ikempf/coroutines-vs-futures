package futures

import common.Person
import org.funktionale.either.Either
import org.funktionale.option.Option
import java.util.Random
import java.util.concurrent.CompletableFuture


object PersonServiceF {
    fun findPerson(id: String): CompletableFuture<Person> {
        return PersonRepositoryF.findPerson(id)
    }

    // Executed in parallel since CompletableFutures are not lazy
    // If they were to be lazy it would still be simple to parallelize (Future.sequence(...))
    fun findPersons(ids: List<String>): CompletableFuture<List<Person>> {
        val eventualPersons = ids.map(PersonRepositoryF::findPerson)

        // No sequence, unsafe crappy java code
        return CompletableFuture.allOf(*eventualPersons.toTypedArray())
                .thenApply { _ ->
                    eventualPersons.map(CompletableFuture<Person>::join)
                }
    }

    fun findPersonRetry(id: String, maxTries: Int = 3): CompletableFuture<Option<Person>> {
        return if (maxTries <= 0)
            CompletableFuture.completedFuture(Option.None)
        else
            PersonRepositoryF.tryFindPerson(id)
                    .thenCompose { either ->
                        either.fold(
                                { this.findPersonRetry(id, maxTries - 1) },
                                { CompletableFuture.completedFuture(Option.Some(it)) }
                        )
                    }
    }
}

object PersonRepositoryF {
    fun findPerson(id: String): CompletableFuture<Person> {
        return CompletableFuture.supplyAsync {
            Thread.sleep(1000)
            Person(id, "John", "Doe", 35)
        }
    }

    fun tryFindPerson(id: String): CompletableFuture<Either<String, Person>> {
        return if (Random().nextBoolean())
            findPerson(id).thenApply { Either.right(it) }
        else
            CompletableFuture.completedFuture(Either.left("failed"))
    }
}