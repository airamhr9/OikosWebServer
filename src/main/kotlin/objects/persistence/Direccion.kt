package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById

class Direccion(override val id: Int,
                private var calle: String,
                private var numero: Int,
                private var puerta: Int?,
                private var codigo_postal: Int,
                private var ciudad: String,
                private var inmueble: Inmueble,
) : SearchableById, JsonConvertible {


    override fun toJson(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", this.id)
        result.addProperty("calle", this.calle)
        result.addProperty("numero", this.numero)
        result.addProperty("puerta", this.puerta)
        result.addProperty("codigo_postal", this.codigo_postal)
        result.addProperty("ciudad", this.ciudad)
        result.addProperty("inmueble", this.inmueble.toJson())
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Direccion {
            val id = jsonObject.get("id").asInt
            val calle = jsonObject.get("calle").asString.toString()
            val numero = jsonObject.get("numero").asInt
            val puerta = jsonObject.get("puerta").asInt
            val codigo_postal = jsonObject.get("codigo_postal").asInt
            val ciudad = jsonObject.get("ciudad").asString.toString()
            val inmueble = Inmueble.fromJson(jsonObject.get("inmueble").asJsonObject)
            return Direccion(id, calle, numero, puerta, codigo_postal, ciudad, inmueble)
        }
    }
}