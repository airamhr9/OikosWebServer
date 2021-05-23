package logic.endpoints

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.Respuesta
import java.io.BufferedReader
import java.net.URLDecoder


class BusquedaEndpoint(endpoint: String) : EndpointHandler(endpoint) {

    override fun getMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        respuesta.response = "GET request"
        if ("id" in params) {
            val usuario = params["id"].toString().toInt()
            respuesta.response = databaseConnection.listaDeBusquedasGuardadas(usuario).toString()
        }
    }

    override fun postMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        respuesta.response = "POST request"
        val objectToPost = exchange.requestBody
        val reader = BufferedReader(objectToPost.reader(Charsets.UTF_8))
        var string = reader.readLines().toString()
        string = URLDecoder.decode(string, "UTF-8")
        val url = exchange.requestURI.toString()
        var usuario = url.substring(18).toInt()
        val busquedaJson = JsonParser.parseString(string) as JsonArray
        val busqueda = busquedaJson[0].toString()
        databaseConnection.crearBusquedaGuardada(busqueda, usuario)
    }

    override fun putMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        /*respuesta.response = "PUT request"
        val objectToPost = exchange.requestBody
        val reader = BufferedReader(objectToPost.reader())
        var string: String = reader.readLines().toString()
        string = URLDecoder.decode(string, "UTF-8");
        string = string.substring(7, string.length - 1)*/
        TODO("Not yet implemented")
    }

    override fun deleteMethod(exchange: HttpExchange, respuesta: Respuesta) {
        TODO("Not yet implemented")
    }
}