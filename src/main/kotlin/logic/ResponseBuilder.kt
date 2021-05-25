package logic

import objects.ElementoVisitado
import objects.JsonConvertible
import objects.JsonExportVisitante

class ResponseBuilder {
    companion object {

        fun <T : ElementoVisitado> createListResponse(elementsToSend: List<T>): String {
            val jsonExportVisitante = JsonExportVisitante()
            for(element in elementsToSend) {
                element.accept(jsonExportVisitante)
            }
            return jsonExportVisitante.obtenerResultado().toString()
        }

        fun <T : JsonConvertible> createObjectResponse(elementToSend : T): String {
            return elementToSend.toJson().toString()
        }
    }
}