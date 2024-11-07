package js.apps.accesscontrol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import js.apps.accesscontrol.network.LoginRepoAuth
import js.apps.accesscontrol.network.LoginRepository
import js.apps.accesscontrol.utils.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _signUpFlow: MutableSharedFlow<Response<Boolean>> = MutableSharedFlow()
    val signUpFlow: SharedFlow<Response<Boolean>> = _signUpFlow.asSharedFlow()


    private val _loginShared: MutableSharedFlow<Response<Boolean>> = MutableSharedFlow()
    val loginShared: SharedFlow<Response<Boolean>> = _loginShared.asSharedFlow()

    private val _signShared: MutableSharedFlow<Response<Boolean>> = MutableSharedFlow()
    val signShared: SharedFlow<Response<Boolean>> = _signShared.asSharedFlow()


    fun signUp(mail: String, pass: String) {
        viewModelScope.launch {
            loginRepository.signUp(mail, pass).onEach { signUpShared ->
                _signUpFlow.emit(signUpShared)
            }.launchIn(viewModelScope)
        }
    }


    fun login(mail: String, pass: String) {
        viewModelScope.launch {
            loginRepository.logIn(mail, pass)
                .onEach { flowData ->
                    _loginShared.emit(flowData)
                }.launchIn(viewModelScope)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            loginRepository.logOut()
                .onEach { flowData ->
                    _signShared.emit(flowData)
                }
        }
    }

}