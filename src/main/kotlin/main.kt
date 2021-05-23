import objects.Server
import com.sun.net.httpserver.HttpExchange
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import logic.endpoints.*
import java.io.File

fun main(args: Array<String>) {
    val serverPort = args[0].toInt()
    val folderName = args[1]
    val imageFolder = createImageFolder(folderName)
    val server = Server(serverPort)

    // http://localhost:9000/api/user/
    server.addEndpoint("/api/user/") {
        exchange: HttpExchange -> GlobalScope.launch {
            UserEndpoint("/api/user").handleExchange(exchange)
        }
    }

    server.addEndpoint("/api/image" ){
        exchange: HttpExchange -> GlobalScope.launch {
            ImageEndpoint("/api/image", imageFolder).handleExchange(exchange)
        }
    }

    server.addEndpoint("/api/inmueble/") {
        exchange: HttpExchange -> GlobalScope.launch {
            InmuebleEndpoint("/api/inmueble").handleExchange(exchange)
        }
    }

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

    server.addEndpoint("/api/visitas/") {
        exchange: HttpExchange -> GlobalScope.launch {
            VisitasEndpoint("/api/visitas").handleExchange(exchange)
        }
    }

    server.start()
}

fun createImageFolder(folderName: String): String {
    val homeDir = System.getProperty("user.home")
    val imageFolder = File(homeDir, folderName)
    if (!imageFolder.exists()) {
        imageFolder.mkdirs()
        println("Carpeta de imágenes creada")
    }
    println("Imágenes: ${imageFolder.absolutePath}")
    return imageFolder.absolutePath
}

