package logic

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import objects.JsonConvertible

class ResponseBuilder {
    companion object{
        fun <T : JsonConvertible> createListResponse(elementsToSend : List<T>, limit : Int) : String{
            val responseJson = JsonArray()

            for(element in elementsToSend){
                responseJson.add(element.toJson())
            }

            return responseJson.toString()
        }

        fun <T : JsonConvertible> createObjectResponse(elementToSend : T) : String {
            return elementToSend.toJson().toString()
        }
    }
}