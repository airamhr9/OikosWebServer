package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById

class Usuario(
    override val id: Int,
    var nombre: String,
    var mail: String,
    var contraseña:String,
    var imagen:String?
) : SearchableById, JsonConvertible {

    override fun toJson(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", this.id)
        result.addProperty("nombre", this.nombre)
        result.addProperty("mail", this.mail)
        result.addProperty("contraseña", this.contraseña)
        result.addProperty("imagen", this.imagen)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other is Usuario) {
            return id == other.id
        }
        return false
    }

    override fun toString(): String {
        return toJson().toString()
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Usuario {
            val id = jsonObject.get("id").asInt
            val nombre = jsonObject.get("nombre").asString.toString()
            val mail = jsonObject.get("mail").asString.toString()
            val contraseña = jsonObject.get("contraseña").asString.toString()
            val imagen = jsonObject.get("imagen").asString.toString()
            return Usuario(id, nombre, mail, contraseña, imagen)
        }
    }


}