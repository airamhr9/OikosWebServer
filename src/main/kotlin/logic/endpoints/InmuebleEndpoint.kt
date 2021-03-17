package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.ResponseBuilder
import objects.persistence.Inmueble
import objects.persistence.Usuario
import persistence.DatabaseConnection
import java.io.BufferedReader
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
                else if("coordenada" in map){
                    val x = map["x"].toString().toDouble()
                    val y = map["y"].toString().toDouble()
                    response = ResponseBuilder.createListResponse(getListWithCoordinates(limit, x, y), limit)
                }
                else if("filtrada" in map){
                    //Sin terminar
                    val precioMin = if("precioMin" in map) map["precioMin"].toString().toDouble() else 0.0
                    val  precioMax = map["precioMax"].toString().toDouble()
                    val  supMin = map["supMin"].toString().toInt()
                    val  supMax = map["supMax"].toString().toInt()
                    val  habitaciones = map["habitaciones"].toString().toInt()
                    val  baños = map["baños"].toString().toInt()
                    val  garaje = map["garaje"].toString().toBoolean()
                    val  ciudad = map["ciudad"].toString()
                    val  tipo = map["tipo"].toString()

                    response = ResponseBuilder.createListResponse(
                        getListWithFilters(limit, precioMin,precioMax,supMin,supMax,habitaciones,baños,garaje,ciudad,tipo),limit)
                }
                else{
                    response = ResponseBuilder.createListResponse(getDefaultList(limit), limit)
                }
            }
            "POST" -> {
                response = "POST request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())

                val inmueble = Inmueble.fromJson(JsonParser.parseReader(reader).asJsonObject)
                postIndividual(inmueble)
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
    fun getListWithFilters(num:Int ,precioMin:Double?,precioMax:Double?,supMin:Int?,supMan:Int?,habitaciones: Int?,baños: Int?,garaje: Boolean?,ciudad: String?,tipo:String?): List<Inmueble> {
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorFiltrado(num, precioMin,precioMax,supMin,supMan, habitaciones,baños,garaje,ciudad,tipo)
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