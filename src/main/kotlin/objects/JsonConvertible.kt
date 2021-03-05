package objects

import com.google.gson.JsonObject

interface JsonConvertible {
    fun toJson() : JsonObject
}