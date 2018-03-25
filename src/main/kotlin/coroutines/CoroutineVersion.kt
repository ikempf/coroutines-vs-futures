package coroutines

import common.Person
import futures.PersonRepositoryF
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

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
        return coroutines.map{ it.await() }
    }
}

object PersonRepositoryC {
    suspend fun findPerson(id: String): Person {
        delay(1000)
        return Person(id, "John", "Doe", 35)
    }

    suspend fun findPersonBis(id: String): Person {
        val f = PersonRepositoryF.findPerson(id)

        return suspendCoroutine { cont: Continuation<Person> ->
            f.whenComplete { result, exception ->
                if (exception == null)
                    cont.resume(result)
                else
                    cont.resumeWithException(exception)
            }
        }
    }

}