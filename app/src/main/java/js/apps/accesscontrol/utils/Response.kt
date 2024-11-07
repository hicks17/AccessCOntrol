package js.apps.accesscontrol.utils

import java.lang.Exception

sealed class Response <out R>{

    data class Success<out T>(val data: T): Response<T>()
    data class Failed(val exception: Exception): Response<Nothing>()
    data object Loading: Response<Nothing>()
    data object Finish: Response<Nothing>()

}
