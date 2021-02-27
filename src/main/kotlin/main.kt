import objects.Server
import com.sun.net.httpserver.HttpExchange
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import logic.endpoints.UserEndpoint

fun main(args: Array<String>){
    val serverPort = args[0].toInt()
    val server = Server(serverPort)

    //http://localhost:9000/api/user/
    server.addEndpoint("/api/user/") {
            exchange: HttpExchange -> GlobalScope.launch {
                UserEndpoint("/api/user").handleExchange(exchange)
            }
    }

    server.start()
}


