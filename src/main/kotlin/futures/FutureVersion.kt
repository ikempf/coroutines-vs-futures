package futures

import common.Person
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
}

object PersonRepositoryF {
    fun findPerson(id: String): CompletableFuture<Person> {
        return CompletableFuture.supplyAsync{
            Thread.sleep(1000)
            Person(id, "John", "Doe", 35)
        }
    }
}