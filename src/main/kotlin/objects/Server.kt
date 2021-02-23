package objects

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class Server (port : Int = 8000) {
    private val httpServer: HttpServer = HttpServer.create(InetSocketAddress(port), 0)

    fun start(){
        httpServer.start()
    }

    fun addEndpoint(endpoint : String, callback : HttpHandler) {
        httpServer.createContext(endpoint, callback)
    }

}