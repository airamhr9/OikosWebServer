package objects.persistence

import com.google.gson.JsonObject
import objects.Visitante
import persistence.DatabaseConnection

class Local(id: Int,
            disponible: Boolean,
            tipo: TipoInmueble,
            superficie: Int,
            precio: Double,
            propietario: Usuario,
            descripcion: String,
            direccion: String,
            ciudad: String,
            latitud: Double,
            longitud: Double,
            imagenes: Array<String>,
            fecha: String,
            contadorVisitas: Int,
            var baños: Int,
) : Inmueble(id, disponible, tipo, superficie, precio, propietario, descripcion,
            direccion, ciudad, latitud, longitud, imagenes, fecha, contadorVisitas) {

    override fun introducirModeloEnJsonObject(jsonObject: JsonObject, nombrePropiedad: String) {
        jsonObject.addProperty(nombrePropiedad, ModeloInmueble.Local.value)
    }

    override fun toJson(): JsonObject {
        val result = super.toJson()
        result.addProperty("baños", baños)
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Local {
            val id = jsonObject.get("id").asInt
            val disponible = jsonObject.get("disponible").asBoolean
            val tipo = TipoInmueble.fromString(jsonObject.get("tipo").asString)
            val superficie = jsonObject.get("superficie").asInt
            val precio = jsonObject.get("precio").asDouble
            val connection = DatabaseConnection.getInstance()
            val propietario = connection.getUsuarioById(jsonObject.getAsJsonObject("propietario").get("id").asInt)
            val descripcion = jsonObject.get("descripcion").asString.toString()
            val direccion = jsonObject.get("direccion").asString
            val ciudad = jsonObject.get("ciudad").asString
            val latitud = jsonObject.get("latitud").asDouble
            val longitud = jsonObject.get("longitud").asDouble
            val fecha = jsonObject.get("fecha").asString
            val contadorVisitas = jsonObject.get("contadorVisitas").asInt

            val listaUrlImagenes = jsonObject.getAsJsonArray("imagenes")
            val imagenes  = listaUrlImagenes.map { it.asString }.toTypedArray()

            val baños = jsonObject.get("baños").asInt

            return Local(id, disponible, tipo, superficie, precio, propietario, descripcion,
                direccion, ciudad, latitud, longitud, imagenes, fecha, contadorVisitas, baños)
        }
    }

    override fun accept(v: Visitante) {
        v.visitLocal(this)
    }
}