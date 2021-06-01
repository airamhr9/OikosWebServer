package logic.endpoints

import com.sun.net.httpserver.HttpExchange
import logic.Respuesta
import logic.RespuestaEndpointHandler

/*
    Este endpoint es innecesario, puesto que su funcionalidad se podria implementar con un PUT de Inmueble.
    Sin embargo, de este modo se tienen que enviar menos datos (no se envia el inmueble entero, solo su id)
    y se consume menos ancho de banda
*/
class VisitasEndpoint(endpoint: String) : RespuestaEndpointHandler(endpoint) {

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
        val inmueble = databaseConnection.getInmuebleById(idInmueble)
        inmueble.contadorVisitas++
        databaseConnection.actualizarInmueble(inmueble)
    }
}