import objects.Server
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import logic.endpoints.UserEndpoint
import java.net.URL
import javax.xml.bind.JAXBElement

/**
 * @param args El primer elemento será el puerto en el que escuchará
 */
fun main(args: Array<String>){
    val serverPort = args[0].toInt()
    val server = Server(serverPort)

    //https://localhost:9000/api/user/
    server.addEndpoint("/api/user/") {
            exchange: HttpExchange -> UserEndpoint("/api/user").handleExchange(exchange)
    }


    server.start()
}

