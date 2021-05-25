package objects.persistence

import com.google.gson.JsonObject
import objects.ElementoVisitado
import objects.JsonConvertible
import objects.SearchableById
import objects.Visitante

class Busqueda(override val id: Int,
               var superficie_min: Int?,
               var superficie_max: Int?,
               var precio_min: Double?,
               var precio_max: Double?,
               var habitaciones: Int?,
               var baños: Int?,
               var garaje: Boolean?,
               var numCompañeros: Int?,
               var ciudad: String,
               var usuario: Usuario,
               var tipo : String,
               var modelo : String,
) : SearchableById, JsonConvertible {

    override fun toJson(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", this.id)
        result.addProperty("superficie_min", this.superficie_min)
        result.addProperty("superficie_max", this.superficie_max)
        result.addProperty("precio_min", this.precio_min)
        result.addProperty("precio_max", this.precio_max)
        result.addProperty("habitaciones", this.habitaciones)
        result.addProperty("baños", this.baños)
        result.addProperty("garaje", this.garaje)
        result.addProperty("ciudad", this.ciudad)
        result.add("usuario", this.usuario.toJson())
        result.addProperty("tipo", this.tipo)
        result.addProperty("modelo", this.modelo)
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Busqueda {
            val id = jsonObject.get("id").asInt
            val superficie_min = jsonObject.get("superficie_min").asInt
            val superficie_max = jsonObject.get("superficie_max").asInt
            val precio_min = jsonObject.get("precio_min").asDouble
            val precio_max = jsonObject.get("precio_max").asDouble
            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean
            val numCompañeros = jsonObject.get("numCompañeros").asInt
            val ciudad = jsonObject.get("ciudad").asString.toString()
            val usuario = Usuario.fromJson(jsonObject.get("usuario").asJsonObject)
            val tipo = jsonObject.get("tipo").asString
            val modelo = jsonObject.get("modelo").asString
            return Busqueda(id, superficie_min, superficie_max, precio_min, precio_max, habitaciones,baños, garaje,numCompañeros, ciudad, usuario, tipo, modelo)
        }
    }

}