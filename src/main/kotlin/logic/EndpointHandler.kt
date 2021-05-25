package logic

import com.sun.net.httpserver.HttpExchange
import persistence.DatabaseConnection
import java.io.OutputStream
import java.net.URL

abstract class EndpointHandler(val endpoint: String) {

    protected val databaseConnection = DatabaseConnection.getInstance()

    fun handleExchange(exchange: HttpExchange) {
        val respuesta = Respuesta("", 200)
        val params: Map<String, Any?> = RequestParser.getQueryParameters(URL(
            "http://"+ exchange.requestHeaders.getFirst("Host") + exchange.requestURI))
        when (exchange.requestMethod) {
            "GET" -> getMethod(exchange, params, respuesta)
            "POST" -> postMethod(exchange, params, respuesta)
            "PUT" -> putMethod(exchange, params, respuesta)
            "DELETE" -> deleteMethod(exchange, respuesta)
        }
        sendResponseHeaders(exchange, respuesta)
        val outputStream = exchange.responseBody
        writeOutputStream(outputStream, respuesta)
        outputStream.flush()
        exchange.close()
    }

    protected abstract fun getMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta)
    protected abstract fun postMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta)
    protected abstract fun putMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta)
    protected abstract fun deleteMethod(exchange: HttpExchange, respuesta: Respuesta)

    protected abstract fun sendResponseHeaders(exchange: HttpExchange, respuesta: Respuesta)
    protected abstract fun writeOutputStream(outputStream: OutputStream, respuesta: Respuesta)

}