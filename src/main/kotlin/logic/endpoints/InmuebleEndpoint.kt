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
                    if(map["modelo"]=="garaje"){
                        response = ResponseBuilder.createObjectResponse(getGarajeById(map["id"].toString().toInt()))}
                    else if(map["modelo"]=="habitacion"){
                        response = ResponseBuilder.createObjectResponse(getHabitacionById(map["id"].toString().toInt()))}
                    else if(map["modelo"]=="local"){
                        response = ResponseBuilder.createObjectResponse(getLocalById(map["id"].toString().toInt()))}
                    else if(map["modelo"]=="piso"){
                        response = ResponseBuilder.createObjectResponse(getPisoById(map["id"].toString().toInt()))}
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
                                            garaje, ciudad, tipo, ModeloInmueble.fromString(modelo),numComp), limit)
                }
                else if ("propietario" in map) {
                    val idPropietario = map["propietario"].toString().toInt()
                    response = ResponseBuilder.createListResponse(getInmueblesDeUsuario(idPropietario), limit)
                }
                else {
                    response = ResponseBuilder.createListResponse(getDefaultList(limit), limit)
                }
            }
            "POST" -> {
                println("IN POST")
                response = "POST request"
                val objectToPost = exchange.requestBody
                val reader = BufferedReader(objectToPost.reader())
                val url = exchange.requestURI.toString()
                val modelo=url.substring(22)
                println("MODELO " + modelo)

                when(modelo){
                    "local" -> {val local = Local.fromJson(JsonParser.parseReader(reader).asJsonObject)
                        postLocal(local)
                    }
                    "habitacion" -> {val habitacion = Habitacion.fromJson(JsonParser.parseReader(reader).asJsonObject)
                        postHabitacion(habitacion)
                    }
                    "garaje" -> {val garaje = Garaje.fromJson(JsonParser.parseReader(reader).asJsonObject)
                        postGaraje(garaje)
                    }
                    "piso" -> {val piso = Piso.fromJson(JsonParser.parseReader(reader).asJsonObject)
                        postPiso(piso)
                        println("post piso endpoint")
                    }
                    else -> println("No es modelo")
                }
            }
            "PUT" -> {
                response = "PUT request"
                val objectToPut = exchange.requestBody
                val reader = BufferedReader(objectToPut.reader())
                val url = exchange.requestURI.toString()
                val modelo=url.substring(22)

                if(modelo=="local"){val local = Local.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    local.imagenes = local.imagenes.map { it.split("/")[5] }.toTypedArray()
                    putLocal(local)
                }
                else if(modelo=="habitacion"){val habitacion = Habitacion.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    habitacion.imagenes = habitacion.imagenes.map { it.split("/")[5] }.toTypedArray()
                    putHabitacion(habitacion)
                }
                else if(modelo=="garaje"){val garaje = Garaje.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    garaje.imagenes = garaje.imagenes.map { it.split("/")[5] }.toTypedArray()
                    putGaraje(garaje)
                }
                else if(modelo=="piso"){val piso = Piso.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    piso.imagenes = piso.imagenes.map { it.split("/")[5] }.toTypedArray()
                    putPiso(piso)
                }
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
    fun getLocalById(objectId: Int): Local {
        val dbConnection = DatabaseConnection()
        return dbConnection.getLocalById(objectId)
    }
    fun getGarajeById(objectId: Int): Garaje {
        val dbConnection = DatabaseConnection()
        return dbConnection.getGarajeById(objectId)
    }
    fun getPisoById(objectId: Int): Piso {
        val dbConnection = DatabaseConnection()
        return dbConnection.getPisoById(objectId)
    }
    fun getHabitacionById(objectId: Int): Habitacion {
        val dbConnection = DatabaseConnection()
        return dbConnection.getHabitacionById(objectId)
    }

    override fun getDefaultList(num:Int): List<Inmueble> {
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorDefecto(num)
    }

    fun getListWithCoordinates(num:Int, x:Double, y:Double): List<InmuebleSprint2> {
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorCordenadas(num, x, y)
    }
    fun getListWithFilters(num: Int , precioMin: Double, precioMax: Double?, supMin: Int, supMan: Int?,
                           habitaciones: Int, baños: Int, garaje: Boolean?, ciudad: String?, tipo: String?,modelo:
                           ModeloInmueble,numComp:Int?): List<InmuebleSprint2> {
        val dbConnection = DatabaseConnection()
        return dbConnection.listaDeInmueblesPorFiltrado(num, precioMin, precioMax, supMin, supMan, habitaciones, baños,
            garaje, ciudad, tipo,modelo,numComp)
    }
    fun borrarInmueble(id:Int){
        val dbConnection = DatabaseConnection()
        return dbConnection.borrarIn(id)
    }

    fun postLocal(newObject: Local){
        val dbConnection = DatabaseConnection()
        return dbConnection.crearLocal(newObject)
    }
    fun postHabitacion(newObject:Habitacion){
        val dbConnection = DatabaseConnection()
        return dbConnection.crearHabitacion(newObject)
    }
    fun postGaraje(newObject: Garaje){
        val dbConnection = DatabaseConnection()
        return dbConnection.crearGaraje(newObject)
    }
    fun postPiso(newObject: Piso){
        println("Posteando piso")
        val dbConnection = DatabaseConnection()
        return dbConnection.crearPiso(newObject)
    }
    fun putLocal(newObject: Local){
        val dbConnection = DatabaseConnection()
        return dbConnection.actualizarLocal(newObject)
    }
    fun putHabitacion(newObject:Habitacion){
        val dbConnection = DatabaseConnection()
        return dbConnection.actualizarHabitacion(newObject)
    }
    fun putGaraje(newObject: Garaje){
        val dbConnection = DatabaseConnection()
        return dbConnection.actualizarGaraje(newObject)
    }
    fun putPiso(newObject: Piso){
        val dbConnection = DatabaseConnection()
        return dbConnection.actualizarPiso(newObject)
    }

    fun getInmueblesDeUsuario(idUsuario: Int): List<InmuebleSprint2> {
        val dbConnection = DatabaseConnection()
        return dbConnection.getInmueblesDeUsuario(idUsuario)
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