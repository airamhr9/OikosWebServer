package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.util.*

class CaracteristicaAlquiler(override val id : Int, private var inmueble : Int, private var caracteristica : String,
                             private var alquiler : Alquiler): SearchableById, JsonConvertible {


    override fun toJson(): JsonObject {
        TODO("Not yet implemented")
    }

    override fun fromJson(): JsonObject {
        TODO("Not yet implemented")
    }
}