package objects.persistence

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import objects.ElementoVisitado
import objects.JsonConvertible
import objects.SearchableById
import java.net.InetAddress

abstract class Inmueble(
    override var id: Int,
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
    var fecha: String,
    var contadorVisitas: Int,
) : SearchableById, ElementoVisitado {

    var esFavorito = false

    fun getUrlImagenes(): JsonArray {
        val listaUrlImagenes = JsonArray()
        imagenes.forEach { listaUrlImagenes.add(urlImagen + it) }
        return listaUrlImagenes
    }
    override fun equals(other: Any?): Boolean {
        if (other is Inmueble) {
            return id == other.id
        }
        return false
    }

    companion object {
        private val urlImagen = "http://${InetAddress.getLocalHost().hostAddress}:9000/api/imagen/"
    }
}