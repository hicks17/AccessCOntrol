package js.apps.accesscontrol.network

import js.apps.accesscontrol.utils.Response
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

        suspend fun logIn(mail:String, pass:String): Flow<Response<Boolean>>

        suspend fun signUp(mail:String, pass:String): Flow<Response<Boolean>>

        suspend fun logOut(): Flow<Response<Boolean>>
}