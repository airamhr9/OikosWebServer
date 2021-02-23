package logic

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import objects.JsonConvertible

class ResponseBuilder {
    companion object{
        fun <T : JsonConvertible> createListResponse(elementsToSend : List<T>, limit : Int = 20) : JsonObject{
            val responseJson = JsonObject()
            val maxElements = if (limit > elementsToSend.size) elementsToSend.size else limit

            responseJson.addProperty("count", maxElements)
            val elementsInJson = JsonArray()

            for(element in elementsToSend){
                elementsInJson.add(element.toJson())
            }
            responseJson.add("results", elementsInJson)

            return responseJson
        }

        fun <T : JsonConvertible> createObjectResponse(elementToSend : T) : JsonObject {
            return elementToSend.toJson()
        }
    }
}