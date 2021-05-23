package logic.endpoints

import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.Respuesta

class VisitasEndpoint(endpoint: String) : EndpointHandler(endpoint) {

    override fun getMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        throw UnsupportedOperationException()
    }

    override fun postMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        throw UnsupportedOperationException()
    }

    override fun putMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        val idInmueble = params["id"].toString().toInt()
        actualizarContador(idInmueble)
        respuesta.response = "Contador actualizado"
    }

    override fun deleteMethod(exchange: HttpExchange, respuesta: Respuesta) {
        throw UnsupportedOperationException()
    }

    private fun actualizarContador(idInmueble: Int) {
        var inmueble = databaseConnection.getInmuebleById(idInmueble)
        inmueble.contadorVisitas++
        databaseConnection.actualizarInmueble(inmueble)
    }
}