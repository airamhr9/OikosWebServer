package logic.endpoints

import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.Respuesta
import java.io.File
import java.lang.Exception
import java.lang.UnsupportedOperationException
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ImageEndpoint(endpoint: String, private val folderName: String) : EndpointHandler(endpoint) {

    // http://ip:9000/api/image/nombre_de_archivo.jpg

    override fun getMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        val stringUri = exchange.requestURI.toString()
        try{
            val imageName = stringUri.substring(11, stringUri.length)
            if(imageName.isEmpty()) throw Exception()
            val image = File(folderName, imageName)
            if(!image.exists()) throw Exception()
            exchange.sendResponseHeaders(200, image.length())
            val outputStream = exchange.responseBody;
            Files.copy(image.toPath(), outputStream);
            outputStream.flush()
            exchange.close()
            return

        } catch (e : Exception) {
            respuesta.response = "Not an image"
        }
    }

    override fun postMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        val params : Map<String, Any?> = RequestParser.getQueryParameters(URL(
            "http://"+ exchange.requestHeaders.getFirst("Host") + exchange.requestURI))
        val images = exchange.requestBody
        val file = File(folderName, params["name"].toString())
        Files.copy(images, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        respuesta.response = "OK"
    }

    override fun putMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        throw UnsupportedOperationException()
    }

    override fun deleteMethod(exchange: HttpExchange, respuesta: Respuesta) {
        throw UnsupportedOperationException()
    }
}
