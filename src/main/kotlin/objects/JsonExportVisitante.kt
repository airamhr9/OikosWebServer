package objects

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import objects.persistence.*
import java.lang.Exception

class JsonExportVisitante : Visitante {
    private val resultado = JsonArray()

    override fun visitHabitacion(hab: Habitacion) {
        resultado.add(habitacionToJson(hab))
    }

    override fun visitPiso(piso: Piso) {
        resultado.add(pisoToJson(piso))
    }

    override fun visitLocal(local: Local) {
        resultado.add(localToJson(local))
    }

    override fun visitGaraje(garaje: Garaje) {
        resultado.add(garajeToJson(garaje))
    }

    override fun visitFavorito(favorito: Favorito) {
        resultado.add(favoritoToJson(favorito))
    }

    private fun inmuebleToJson(inmueble: Inmueble): JsonObject {
        val result = JsonObject()
        result.addProperty("id", inmueble.id)
        result.addProperty("disponible", inmueble.disponible)
        result.addProperty("tipo", inmueble.tipo.value)
        result.addProperty("precio", inmueble.precio)
        result.addProperty("direccion", inmueble.direccion)
        result.addProperty("ciudad", inmueble.ciudad)
        result.addProperty("latitud", inmueble.latitud)
        result.addProperty("longitud", inmueble.longitud)
        result.addProperty("superficie", inmueble.superficie)
        result.add("propietario", inmueble.propietario.toJson())
        result.addProperty("descripcion", inmueble.descripcion)
        result.add("imagenes", inmueble.getUrlImagenes())
        result.addProperty("fecha", inmueble.fecha)
        result.addProperty("contadorVisitas", inmueble.contadorVisitas)
        result.addProperty("favorito", inmueble.esFavorito)
        return result
    }

    private fun habitacionToJson(hab: Habitacion): JsonObject {
        val result = pisoToJson(hab)
        result.addProperty("numCompañeros", hab.numCompañeros)
        result.addProperty("modelo", ModeloInmueble.Habitacion.value)
        return result
    }

    private fun garajeToJson(garaje: Garaje): JsonObject {
        val result = inmuebleToJson(garaje)
        result.addProperty("modelo", ModeloInmueble.Garaje.value)
        return result
    }

    private fun localToJson(local: Local): JsonObject {
        val result = inmuebleToJson(local)
        result.addProperty("baños", local.baños)
        result.addProperty("modelo", ModeloInmueble.Local.value)
        return result
    }

    private fun pisoToJson(piso: Piso): JsonObject {
        val result = inmuebleToJson(piso)
        result.addProperty("habitaciones", piso.habitaciones)
        result.addProperty("baños", piso.baños)
        result.addProperty("garaje", piso.garaje)
        result.addProperty("modelo", ModeloInmueble.Piso.value)
        return result
    }

    private fun favoritoToJson(fav: Favorito): JsonObject {
        val result = JsonObject()
        result.add("usuario", fav.usuario.toJson())
        val inmuebleJson = when (fav.inmueble) {
            is Piso -> pisoToJson(fav.inmueble as Piso)
            is Habitacion -> habitacionToJson(fav.inmueble as Habitacion)
            is Garaje -> garajeToJson(fav.inmueble as Garaje)
            is Local -> localToJson(fav.inmueble as Local)
            else -> throw Exception()
        }
        result.add("inmueble", inmuebleJson)
        result.addProperty("notas", fav.notas)
        result.addProperty("orden", fav.orden)
        return result
    }

    fun obtenerResultado() : JsonArray {
        return resultado
    }
}