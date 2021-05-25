package logic

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import objects.ElementoVisitado
import objects.JsonConvertible
import objects.JsonExportVisitante

class ResponseBuilder {
    companion object{

        fun <T : ElementoVisitado> createListResponse(elementsToSend : List<T>, limit : Int) : String{
            val jsonExportVisitante = JsonExportVisitante()

            for(element in elementsToSend){
                element.accept(jsonExportVisitante)
            }

            return jsonExportVisitante.obtenerResultado().toString()
        }

        fun <T : JsonConvertible> createObjectResponse(elementToSend : T) : String {
            return elementToSend.toJson().toString()
        }
    }
}