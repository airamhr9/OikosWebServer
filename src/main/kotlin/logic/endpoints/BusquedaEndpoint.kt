package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.ResponseBuilder
import objects.persistence.Busqueda
import persistence.DatabaseConnection
import java.io.BufferedReader
import java.net.URL
import java.net.URLDecoder


class BusquedaEndpoint(endpoint: String) : EndpointHandler<Busqueda>(endpoint) {

    override fun handleExchange(exchange: HttpExchange) {
        println("handling exchange")
        lateinit var response : String
        when (exchange.requestMethod) {
            "GET" -> {
                response = "GET request"
                val map: Map<String, Any?> =
                    RequestParser.getQueryParameters(URL("http://" + exchange.requestHeaders.getFirst("Host") + exchange.requestURI))

                if ("id" in map) {
                    response = ResponseBuilder.createObjectResponse(getIndividualById(map["id"].toString().toInt()))
                }

            }
            "POST" -> {
                response = "POST request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader(Charsets.UTF_8))
                var string: String = reader.readLines().toString()
                string = URLDecoder.decode(string, "UTF-8");
                string = string.substring(7, string.length - 1)


                val guardado = Busqueda.fromJson(JsonParser.parseString(string).asJsonObject)
                postIndividual(guardado)
            }
            "PUT" -> {
                response = "PUT request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())
                var string: String = reader.readLines().toString()
                string = URLDecoder.decode(string, "UTF-8");
                string = string.substring(7, string.length - 1)


                val guardado= Busqueda.fromJson(JsonParser.parseString(string).asJsonObject)
                put(guardado)
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

    override fun getIndividualById(objectId: Int): Busqueda {
        val dbConnection = DatabaseConnection()
        return dbConnection.guardadoById(objectId)
    }

    override fun getDefaultList(num: Int): List<Busqueda> {
        TODO("Not yet implemented")
    }

    override fun getListByIds(idList: List<Int>): List<Busqueda> {
        TODO("Not yet implemented")
    }

    override fun postIndividual(newObject: Busqueda): Busqueda {
        val dbConnection = DatabaseConnection()
        return dbConnection.crearGuardado(newObject)
    }

    override fun put(modifiedObject: Busqueda): Busqueda {
        val dbConnection = DatabaseConnection()
        return dbConnection.actualizarGuardado(modifiedObject)
    }

    override fun responseToJson(): String {
        TODO("Not yet implemented")
    }
}