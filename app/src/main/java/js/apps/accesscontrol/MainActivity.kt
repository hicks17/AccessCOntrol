package js.apps.accesscontrol

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import js.apps.accesscontrol.utils.Response
import js.apps.accesscontrol.viewmodel.AlumnoViewModel
import js.apps.accesscontrol.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<AlumnoViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth

        if (auth.currentUser != null) {
            userViewModel.setUser(auth.currentUser!!.email!!)
        }

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = if (auth.currentUser != null) "pantalla2" else "pantalla1") {
                composable("pantalla1") {
                    LoginScreen(goToHomeScreen = {
                        userViewModel.setUser(auth.currentUser!!.email!!)
                        navController.popBackStack()
                        navController.navigate("pantalla2")
                    }, goToAdminScreen = { navController.navigate("pantalla4") }) {
                        navController.navigate("pantalla3")
                    }
                }
                composable("pantalla2") {
                    HomeScreen(userViewModel) {
                        navController.popBackStack()
                        navController.navigate("pantalla1")
                        auth.signOut()

                    }
                }
                composable("pantalla3") {
                    RegisterScreen(authViewModel = authViewModel, goToHomeScreen = {
                        userViewModel.createUser(it.copy(uid = auth.currentUser!!.uid))
                        navController.popBackStack()
                        navController.navigate("pantalla2")
                    }) {
                        navController.popBackStack()
                    }
                }
                composable("pantalla4") {
                    AdminScreen(alumnViewModel = userViewModel)
                }
            }
        }
    }


    @Composable
    fun LoginScreen(
        goToHomeScreen: () -> Unit,
        goToAdminScreen: () -> Unit,
        goToRegisterScreen: () -> Unit
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var cargando by remember { mutableStateOf(false) }


        val localContext = LocalContext.current


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF720626)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Image(painter = painterResource(id = R.drawable.img), contentDescription = null)
            TextField(value = username, onValueChange = { username = it }, label = {
                Text(
                    "Username",
                    fontFamily = FontFamily(Font(R.font.raleway_light))
                )
            }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            TextField(value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        "Contraseña",
                        fontFamily = FontFamily(Font(R.font.raleway_light))
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    if (!passwordVisible)
                        Image(painter = painterResource(id = R.drawable.eye),
                            contentDescription = null,
                            modifier = Modifier.clickable { passwordVisible = true })
                    else Image(painter = painterResource(id = R.drawable.baseline_password_24),
                        contentDescription = null,
                        modifier = Modifier.clickable { passwordVisible = false })
                })
            Button(onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    if (username == "admin" && password == "123") {
                        goToAdminScreen()
                    }else {
                        authViewModel.login(username, password)
                    }
                } else {
                    Toast.makeText(localContext, "Rellena todos los campos", Toast.LENGTH_SHORT)
                        .show()
                }
            }, modifier = Modifier.width(150.dp)) {
                Text(text = "Iniciar sesión", fontFamily = FontFamily(Font(R.font.raleway_light)))
            }
            Button(onClick = {
                goToRegisterScreen()
            }, modifier = Modifier.width(150.dp)) {
                Text(text = "Registrarse", fontFamily = FontFamily(Font(R.font.raleway_light)))
            }
        }

        if (cargando) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(text = "Cargando...", fontFamily = FontFamily(Font(R.font.raleway_light)))

            }
        }

        LaunchedEffect(key1 = Unit) {
            authViewModel.loginShared.collect { loginFlow ->
                when (loginFlow) {
                    is Response.Success<Boolean> -> {
                        Log.d(TAG, "LoginScreen: ${loginFlow.data}")
                        Toast.makeText(localContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        goToHomeScreen()
                    }

                    is Response.Failed -> {
                        Log.d(TAG, "LoginScreen: ${loginFlow.exception.message}")
                        Toast.makeText(
                            localContext,
                            loginFlow.exception.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Response.Finish -> {
                        cargando = false
                    }

                    is Response.Loading -> {
                        cargando = true
                    }

                }

            }
        }


    }

}













