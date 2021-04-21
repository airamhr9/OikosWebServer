package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.ResponseBuilder
import objects.persistence.*
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
                    100
                }
                if("id" in map){
                    response = ResponseBuilder.createObjectResponse(getIndividualById(map["id"].toString().toInt()))
                }
                else if("coordenada" in map){
                    val x = map["x"].toString().toDouble()
                    val y = map["y"].toString().toDouble()
                    response = ResponseBuilder.createListResponse(getListWithCoordinates(limit, x, y), limit)
                }
                else if("filtrada" in map) {
                    val modelo =  map["modelo"].toString()
                    val precioMin = if ("precioMin" in map) map["precioMin"].toString().toDouble() else 0.0
                    val precioMax = if ("precioMax" in map) map["precioMax"].toString().toDouble() else null
                    val supMin = if ("supMin" in map) map["supMin"].toString().toInt() else 0
                    val supMax = if ("supMax" in map) map["supMax"].toString().toInt() else null
                    val habitaciones = if ("habitaciones" in map) map["habitaciones"].toString().toInt() else -1
                    val baños = if ("baños" in map) map["baños"].toString().toInt() else -1
                    val garaje = if ("garaje" in map) map["garaje"].toString().toBoolean() else null
                    val ciudad = if ("ciudad" in map) map["ciudad"].toString() else null
                    val tipo = if ("tipo" in map) map["tipo"].toString() else null
                    val numComp = if ("numCompañeros" in map) map["numCompañeros"].toString().toInt() else null
                    response = ResponseBuilder.createListResponse(
                        getListWithFilters(limit, precioMin, precioMax, supMin, supMax, habitaciones, baños,
                                            garaje, ciudad, tipo, ModeloInmueble.fromString(modelo),numComp),
                        limit)
                }
                else{
                    response = ResponseBuilder.createListResponse(getDefaultList(limit), limit)
                }
            }
            "POST" -> {
                response = "POST request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())
                print(JsonParser.parseReader(reader).asJsonObject)

                if(false){val local = Local.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    postLocal(local)
                }
                else if(false){val habitacion = Habitacion.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    postHabitacion(habitacion)
                }
                else if(false){val garaje = Garaje.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    postGaraje(garaje)
                }
                else if(true){val piso = Piso.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    postPiso(piso)
                }
                //val inmueble = Inmueble.fromJson(JsonParser.parseReader(reader).asJsonObject)
                //postIndividual(inmueble)
            }
            "PUT" -> {
                response = "PUT request"
                val objectToPut = exchange.requestBody
                //put(Inmueble.fromJson(objectToPut))
            }
            "DELETE" -> {
                response = "DELETE request"
                val url = exchange.requestURI.toString()
                val id=url.substring(14).toInt();
                borrarInmueble(id)
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
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorDefecto(num)
    }

    fun getListWithCoordinates(num:Int, x:Double, y:Double): List<Inmueble> {
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorCordenadas(num, x, y)
    }
    fun getListWithFilters(num: Int , precioMin: Double, precioMax: Double?, supMin: Int, supMan: Int?,
                           habitaciones: Int, baños: Int, garaje: Boolean?, ciudad: String?, tipo: String?,modelo: ModeloInmueble,numComp:Int?): List<Inmueble> {
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorFiltrado(num, precioMin, precioMax, supMin, supMan, habitaciones, baños,
            garaje, ciudad, tipo,modelo,numComp)
    }
    fun borrarInmueble(id:Int){
        val dbConnection = DatabaseConnection()
        return dbConnection.borrarIn(id)
    }

    fun postLocal(newObject: Local) :Local{
        TODO("Not yet implemented")
    }
    fun postHabitacion(newObject:Habitacion) :Habitacion{
        TODO("Not yet implemented")
    }
    fun postGaraje(newObject: Garaje) :Garaje{
        TODO("Not yet implemented")
    }
    fun postPiso(newObject: Piso) :Piso{
        TODO("Not yet implemented")
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