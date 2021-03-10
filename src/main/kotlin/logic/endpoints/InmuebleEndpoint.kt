package logic.endpoints

import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import objects.persistence.Inmueble

class InmuebleEndpoint(endpoint: String) : EndpointHandler<Inmueble>(endpoint) {

    override fun handleExchange(exchange: HttpExchange) {
        lateinit var response : String
        when(exchange.requestMethod){
            "GET" -> {
                response = "GET request"
                val map : Map<String, Any?> = RequestParser.getQueryParameters(exchange.requestURI)
                val limit : Int
                if("limit" in map){ limit = map["limit"].toString().toInt() } else {limit = 20}


                if("id" in map){
                    getIndividualById(map["id"].toString().toInt())
                }
                else if("x" in map){
                    val x= map["x"].toString().toDouble()
                    val y= map["y"].toString().toDouble()

                }

                else{
                    getDefaultList(limit)
                }

            }
            "POST" -> {
                response = "POST request"
                val objectToPost = exchange.requestBody
               /* val reader = BufferedReader(objectToPost.reader())
                var content: String
                try {
                    content = reader.readText()
                } finally {
                    reader.close()
                }

                postIndividual(Inmueble.fromJson(content))

                */
            }
            "PUT" -> {
                response = "PUT request"
                val objectToPut = exchange.requestBody
                //put(Inmueble.fromJson(objectToPut))
            }
            "OPTIONS" -> {
                /**
                 * Endpoint que devuelve un objeto que define los
                 * métodos de petición
                 * Por ejemplo:
                 * Para este endpoint devolvería "GET" "POST" Y "PUT", con los parámetros necesarios de cada uno
                 */
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