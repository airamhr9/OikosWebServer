package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.util.*

class Usuario(
    override val id: Int,
    private var nombre: String,
    private var mail: String,
) : SearchableById, JsonConvertible {

    private val token = UUID.randomUUID()

    override fun toJson(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", this.id)
        result.addProperty("nombre", this.nombre)
        result.addProperty("mail", this.mail)
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Usuario {
            val id = jsonObject.get("id").asInt
            val nombre = jsonObject.get("nombre").asString.toString()
            val mail = jsonObject.get("mail").asString.toString()
            return Usuario(id, nombre, mail)
        }
    }

}