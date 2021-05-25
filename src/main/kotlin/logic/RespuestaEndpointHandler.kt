package logic

import com.sun.net.httpserver.HttpExchange
import java.io.OutputStream

abstract class RespuestaEndpointHandler(endpoint: String) : EndpointHandler(endpoint) {

    override fun sendResponseHeaders(exchange: HttpExchange, respuesta: Respuesta) {
        exchange.sendResponseHeaders(respuesta.codigoRespuesta,
            respuesta.response.toByteArray(Charsets.UTF_8).size.toLong())
    }

    override fun writeOutputStream(outputStream: OutputStream, respuesta: Respuesta) {
        outputStream.write(respuesta.response.toByteArray())
    }

}