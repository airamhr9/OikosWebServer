import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

fun main(){
    val serverPort : Int = 9000
    val server = HttpServer.create(InetSocketAddress(serverPort), 0)
    //Así es como se escriben las funciones anónimas en Kotlin
    server.createContext("/api/hello") { exchange: HttpExchange -> helloResponse(exchange) }
    server.executor = null
    server.start()
}
fun helloResponse(exchange : HttpExchange){
    val response = "Hello"
    exchange.sendResponseHeaders(200, response.toByteArray(Charsets.UTF_8).size.toLong())
    val outputStream = exchange.responseBody
    outputStream.write(response.toByteArray())
    outputStream.flush()
    exchange.close()
}