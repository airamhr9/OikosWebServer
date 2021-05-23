package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.ResponseBuilder
import logic.Respuesta
import objects.persistence.*
import java.io.BufferedReader

class InmuebleEndpoint(endpoint: String) : EndpointHandler(endpoint) {

    override fun getMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        println(params.entries.toString())
        val limit = if ("limit" in params) {
            params["limit"].toString().toInt()
        } else {
            100
        }
        if ("coordenada" in params) {
            busquedaGeolocalizada(params, respuesta, limit)
        }
        else if ("filtrada" in params) {
            busquedaFiltrada(params, respuesta, limit)
        }
        else {
            val idPropietario = params["propietario"].toString().toInt()
            respuesta.response = ResponseBuilder.createListResponse(
                databaseConnection.getInmueblesDeUsuario(idPropietario), limit)
        }
    }

    override fun postMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        respuesta.response = "POST request"
        val objectToPost = exchange.requestBody
        val reader = BufferedReader(objectToPost.reader())
        val url = exchange.requestURI.toString()
        val modelo = url.substring(22)
        when(modelo){
            "local" -> {
                val local = Local.fromJson(JsonParser.parseReader(reader).asJsonObject)
                databaseConnection.crearLocal(local)
            }
            "habitacion" -> {
                val habitacion = Habitacion.fromJson(JsonParser.parseReader(reader).asJsonObject)
                databaseConnection.crearHabitacion(habitacion)
            }
            "garaje" -> {
                val garaje = Garaje.fromJson(JsonParser.parseReader(reader).asJsonObject)
                databaseConnection.crearGaraje(garaje)
            }
            "piso" -> {
                val piso = Piso.fromJson(JsonParser.parseReader(reader).asJsonObject)
                databaseConnection.crearPiso(piso)
            }
        }
    }

    override fun putMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        respuesta.response = "PUT request"
        val objectToPut = exchange.requestBody
        val reader = BufferedReader(objectToPut.reader())
        val url = exchange.requestURI.toString()
        val modelo = url.substring(22)
        val inmueble = when (modelo) {
            "piso" -> Piso.fromJson(JsonParser.parseReader(reader).asJsonObject)
            "habitacion" -> Habitacion.fromJson(JsonParser.parseReader(reader).asJsonObject)
            "local" -> Local.fromJson(JsonParser.parseReader(reader).asJsonObject)
            else -> Garaje.fromJson(JsonParser.parseReader(reader).asJsonObject)
        }
        inmueble.imagenes = inmueble.imagenes.map {
            val splitted = it.split("/")
            if (splitted.size > 1) {
                splitted[5]
            } else {
                splitted[0]
            }
        }.toTypedArray()
        databaseConnection.actualizarInmueble(inmueble)
    }

    override fun deleteMethod(exchange: HttpExchange, respuesta: Respuesta) {
        respuesta.response = "DELETE request"
        val url = exchange.requestURI.toString()
        val id = url.substring(18).toInt()
        databaseConnection.borrarInmuebleById(id)
    }

    private fun busquedaGeolocalizada(params: Map<String, Any?>, respuesta: Respuesta, limit: Int ) {
        val x = params["x"].toString().toDouble()
        val y = params["y"].toString().toDouble()
        respuesta.response = ResponseBuilder.createListResponse(
            databaseConnection.listaDeInmueblesPorCordenadas(limit, x, y), limit)
    }

    private fun busquedaFiltrada(params: Map<String, Any?>, respuesta: Respuesta, limit: Int) {
        val modelo = params["modelo"].toString()
        val precioMin = if ("precioMin" in params) params["precioMin"].toString().toDouble() else 0.0
        val precioMax = if ("precioMax" in params) params["precioMax"].toString().toDouble() else null
        val superficieMin = if ("supMin" in params) params["supMin"].toString().toInt() else 0
        val superficieMax = if ("supMax" in params) params["supMax"].toString().toInt() else null
        val habitaciones = if ("habitaciones" in params) params["habitaciones"].toString().toInt() else -1
        val baños = if ("baños" in params) params["baños"].toString().toInt() else -1
        val garaje = if ("garaje" in params) params["garaje"].toString().toBoolean() else null
        val ciudad = if ("ciudad" in params) params["ciudad"].toString() else null
        val tipo = if ("tipo" in params) params["tipo"].toString() else null
        val numComp = if ("numCompañeros" in params) params["numCompañeros"].toString().toInt() else null
        val idUsuario = params["usuario"].toString().toInt()

        respuesta.response = ResponseBuilder.createListResponse(
            databaseConnection.listaDeInmueblesPorFiltrado(limit, precioMin, precioMax, superficieMin, superficieMax,
                habitaciones, baños, garaje, ciudad, tipo, ModeloInmueble.fromString(modelo), numComp, idUsuario),
            limit
        )
    }
}