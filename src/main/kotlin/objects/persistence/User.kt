package objects.persistence

import com.google.gson.JsonObject
import objects.JsonConvertible
import objects.SearchableById
import java.util.*

class User(override val id : Int, private var name : String, private var mail : String) : SearchableById, JsonConvertible {
    private val token = UUID.randomUUID()

    override fun toJson(): JsonObject {
        TODO("Not yet implemented")
    }

    override fun fromJson(): JsonObject {
        TODO("Not yet implemented")
    }

}