package objects.persistence

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.net.InetAddress

abstract class InmuebleSprint2(override var id: Int,
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

    var esFavorito = false

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
        result.addProperty("favorito", esFavorito)

        val listaUrlImagenes = JsonArray()
        imagenes.forEach { listaUrlImagenes.add(urlImagen + it) }
        result.add("imagenes", listaUrlImagenes)

        return result
    }

    fun toJsonReducido(): JsonObject {
        val result = generarJsonBasico()
        result.addProperty("numImagenes", imagenes.size)
        result.addProperty("imagen", urlImagen + imagenes[0])
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other is InmuebleSprint2) {
            return id == other.id
        }
        return false
    }

    companion object {
        //var serverPort: Int? = null
        private val urlImagen = "http://${InetAddress.getLocalHost().hostAddress}:9000/api/imagen/"
    }
}