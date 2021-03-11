package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById

class Inmueble(override val id: Int,
               private var disponible: Boolean,
               private var superficie: Int,
               private var precio: Double,
               private var direccion: String,
               private var latitud: Double,
               private var longitud: Double,
               private var habitaciones: Int,
               private var baños: Int,
               private var garaje: Boolean,
               private var propietario: Usuario,
               private var descripcion: String?,
               // private var imagenes: Array<String>,
) : SearchableById, JsonConvertible {

    override fun toJson(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", this.id)
        result.addProperty("disponible", this.disponible)
        // Falta añadir el tipo
        result.addProperty("superficie", this.superficie)
        result.addProperty("precio", this.precio)
        result.addProperty("direccion", this.direccion)
        result.addProperty("latitud", this.latitud)
        result.addProperty("longitud", this.longitud)
        result.addProperty("habitaciones", this.habitaciones)
        result.addProperty("baños", this.baños)
        result.addProperty("garaje", this.garaje)
        result.add("propietario", this.propietario.toJson())
        result.addProperty("descripcion", this.descripcion)
        // Falta añadir las imagenes
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Inmueble {
            val id = jsonObject.get("id").asInt
            val disponible = jsonObject.get("disponible").asBoolean
            val superficie = jsonObject.get("superficie").asInt
            val precio = jsonObject.get("precio").asDouble
            val direccion = jsonObject.get("direccion").asString
            val latitud = jsonObject.get("latitud").asDouble
            val longitud = jsonObject.get("longitud").asDouble
            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean
            val propietario = Usuario.fromJson(jsonObject.get("propietario").asJsonObject)
            val descripcion = jsonObject.get("descripcion").asString.toString()
            return Inmueble(id, disponible, superficie, precio, direccion, latitud, longitud,
                habitaciones, baños, garaje, propietario, descripcion)
        }
    }
}