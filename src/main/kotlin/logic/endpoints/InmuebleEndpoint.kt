package logic.endpoints

import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.ResponseBuilder
import objects.persistence.Inmueble
import objects.persistence.Usuario
import persistence.DatabaseConnection
import java.net.URL

class InmuebleEndpoint(endpoint: String) : EndpointHandler<Inmueble>(endpoint) {

    override fun handleExchange(exchange: HttpExchange) {
        lateinit var response : String
        when(exchange.requestMethod) {
            "GET" -> {
                val map : Map<String, Any?> = RequestParser.getQueryParameters(URL("http://"+ exchange.requestHeaders.getFirst("Host") + exchange.requestURI))
                println(map.entries.toString())
                val limit : Int = if("limit" in map){
                    map["limit"].toString().toInt()
                } else {
                    20
                }
                if("id" in map){
                    response = ResponseBuilder.createObjectResponse(getIndividualById(map["id"].toString().toInt()))
                }
                else if("x" in map){
                    val x = map["x"].toString().toDouble()
                    val y = map["y"].toString().toDouble()
                    response = ResponseBuilder.createListResponse(getListWithCoordinates(limit, x, y), limit)
                }
                else if("precio" in map){
                    //response = ResponseBuilder.createListResponse(getListWithFilters(),limit)
                }
                else{
                    response = ResponseBuilder.createListResponse(getDefaultList(limit), limit)
                }
            }
            "POST" -> {
                response = "POST request"
                val objectToPost = exchange.requestBody
               /* val reader = BufferedReader(objectToPost.reader())
                var content: String
                try {
                    content = reader.readText()
                } finally {
                    reader.close()
                }

                postIndividual(Inmueble.fromJson(content))

                */
            }
            "PUT" -> {
                response = "PUT request"
                val objectToPut = exchange.requestBody
                //put(Inmueble.fromJson(objectToPut))
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

    override fun getIndividualById(objectId: Int): Inmueble {
        val dbConnection = DatabaseConnection()
        return dbConnection.inmuebleById(objectId)
    }

    override fun getDefaultList(num:Int): List<Inmueble> {
        TODO("Not yet implemented")
    }

    fun getListWithCoordinates(num:Int, x:Double, y:Double): List<Inmueble> {
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorCordenadas(num, x, y)
    }
    fun getListWithFilters(num:Int,precio: Double,habitaciones: Int,baños: Int,garaje: Boolean,direccion: String): List<Inmueble> {
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorFiltrado(num, precio, habitaciones,baños,garaje,direccion)
    }

    override fun getListByIds(idList: List<Int>): List<Inmueble> {
        TODO("Not yet implemented")
    }

    override fun postIndividual(newObject : Inmueble): Inmueble {
        TODO("Not yet implemented")
    }

    override fun put(modifiedObject: Inmueble): Inmueble {
        TODO("Not yet implemented")
    }

    override fun responseToJson(): String {
        TODO("Not yet implemented")
    }
}