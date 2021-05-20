package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.ResponseBuilder
import objects.persistence.Favorito
import persistence.DatabaseConnection
import java.io.BufferedReader
import java.net.URL

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
                response = "POST request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())
                val favorito = Favorito.fromJson(JsonParser.parseReader(reader).asJsonObject)
                postFavorito(favorito)
            }
            "PUT" -> {
                response = "PUT request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())
                val favorito = Favorito.fromJson(JsonParser.parseReader(reader).asJsonObject)
                putFavorito(favorito)
            }
            "DELETE" -> {
                response = "DELETE request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())
                val favorito = Favorito.fromJson(JsonParser.parseReader(reader).asJsonObject)
                deleteFavorito(favorito)
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
        val dbConnection = DatabaseConnection.getInstance()
        return dbConnection.getFavoritosDeUsuario(idUsuario)
    }

    private fun postFavorito(favorito: Favorito) {
        val dbConnection = DatabaseConnection.getInstance()
        dbConnection.storeFavorito(favorito)
    }

    private fun deleteFavorito(favorito: Favorito) {
        val dbConnection = DatabaseConnection.getInstance()
        dbConnection.eliminarFavorito(favorito)
    }
    private fun putFavorito(favorito: Favorito) {
        val dbConnection = DatabaseConnection.getInstance()
        dbConnection.modificarFavorito(favorito)
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