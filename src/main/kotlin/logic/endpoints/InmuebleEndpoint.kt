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
                val parameters : Map<String, Any?> = RequestParser.getQueryParameters(exchange.requestURI)

                for(llave:Map.Entry<String, Any?> in parameters){
                    if(llave.key=="id"){
                        getIndividualById(llave.value.toString().toInt())
                    }
                    else{
                        if(llave.key=="?limit"){
                            getDefaultList(llave.value.toString().toInt())
                        }
                    }
                }
            }
            "POST" -> {
                response = "POST request"
                val objectToPost = exchange.requestBody
                //postIndividual(Inmueble.fromJson(objectToPost))
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
        return inmuebleById(objectId)
    }

    override fun getDefaultList(num:Int): List<Inmueble> {
        return listaDeInmuebles(num)
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