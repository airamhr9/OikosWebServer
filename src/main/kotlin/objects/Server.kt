package objects

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.InetAddress

import java.net.DatagramSocket




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