package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.util.*

class Direccion(override val id : Int, private var calle : String, private var numero : Int, private var puerta : Int?,
                private var codigo_postal : Int, private var ciudad : String, private var inmueble : Inmueble): SearchableById, JsonConvertible {

    override fun toJson(): JsonObject {
        TODO("Not yet implemented")
    }

    override fun fromJson(jsonObject: JsonObject): Direccion {
        TODO("Not yet implemented")
    }
}