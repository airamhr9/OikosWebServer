package logic.endpoints

import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.ResponseBuilder
import objects.persistence.Favorito
import java.net.URL

// Modificar la clase EndpointHandler para que compile
class FavoritoEndpoint(endpoint: String) : EndpointHandler<Favorito>(endpoint) {

    override fun handleExchange(exchange: HttpExchange) {
        var response: String

        when (exchange.requestMethod) {
            "GET" -> {
                val map: Map<String, Any?> = RequestParser.getQueryParameters(URL(
                        "http://" + exchange.requestHeaders.getFirst("Host") + exchange.requestURI))

                val limit = if ("limit" in map) {
                    map["limit"].toString().toInt()
                } else {
                    100
                }

                val favoritos = getFavoritosdeUsuario(map["usuario"].toString().toInt())
                response = ResponseBuilder.createListResponse(favoritos, limit)
            }
            "POST" -> {
                response = ""
            }
            "PUT" -> {
                response = ""
            }
            "OPTIONS" -> {
                /**
                 * Endpoint que devuelve un objeto que define los
                 * métodos de petición
                 * Por ejemplo:
                 * Para este endpoint devolvería "GET" "POST" Y "PUT", con los parámetros necesarios de cada uno
                 */
                response = "OPTIONS request"
            }
            else -> {
                //405 Método no soportado
                exchange.sendResponseHeaders(405, -1)
                response = "Method not supported"
            }
        }
        exchange.sendResponseHeaders(200, response.toByteArray(Charsets.UTF_8).size.toLong())
        val outputStream = exchange.responseBody
        outputStream.write(response.toByteArray())
        outputStream.flush()
        exchange.close()
    }

    private fun getFavoritosdeUsuario(idUsuario: Int): List<Favorito> {
        TODO("Not yet implemented")
    }


    override fun getIndividualById(objectId : Int): Favorito {
        TODO("Not yet implemented")
    }

    override fun getDefaultList(num: Int): List<Favorito> {
        TODO("Not yet implemented")
    }

    override fun getListByIds(idList: List<Int>): List<Favorito> {
        TODO("Not yet implemented")
    }

    override fun postIndividual(newObject: Favorito): Favorito {
        TODO("Not yet implemented")
    }

    override fun put(modifiedObject: Favorito): Favorito {
        TODO("Not yet implemented")
    }

    override fun responseToJson(): String {
        TODO("Not yet implemented")
    }

}