package objects.persistence

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.net.InetAddress

abstract class InmuebleSprint2(override val id: Int,
                      var disponible: Boolean,
                      var tipo: TipoInmueble,
                      var superficie: Int,
                      var precio: Double,
                      var propietario: Usuario,
                      var descripcion: String,
                      var direccion: String,
                      var ciudad: String,
                      var latitud: Double,
                      var longitud: Double,
                      var imagenes: Array<String>,
) : SearchableById, JsonConvertible {

    private fun generarJsonBasico(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", id)
        result.addProperty("disponible", disponible)
        result.addProperty("tipo", tipo.value)
        result.addProperty("precio", precio)
        result.addProperty("direccion", direccion)
        result.addProperty("ciudad", ciudad)
        result.addProperty("latitud", latitud)
        result.addProperty("longitud", longitud)
        introducirModeloEnJsonObject(result, "modelo")
        return result
    }

    protected abstract fun introducirModeloEnJsonObject(jsonObject: JsonObject, nombrePropiedad: String)

    override fun toJson(): JsonObject {
        val result = generarJsonBasico()

        result.addProperty("superficie", superficie)
        result.add("propietario", propietario.toJson())
        result.addProperty("descripcion", descripcion)

        val listaUrlImagenes = JsonArray()
        for (imagen:String in imagenes) {
            listaUrlImagenes.add(urlImagen + imagen)
        }
        result.add("imagenes", listaUrlImagenes)

        return result
    }

    fun toJsonReducido(): JsonObject {
        val result = generarJsonBasico()
        result.addProperty("numImagenes", imagenes.size)
        result.addProperty("imagen", urlImagen + imagenes[0])
        return result
    }

    companion object {
        val urlImagen = "http://${InetAddress.getLocalHost().hostAddress}:9000/api/imagen/"
    }
}