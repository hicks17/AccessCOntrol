package js.apps.accesscontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import js.apps.accesscontrol.model.Alumno
import js.apps.accesscontrol.model.Entrance
import js.apps.accesscontrol.network.UserRepository
import js.apps.accesscontrol.network.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlumnoViewModel @Inject constructor(
    private val userRepository: UserRepositoryImpl

) : ViewModel() {
    private val _userData = MutableStateFlow(Alumno(uid = "", matricula ="", nombre = "", correo = "", apellido = ""))
    val userData = _userData.asStateFlow()

    private val _lastEntranceList = MutableStateFlow(listOf<Entrance>())
    val lastEntranceList = _lastEntranceList.asStateFlow()

    private val _isAuthorized = MutableStateFlow(2)
    val isAuthorized = _isAuthorized.asStateFlow()

    var uid = MutableStateFlow("")


    fun createUser(user: Alumno) {
        viewModelScope.launch {
            userRepository.createUser(user)
            _userData.update { user }

        }

    }

    fun authorizeEntrance(correo: String) {

        viewModelScope.launch {
            _isAuthorized.update {
                when (userRepository.authorization(correo)) {
                    true -> 1
                    false -> 0
                }
            }

        }
    }

    fun setUser(uid: String) {
        viewModelScope.launch {
            _userData.update {
                userRepository.getUser(uid)
            }
            _lastEntranceList.update {
                userRepository.getLastEntrancesByUserID(uid)
            }
        }
    }
}