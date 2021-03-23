package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.ResponseBuilder
import objects.persistence.Inmueble
import objects.persistence.Preferencia
import persistence.DatabaseConnection
import java.io.BufferedReader
import java.net.URL


class PreferenciasEndpoint(endpoint: String) : EndpointHandler<Preferencia>(endpoint) {

    override fun handleExchange(exchange: HttpExchange) {
        println("handling exchange")
        lateinit var response : String
        when(exchange.requestMethod){
            "GET" -> {
                response = "GET request"
                val map : Map<String, Any?> = RequestParser.getQueryParameters(URL("http://"+ exchange.requestHeaders.getFirst("Host") + exchange.requestURI))

                if("id" in map){
                    response = ResponseBuilder.createObjectResponse(getIndividualById(map["id"].toString().toInt()))
                }

            }
            "POST" -> {
                response = "POST request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())

                val preferencia  = Preferencia.fromJson(JsonParser.parseReader(reader).asJsonObject)
                postIndividual(preferencia)
            }
            "PUT" -> {
                response = "PUT request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())

                val preferencia  = Preferencia.fromJson(JsonParser.parseReader(reader).asJsonObject)
                put(preferencia)
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

    override fun getIndividualById(objectId: Int): Preferencia {
        val dbConnection = DatabaseConnection()
        return dbConnection.preferenciasById(objectId)
    }

    override fun getDefaultList(num:Int): List<Preferencia> {
        TODO("Not yet implemented")
    }

    override fun getListByIds(idList: List<Int>): List<Preferencia> {
        TODO("Not yet implemented")
    }

    override fun postIndividual(newObject: Preferencia): Preferencia {
        val dbConnection = DatabaseConnection()
        return dbConnection.crearPreferencias(newObject)
    }

    override fun put(modifiedObject: Preferencia): Preferencia {
        val dbConnection = DatabaseConnection()
        return dbConnection.actualizarPreferencias(modifiedObject)
    }

    override fun responseToJson(): String {
        TODO("Not yet implemented")
    }
}