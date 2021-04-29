package logic.endpoints

import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import logic.EndpointHandler
import logic.RequestParser
import logic.ResponseBuilder
import objects.persistence.Preferencia
import objects.persistence.Usuario
import persistence.DatabaseConnection
import java.io.BufferedReader
import java.net.URL


class UserEndpoint(endpoint: String) : EndpointHandler<Usuario>(endpoint) {

    override fun handleExchange(exchange: HttpExchange) {
        println("handling exchange")
        lateinit var response : String
         when(exchange.requestMethod){
                "GET" -> {
                    response = "GET request"
                    val map : Map<String, Any?> = RequestParser.getQueryParameters(URL("http://"+ exchange.requestHeaders.getFirst("Host") + exchange.requestURI))

                    if("nombre" in map){
                        if(existeUsuario(map["nombre"].toString(), map["contraseña"].toString())){
                            response = "Usuario encontrado"
                        }else{
                            exchange.sendResponseHeaders(305, -1)
                            response = "Usuario no encontrado"
                        }
                    }
                }
                "POST" -> {
                    response = "POST request"
                    val objectToPost = exchange.requestBody
                    val reader = BufferedReader(objectToPost.reader())
                    val usuario  = Usuario.fromJson(JsonParser.parseReader(reader).asJsonObject)
                    if(!emailRepetido(usuario)){
                        postUsuario(usuario)
                    }else{
                        exchange.sendResponseHeaders(305, -1)
                        response = "Email repetido"
                    }

                }
                "PUT" -> {
                    response = "PUT request"
                    val objectToPut = exchange.requestBody
                    //put(Usuario.fromJson(objectToPut))
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

    override fun getIndividualById(objectId: Int): Usuario {
        TODO("Not yet implemented")
    }

    override fun getDefaultList(num:Int): List<Usuario> {
        TODO("Not yet implemented")
    }

    override fun getListByIds(idList: List<Int>): List<Usuario> {
        TODO("Not yet implemented")
    }

    override fun postIndividual(newObject: Usuario):Usuario {
        TODO("Not yet implemented")
    }
    fun postUsuario(newObject: Usuario) {
        val dbConnection = DatabaseConnection()
        dbConnection.crearUsuario(newObject)
    }
    fun emailRepetido(newObject: Usuario):Boolean{
        val dbConnection = DatabaseConnection()
        return dbConnection.revisarEmail(newObject)
    }
    fun existeUsuario(nombre:String, contraseña:String):Boolean{
        val dbConnection = DatabaseConnection()
        return dbConnection.comprobarUsuario(nombre,contraseña)
    }

    override fun put(modifiedObject: Usuario): Usuario {
        TODO("Not yet implemented")
    }

    override fun responseToJson(): String {
        TODO("Not yet implemented")
    }
}