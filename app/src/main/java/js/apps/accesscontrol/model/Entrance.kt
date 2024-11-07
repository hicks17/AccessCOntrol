package js.apps.accesscontrol.model

data class Entrance(
    val fecha: String,
    val hora: String,
    val matricula: String,
    val fullname: String,
    val timeStamp: Long,
    val puerta:Int =0

)

