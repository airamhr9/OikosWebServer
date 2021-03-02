package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.util.*

class Preferencia(override val id : Int, private var superficie_min : Int, private var superficie_max : Int,
                  private var precio_min : Double, private var precio_max : Double, private var habitaciones : Int,
                  private var baños : Int, private var garaje : Boolean, private var ciudad : String, private var usuario : User): SearchableById, JsonConvertible {

    override fun toJson(): JsonObject {
        TODO("Not yet implemented")
    }

    override fun fromJson(): JsonObject {
        TODO("Not yet implemented")
    }
}