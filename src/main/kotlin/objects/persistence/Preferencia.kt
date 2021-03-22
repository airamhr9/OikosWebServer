package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById

class Preferencia(override val id: Int,
                  private var superficie_min: Int,
                  private var superficie_max: Int,
                  private var precio_min: Double,
                  private var precio_max: Double,
                  private var habitaciones: Int,
                  private var baños: Int,
                  private var garaje: Boolean,
                  private var ciudad: String,
                  private var usuario: Usuario,
                  private var tipo : String
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
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Preferencia {
            val id = jsonObject.get("id").asInt
            val superficie_min = jsonObject.get("superficie_min").asInt
            val superficie_max = jsonObject.get("superficie_max").asInt
            val precio_min = jsonObject.get("precio_min").asDouble
            val precio_max = jsonObject.get("precio_max").asDouble
            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean
            val ciudad = jsonObject.get("ciudad").asString.toString()
            val usuario = Usuario.fromJson(jsonObject.get("usuario").asJsonObject)
            val tipo = jsonObject.get("tipo").asString
            return Preferencia(id, superficie_min, superficie_max, precio_min, precio_max, habitaciones,baños, garaje, ciudad, usuario, tipo)
        }
    }
}