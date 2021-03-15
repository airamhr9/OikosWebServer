package objects.persistence

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.net.InetAddress

class Inmueble(override val id: Int,
               private var disponible: Boolean,
               private var tipo: TipoInmueble,
               private var superficie: Int,
               private var precio: Double,
               private var habitaciones: Int,
               private var baños: Int,
               private var garaje: Boolean,
               private var propietario: Usuario,
               private var descripcion: String?,
               private var direccion: String,
               private var ciudad: String,
               private var latitud: Double,
               private var longitud: Double,
               private var imagenes: Array<String>,
) : SearchableById, JsonConvertible {

    private fun generarJsonBasico(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", this.id)
        result.addProperty("disponible", this.disponible)
        result.addProperty("tipo", this.tipo.value)
        result.addProperty("precio", this.precio)
        result.addProperty("direccion", this.direccion)
        result.addProperty("ciudad", this.ciudad)
        result.addProperty("latitud", this.latitud)
        result.addProperty("longitud", this.longitud)
        return result
    }

    override fun toJson(): JsonObject {
        val result = generarJsonBasico()

        result.addProperty("superficie", this.superficie)
        result.addProperty("habitaciones", this.habitaciones)
        result.addProperty("baños", this.baños)
        result.addProperty("garaje", this.garaje)
        result.add("propietario", this.propietario.toJson())
        result.addProperty("descripcion", this.descripcion)

        val listaUrlImagenes = JsonArray()
        for (imagen:String in imagenes) {
            listaUrlImagenes.add(urlImagen + imagen)
        }
        result.add("imagenes", listaUrlImagenes)

        return result
    }

    fun toJsonReducido(): JsonObject {
        val result = generarJsonBasico()
        result.addProperty("numImagenes", this.imagenes.size)
        result.addProperty("imagen", urlImagen + imagenes[0])
        return result
    }

    companion object {
        private val urlImagen = "http://${InetAddress.getLocalHost()}/api/imagen"

        fun fromJson(jsonObject: JsonObject): Inmueble {
            val id = jsonObject.get("id").asInt
            val disponible = jsonObject.get("disponible").asBoolean
            val tipo = TipoInmueble.fromString(jsonObject.get("tipo").asString)
            val superficie = jsonObject.get("superficie").asInt
            val precio = jsonObject.get("precio").asDouble
            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean
            val propietario = Usuario.fromJson(jsonObject.getAsJsonObject("propietario"))
            val descripcion = jsonObject.get("descripcion").asString.toString()
            val direccion = jsonObject.get("direccion").asString
            val ciudad = jsonObject.get("ciudad").asString
            val latitud = jsonObject.get("latitud").asDouble
            val longitud = jsonObject.get("longitud").asDouble

            /* val listaUrlImagenes = jsonObject.getAsJsonArray("imagenes")
            val imagenes:Array<String> = listaUrlImagenes.map { it.asString.split("/")[4] }.toTypedArray() */
            val imagenes = arrayOf("")

            return Inmueble(id, disponible, tipo, superficie, precio, habitaciones, baños, garaje,
                propietario, descripcion, direccion, ciudad, latitud, longitud, imagenes)
        }
    }
}