import objects.Server
import com.sun.net.httpserver.HttpExchange
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import logic.endpoints.InmuebleEndpoint
import logic.endpoints.UserEndpoint
import persistence.DatabaseConnection

fun main(args: Array<String>){
    val serverPort = args[0].toInt()
    val server = Server(serverPort)

    //http://localhost:9000/api/user/
    server.addEndpoint("/api/user/") {
            exchange: HttpExchange -> GlobalScope.launch {
                UserEndpoint("/api/user").handleExchange(exchange)
            }
    }

    server.addEndpoint("/api/inmueble/") {
            exchange: HttpExchange -> GlobalScope.launch {
        InmuebleEndpoint("/api/inmueble").handleExchange(exchange)
    }
    }

    server.start()
}


