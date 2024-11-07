package js.apps.accesscontrol

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getColor
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import js.apps.accesscontrol.model.Alumno
import js.apps.accesscontrol.model.Entrance
import js.apps.accesscontrol.ui.theme.guinda
import js.apps.accesscontrol.viewmodel.AlumnoViewModel
import java.util.EnumMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userViewModel: AlumnoViewModel, goToLoginScreen: () -> Unit) {

    val localContext = LocalContext.current
    val listaDeEntrda by userViewModel.lastEntranceList.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    var isDetailExpanded by remember { mutableStateOf(false) }
    val userData by userViewModel.userData.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()


    Column( modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    colorResource(id = R.color.tinto),
                    Color(0xFFC92052)
                )
            )
        ), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Acceso UAdeO", fontSize = 30.sp, fontFamily = FontFamily(Font(R.font.raleway_light)), color = Color.White,
            modifier = Modifier.padding(top = 30.dp))
        Spacer(modifier = Modifier.height(36.dp))
        Card(onClick = {
            isExpanded = true
                       
                       }, modifier = Modifier.size(220.dp)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Mostrar código de acceso", fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.raleway_light)), modifier = Modifier.padding(top = 10.dp))
                Spacer(modifier = Modifier.height(30.dp))
                Image(painterResource(id =  R.drawable.usuario), contentDescription = "", modifier = Modifier.size(100.dp))


            }

        }
        Spacer(modifier = Modifier.height(36.dp))
        Card(onClick = {
            isDetailExpanded = true
            //cameraScanner.launch(IntentIntegrator(localContext as Activity).createScanIntent()) }
        }, modifier = Modifier.size(220.dp)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Perfil de alumno", fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.raleway_light)), modifier = Modifier.padding(top = 20.dp))
                Spacer(modifier = Modifier.height(30.dp))
                Image(painterResource(id =  R.drawable.perfil), contentDescription = "", modifier = Modifier.size(100.dp))
            }
        }
        Spacer(modifier = Modifier.height(36.dp))
        Card(onClick = { goToLoginScreen() }, modifier = Modifier.size(220.dp)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Cerrar sesión", fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.raleway_light)), modifier = Modifier.padding(top = 10.dp))
                Spacer(modifier = Modifier.height(30.dp))
                Image(painterResource(id =  R.drawable.cerrar_sesion), contentDescription = "", modifier = Modifier.size(100.dp))

            }
        }
        if (isDetailExpanded){
            UserOnDetail(user = userData, entranceList = listaDeEntrda, onDismiss = { isDetailExpanded = false })
        }
        if(isExpanded) {

            ModalBottomSheet( sheetState = bottomSheetState, onDismissRequest = { isExpanded = false }) {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Tu codigo de acceso es:", fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.raleway_light)), modifier = Modifier.padding( 40.dp))
                    generarCodigo(userData.correo.replace(".", ","), localContext)?.asImageBitmap()
                        ?.let { BitmapPainter(it) }
                        ?.let { Image(painter = it, contentDescription = null) }

                }
            }
        }
    }

}

private fun generarCodigo(data: String, resources: Context): Bitmap? {
    val qrCodeWidthPixels = 500
        val bitMatrix: BitMatrix = try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE,
                qrCodeWidthPixels,
                qrCodeWidthPixels,
                hints
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("QR", "Error al generar el código QR")
            return null
        }

        val qrCodeWidth = bitMatrix.width
        val qrCodeHeight = bitMatrix.height
        val pixels = IntArray(qrCodeWidth * qrCodeHeight)

        for (y in 0 until qrCodeHeight) {
            val offset = y * qrCodeWidth
            for (x in 0 until qrCodeWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) {
                    getColor(resources, R.color.black) // QR code color
                } else {
                    getColor(resources, R.color.white) // Background color
                }
            }
        }

        val bitmap = Bitmap.createBitmap(qrCodeWidth, qrCodeHeight, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, qrCodeWidth, 0, 0, qrCodeWidth, qrCodeHeight)


        return bitmap
    }

@Composable
fun UserOnDetail(
    user: Alumno,
    entranceList: List<Entrance>,
    onDismiss: () -> Unit
){

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = Modifier
            .fillMaxWidth(), colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = guinda
            )) {
            LazyColumn(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Datos del alumno",
                            fontFamily = FontFamily(Font(R.font.raleway_bold)),
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nombre: ${user.nombre} ${user.apellido}",
                            fontFamily = FontFamily(Font(R.font.raleway_light)),
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Matricula: ${user.matricula}",
                            fontFamily = FontFamily(Font(R.font.raleway_light)),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Correo: ${user.correo.replace(",", ".")}",
                            fontFamily = FontFamily(Font(R.font.raleway_light)), fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "UID: ${user.uid}",
                            fontFamily = FontFamily(Font(R.font.raleway_light)),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Historial de entradas: ", fontFamily = FontFamily(Font(R.font.raleway_light)), fontSize = 18.sp)
                    }
                }
                items(entranceList){
                    EntranceCard(entrance = it)
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = { onDismiss() }, modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 16.dp)
                    ) {
                        Text(text = "Cerrar")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

        }
    }

}



