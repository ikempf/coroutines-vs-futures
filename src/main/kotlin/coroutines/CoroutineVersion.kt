package coroutines

import common.Person

object PersonServiceC {
    suspend fun findPerson(id: String): Person {
        return PersonRepositoryC.findPerson(id)
    }

    suspend fun findPersons(ids: List<String>): List<Person> {
        return ids.map { id -> PersonRepositoryC.findPerson(id) }
    }
}

object PersonRepositoryC {
    suspend fun findPerson(id: String): Person {
        return Person(id, "John", "Doe", 35)
    }
}