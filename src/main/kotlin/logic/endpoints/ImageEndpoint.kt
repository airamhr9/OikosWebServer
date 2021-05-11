package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import objects.persistence.Inmueble
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ImageEndpoint(endpoint: String, private val folderName : String) : EndpointHandler<Inmueble>(endpoint) {
    //http://ip:9000/api/image/nombre_de_archivo.jpg
    override fun handleExchange(exchange: HttpExchange) {
        lateinit var response : String
        when(exchange.requestMethod){
            "GET" -> {
                val stringUri = exchange.requestURI.toString()
                try{
                    //TODO(mejorar esto que está feo)
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
                    response = "Not an image"
                }
            }
            "POST" -> {
                val params : Map<String, Any?> = RequestParser.getQueryParameters(URL("http://"+ exchange.requestHeaders.getFirst("Host") + exchange.requestURI))
                val images = exchange.requestBody
                val file = File(folderName, params["name"].toString())
                Files.copy(images, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                response = "OK"
            }
            "OPTIONS" -> {
                response = "OPTIONS request"
            }
            else -> {
                //405 Método no soportado
                exchange.sendResponseHeaders(405, -1)
                response = "Method not supported"
            }
        }
        exchange.sendResponseHeaders(200, response.toByteArray(Charsets.UTF_8).size.toLong())
        val outputStream = exchange.responseBody
        outputStream.write(response.toByteArray())
        outputStream.flush()
        exchange.close()
    }

    override fun getIndividualById(objectId: Int): Inmueble {
        TODO("Not yet implemented")
    }

    override fun getDefaultList(num:Int): List<Inmueble> {
        TODO("Not yet implemented")
    }

    override fun getListByIds(idList: List<Int>): List<Inmueble> {
        TODO("Not yet implemented")
    }

    override fun postIndividual(newObject : Inmueble): Inmueble {
        TODO("Not yet implemented")
    }

    override fun put(modifiedObject: Inmueble): Inmueble {
        TODO("Not yet implemented")
    }

    override fun responseToJson(): String {
        TODO("Not yet implemented")
    }
}
