package objects

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import logic.endpoints.UserEndpoint
import java.net.InetSocketAddress

class Server (port : Int = 8000) {
    private val httpServer: HttpServer = HttpServer.create(InetSocketAddress(port), 0)

    fun start(){
        httpServer.executor = null
        httpServer.start()
        println("Server started")
    }

    fun addEndpoint(endpoint : String, callback : HttpHandler) {
        httpServer.createContext(endpoint, callback)
        println("Endpoint added")
    }

}