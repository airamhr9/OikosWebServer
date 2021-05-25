package objects.persistence

import com.google.gson.JsonObject
import objects.Visitante
import persistence.DatabaseConnection

class Habitacion(id: Int,
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
                 habitaciones: Int,
                 baños: Int,
                 garaje: Boolean,
                 var numCompañeros: Int,
) : Piso(id, disponible, tipo, superficie, precio, propietario, descripcion,
            direccion, ciudad, latitud, longitud, imagenes, fecha, contadorVisitas, habitaciones, baños, garaje) {

    override fun introducirModeloEnJsonObject(jsonObject: JsonObject, nombrePropiedad: String) {
        jsonObject.addProperty(nombrePropiedad, ModeloInmueble.Habitacion.value)
    }

    override fun toJson(): JsonObject {
        val result = super.toJson()
        result.addProperty("numCompañeros", numCompañeros)
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Habitacion {
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

            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean
            val numCompañeros = jsonObject.get("numCompañeros").asInt

            return Habitacion(id, disponible, tipo, superficie, precio, propietario, descripcion,
                direccion, ciudad, latitud, longitud, imagenes, fecha, contadorVisitas, habitaciones, baños, garaje, numCompañeros)
        }
    }

    override fun accept(v: Visitante) {
        v.visitHabitacion(this)
    }
}