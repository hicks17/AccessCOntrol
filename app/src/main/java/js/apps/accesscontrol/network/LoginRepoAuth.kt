package js.apps.accesscontrol.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import js.apps.accesscontrol.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepoAuth @Inject constructor(
    private val auth: FirebaseAuth
): LoginRepository {

    override suspend fun logIn(mail: String, pass: String) = flow {
        emit(Response.Loading)
        try {

            var response = false

            auth.signInWithEmailAndPassword(mail, pass)
                .addOnSuccessListener { response = true }
                .addOnFailureListener { response = false }
                .await()
            emit(Response.Success(response))

            emit(Response.Finish)
            Log.d("LoginRepoAuth", "logIn: $response")
        } catch (e: Exception) {
            emit(Response.Failed(e))
            emit(Response.Finish)
        }
    }

    override suspend fun signUp(mail: String, pass: String)= flow {
        emit(Response.Loading)
        try {
            var response = false
            lateinit var exception: Exception
            auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    response = true
                    //userInstance.child(auth.currentUser!!.uid).setValue(data)
                } else {
                    exception = task.exception!!
                    response = false
                }
            }
                .await()
            emit(Response.Success(response))
            emit(Response.Finish)
        } catch (e: Exception) {
            emit(Response.Failed(e))
            emit(Response.Finish)
        }
    }

    override suspend fun logOut() = flow {
        emit(Response.Loading)
        auth.signOut()
        emit(Response.Success(true))
    }
}