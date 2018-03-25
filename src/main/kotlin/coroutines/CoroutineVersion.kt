package coroutines

import common.Person
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.funktionale.either.Either
import org.funktionale.option.Option
import java.util.Random

object PersonServiceC {
    suspend fun findPerson(id: String): Person {
        return PersonRepositoryC.findPerson(id)
    }

    // Not parallel by default
    suspend fun findPersons(ids: List<String>): List<Person> {
        return ids.map { id -> PersonRepositoryC.findPerson(id) }
    }


    suspend fun findPersonsParallel(ids: List<String>): List<Person> {
        val coroutines = ids.map { id -> async { PersonRepositoryC.findPerson(id) } }
        return coroutines.map { it.await() }
    }

    suspend fun findPersonRetry(id: String, maxTries: Int = 3): Option<Person> {
        return if (maxTries <= 0)
            Option.None
        else {
            val tryFind = PersonRepositoryC.tryFindPerson(id)

            // Cannot use fold since continuation can't be in non suspend callback
            when(tryFind) {
                is Either.Left -> findPersonRetry(id, maxTries - 1)
                is Either.Right -> Option.Some(tryFind.r)
            }
        }
    }
}

object PersonRepositoryC {
    suspend fun findPerson(id: String): Person {
        delay(1000)
        return Person(id, "John", "Doe", 35)
    }

    suspend fun tryFindPerson(id: String): Either<String, Person> {
        return if (Random().nextBoolean())
            Either.right(findPerson(id))
        else
            Either.left("failed")
    }
}