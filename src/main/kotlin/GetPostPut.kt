import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

fun main(){
    val serverPort : Int = 9000
    val server = HttpServer.create(InetSocketAddress(serverPort), 0)
    //Así es como se escriben las funciones anónimas en Kotlin
    server.createContext("/api/hello") { exchange: HttpExchange -> handleResp(exchange) }
    server.executor = null
    server.start()
}
fun handleResp(exchange : HttpExchange){
    if("GET" == exchange.requestMethod){
        val response = "GET METHOD"
        exchange.sendResponseHeaders(200, response.toByteArray(Charsets.UTF_8).size.toLong())
        val outputStream = exchange.responseBody
        outputStream.write(response.toByteArray())
        outputStream.flush()
    }
    else{
        //405 Método no soportado
        exchange.sendResponseHeaders(405, -1)
    }
    exchange.close()
}