package js.apps.accesscontrol.network

import js.apps.accesscontrol.model.Alumno
import js.apps.accesscontrol.model.Entrance

interface UserRepository {

    suspend fun createUser(user: Alumno)

    suspend fun getUser(userId: String): Alumno

    suspend fun updateUser(user: Alumno)

    suspend fun deleteUser(userId: String)

    suspend fun getLastEntrancesByUserID(userId: String): List<Entrance>

    suspend fun getLastEntrance():List<Entrance>


    suspend fun authorization(matricula: String): Boolean
}