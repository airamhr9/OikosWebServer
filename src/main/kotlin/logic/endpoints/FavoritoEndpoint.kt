package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.ResponseBuilder
import logic.Respuesta
import objects.persistence.Favorito
import java.io.BufferedReader

class FavoritoEndpoint(endpoint: String) : EndpointHandler(endpoint) {

    override fun getMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        val limit = if ("limit" in params) {
            params["limit"].toString().toInt()
        } else {
            100
        }
        val idUsuario = params["usuario"].toString().toInt()
        val favoritos = databaseConnection.getFavoritosDeUsuario(idUsuario)
        respuesta.response = ResponseBuilder.createListResponse(favoritos, limit)
    }

    override fun postMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        respuesta.response = "POST request"
        val objectToPost = exchange.requestBody
        val reader = BufferedReader(objectToPost.reader())
        val favorito = Favorito.fromJson(JsonParser.parseReader(reader).asJsonObject)
        databaseConnection.storeFavorito(favorito)
    }

    override fun putMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        respuesta.response = "PUT request"
        val objectToPost = exchange.requestBody
        val reader = BufferedReader(objectToPost.reader())
        val favorito = Favorito.fromJson(JsonParser.parseReader(reader).asJsonObject)
        databaseConnection.modificarFavorito(favorito)
    }

    override fun deleteMethod(exchange: HttpExchange, respuesta: Respuesta) {
        respuesta.response = "DELETE request"
        val objectToPost = exchange.requestBody
        val reader = BufferedReader(objectToPost.reader())
        val favorito = Favorito.fromJson(JsonParser.parseReader(reader).asJsonObject)
        databaseConnection.eliminarFavorito(favorito)
    }
}