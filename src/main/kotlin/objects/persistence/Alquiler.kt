package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.util.*

class Alquiler(override val id : Int, private var fecha_disponible : Date, private var estancia_min : Int,
               private var inmueble : Inmueble): SearchableById, JsonConvertible{

    override fun toJson(): JsonObject {
        TODO("Not yet implemented")
    }

    override fun fromJson(jsonObject: JsonObject): Alquiler {
        TODO("Not yet implemented")
    }
}