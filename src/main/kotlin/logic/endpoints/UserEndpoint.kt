package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.ResponseBuilder
import logic.Respuesta
import objects.persistence.Usuario
import java.io.BufferedReader

class UserEndpoint(endpoint: String) : EndpointHandler(endpoint) {

    override fun getMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        respuesta.response = "GET request"
        if ("mail" in params) {
            print(params["mail"].toString() + " " + params["contraseña"].toString())
            if (existeUsuario(params["mail"].toString(), params["contraseña"].toString()) != null) {
                respuesta.response = ResponseBuilder.createObjectResponse(
                    existeUsuario(params["mail"].toString(), params["contraseña"].toString())!!)
            } else {
                respuesta.codigoRespuesta = 404
                respuesta.response = "Usuario no encontrado"
            }
        }
    }

    override fun postMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        respuesta.response = "POST request"
        val objectToPost = exchange.requestBody
        println(objectToPost)
        val reader = BufferedReader(objectToPost.reader())
        println(reader)
        val usuario = Usuario.fromJson(JsonParser.parseReader(reader).asJsonObject)
        println(usuario)
        if (!emailRepetido(usuario)) {
            databaseConnection.crearUsuario(usuario)
        } else {
            respuesta.codigoRespuesta = 404
            respuesta.response = "Email repetido"
        }
    }

    override fun putMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        TODO("Not yet implemented")
    }

    override fun deleteMethod(exchange: HttpExchange, respuesta: Respuesta) {
        TODO("Not yet implemented")
    }

    private fun emailRepetido(newObject: Usuario): Boolean {
        return databaseConnection.revisarEmail(newObject)
    }

    private fun existeUsuario(email: String, contraseña: String): Usuario? {
        return databaseConnection.comprobarUsuario(email, contraseña)
    }
}