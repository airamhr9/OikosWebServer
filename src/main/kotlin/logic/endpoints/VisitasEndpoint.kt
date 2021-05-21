package logic.endpoints

import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import objects.persistence.Inmueble
import persistence.DatabaseConnection
import java.net.URL

class VisitasEndpoint(endpoint: String) : EndpointHandler<Inmueble>(endpoint) {

    override fun handleExchange(exchange: HttpExchange) {
        var response = ""
        when (exchange.requestMethod) {
            "PUT" -> {
                val map: Map<String, Any?> = RequestParser.getQueryParameters(URL(
                    "http://" + exchange.requestHeaders.getFirst("Host") + exchange.requestURI)                )
                val idInmueble = map["id"].toString().toInt()
                actualizarContador(idInmueble)
                response = "Contador actualizado"
            }
        }
        exchange.sendResponseHeaders(200, response.toByteArray(Charsets.UTF_8).size.toLong())
        val outputStream = exchange.responseBody
        outputStream.write(response.toByteArray())
        outputStream.flush()
        exchange.close()
    }

    private fun actualizarContador(idInmueble: Int) {
        val databaseConnection = DatabaseConnection.getInstance()
        var inmueble = databaseConnection.getInmuebleById(idInmueble)
        inmueble.contadorVisitas++
        databaseConnection.actualizarInmueble(inmueble)
    }

    override fun getIndividualById(objectId: Int): Inmueble {
        TODO("Not yet implemented")
    }

    override fun getDefaultList(num: Int): List<Inmueble> {
        TODO("Not yet implemented")
    }

    override fun getListByIds(idList: List<Int>): List<Inmueble> {
        TODO("Not yet implemented")
    }

    override fun postIndividual(newObject: Inmueble): Inmueble {
        TODO("Not yet implemented")
    }

    override fun put(modifiedObject: Inmueble): Inmueble {
        TODO("Not yet implemented")
    }

    override fun responseToJson(): String {
        TODO("Not yet implemented")
    }
}