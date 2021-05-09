import objects.Server
import com.sun.net.httpserver.HttpExchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logic.endpoints.*
import objects.persistence.InmuebleSprint2
import persistence.DatabaseConnection
import java.io.File

fun main(args: Array<String>){
    val serverPort = args[0].toInt()
    val folderName = args[1]
    val imageFolder = createImageFolder(folderName)
    val server = Server(serverPort)
    //InmuebleSprint2.serverPort = serverPort

    //http://localhost:9000/api/user/
    server.addEndpoint("/api/user/") {
        exchange: HttpExchange -> GlobalScope.launch {
            UserEndpoint("/api/user").handleExchange(exchange)
        }
    }

    server.addEndpoint("/api/image"){
        exchange: HttpExchange -> GlobalScope.launch {
            ImageEndpoint("/api/image", imageFolder).handleExchange(exchange)
        }
    }

    server.addEndpoint("/api/inmueble/") {
        exchange: HttpExchange -> GlobalScope.launch {
            InmuebleEndpoint("/api/inmueble").handleExchange(exchange)
        }
    }

    /*server.addEndpoint("/api/preferencias/") {
            exchange: HttpExchange -> GlobalScope.launch {
                PreferenciasEndpoint("/api/preferencias").handleExchange(exchange)
        }
    }*/

    server.addEndpoint("/api/busqueda/") {
            exchange: HttpExchange -> GlobalScope.launch {
            BusquedaEndpoint("/api/busqueda").handleExchange(exchange)
        }
    }

    server.addEndpoint("/api/favorito/") {
        exchange: HttpExchange -> GlobalScope.launch {
            FavoritoEndpoint("/api/favorito").handleExchange(exchange)
        }
    }

    server.addEndpoint("/api/hello/") {
            exchange: HttpExchange -> GlobalScope.launch {
            val response = "Hello bro test ok"
            withContext(Dispatchers.IO){
                exchange.sendResponseHeaders(200, response.toByteArray(Charsets.UTF_8).size.toLong())
                val outputStream = exchange.responseBody
                outputStream.write(response.toByteArray())
                outputStream.flush()
                exchange.close()
            }
        }
    }

    server.start()
}

fun createImageFolder(folderName : String) : String{
    val homeDir = System.getProperty("user.home")
    val imageFolder = File(homeDir, folderName)
    if(!imageFolder.exists()){
        imageFolder.mkdirs()
    }
    return imageFolder.absolutePath
}

