package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.util.*

class Inmueble(override val id : Int, private var disponible : Boolean, private var superficie : Int, private var precio : Double,
               private var habitaciones : Int, private var baños : Int, private var garaje : Boolean, private var descripcion : String?,
               private var propietario : Int, private var fk_propietario : User): SearchableById, JsonConvertible {

    override fun toJson(): JsonObject {
        TODO("Not yet implemented")
    }

    override fun fromJson(): JsonObject {
        TODO("Not yet implemented")
    }
}