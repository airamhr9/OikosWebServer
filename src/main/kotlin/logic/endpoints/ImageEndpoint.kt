package logic.endpoints

import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.Respuesta
import java.io.File
import java.io.OutputStream
import java.lang.Exception
import java.lang.UnsupportedOperationException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ImageEndpoint(endpoint: String, private val folderName: String) : EndpointHandler(endpoint) {

    // http://ip:9000/api/image/nombre_de_archivo.jpg

    private var image : File? = null
    private var requestMethod = ""

    override fun getMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        requestMethod = "GET"
        val stringUri = exchange.requestURI.toString()
        try {
            val imageName = stringUri.substring(11, stringUri.length)
            if (imageName.isEmpty()) throw Exception()
            image = File(folderName, imageName)
            if (!image!!.exists()) {
                image = null
                throw Exception()
            }
        } catch (e: Exception) {
            respuesta.response = "Not an image"
            respuesta.codigoRespuesta = 404
        }
    }

    override fun postMethod(exchange: HttpExchange, params: Map<String, Any?>, respuesta: Respuesta) {
        requestMethod = "POST"
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

    override fun sendResponseHeaders(exchange: HttpExchange, respuesta: Respuesta) {
        if (exchange.requestMethod == "GET" && image != null) {
            exchange.sendResponseHeaders(200, image!!.length())
        } else {
            exchange.sendResponseHeaders(respuesta.codigoRespuesta,
                respuesta.response.toByteArray(Charsets.UTF_8).size.toLong())
        }
    }

    override fun writeOutputStream(outputStream: OutputStream, respuesta: Respuesta) {
        if (requestMethod == "GET" && image != null) {
            Files.copy(image!!.toPath(), outputStream)
        } else {
            outputStream.write(respuesta.response.toByteArray())
        }
    }
}
