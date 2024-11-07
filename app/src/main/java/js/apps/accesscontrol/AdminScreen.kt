package js.apps.accesscontrol

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import js.apps.accesscontrol.model.Entrance
import js.apps.accesscontrol.ui.theme.guinda
import js.apps.accesscontrol.ui.theme.styles
import js.apps.accesscontrol.ui.theme.tinto
import js.apps.accesscontrol.viewmodel.AlumnoViewModel

@Composable
fun AdminScreen(alumnViewModel: AlumnoViewModel){

    val localContext = LocalContext.current
    val authState = alumnViewModel.isAuthorized.collectAsState()
    val cameraScanner = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {

            val result: IntentResult? =
                IntentIntegrator.parseActivityResult(it.resultCode, it.data)

            if (result != null) {
                if (result.contents != null) {
                    Toast.makeText(localContext, result.contents, Toast.LENGTH_SHORT).show()
                    alumnViewModel.authorizeEntrance(result.contents)

                } else {
                    Toast.makeText(localContext, "Scan Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(top = 30.dp)) {
        Text(text = "Pantalla de administrador", style = styles)
        Spacer(modifier = Modifier.height(18.dp))
        Card (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = guinda),elevation = CardDefaults.cardElevation(10.dp),
            onClick = { cameraScanner.launch(IntentIntegrator(localContext as Activity).createScanIntent()) }) {

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(text = "Scanner de QR", style = styles)
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.qr_scanner),
                    contentDescription = "Scanner QR",
                    modifier = Modifier.padding(16.dp)
                )

            }
        }
        Text(text = "Últimas entradas", style = styles, modifier = Modifier.padding(16.dp))

    }
    when(authState.value){
        0 -> { Toast.makeText(localContext, "QR no válido", Toast.LENGTH_SHORT).show()}
        1 -> { Toast.makeText(localContext, "Acceso concedido", Toast.LENGTH_SHORT).show()}
        2 -> Unit
    }

}


@Composable
fun EntranceCard(entrance: Entrance){


    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .background(guinda)) {
        val (fecha, hora, matricula, fullname, divider) = createRefs()

        Text(text = entrance.fecha, modifier = Modifier.constrainAs(fecha){
            top.linkTo(matricula.bottom, margin = 16.dp)
            start.linkTo(parent.start, margin = 16.dp)
            bottom.linkTo(parent.bottom, margin = 16.dp)
        }, style = styles)
        Text(text = entrance.hora, modifier = Modifier.constrainAs(hora){
            bottom.linkTo(parent.bottom, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
        }, style = styles)
        Text(text = entrance.matricula, modifier = Modifier.constrainAs(matricula){
            top.linkTo(fullname.bottom, margin = 0.dp)
            start.linkTo(parent.start, margin = 16.dp)
        }, style = styles)
        Text(text = entrance.fullname, modifier = Modifier.constrainAs(fullname){
            top.linkTo(parent.top, margin = 16.dp)
            start.linkTo(parent.start, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
        }, fontFamily = FontFamily(Font(R.font.raleway_bold)), textAlign = TextAlign.Left,
            fontSize = 20.sp)
        HorizontalDivider(modifier = Modifier.constrainAs(divider){
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
        }, thickness = 1.dp, color = Color.Black)



        }
    }

private fun combineBitmaps(backgroundBitmap: Bitmap, overlayBitmap: Bitmap): Bitmap {
    val combinedBitmap = Bitmap.createBitmap(backgroundBitmap.width, backgroundBitmap.height, backgroundBitmap.config)
    val canvas = Canvas(combinedBitmap)
    canvas.drawBitmap(backgroundBitmap, 0f, 0f, null)
    val left = (backgroundBitmap.width - overlayBitmap.width) / 2
    val top = (backgroundBitmap.height - overlayBitmap.height) / 2
    canvas.drawBitmap(overlayBitmap, left.toFloat(), top.toFloat(), null)
    return combinedBitmap
}

private fun startQRCodeScanner(localContext: Context) {
    val integrator = IntentIntegrator(localContext as Activity)
    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
    integrator.setBeepEnabled(false)
    integrator.setOrientationLocked(false)
    integrator.initiateScan()


}

