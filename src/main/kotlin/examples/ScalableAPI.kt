package examples/*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.URL
import java.net.URLDecoder
import kotlin.collections.LinkedHashMap
import java.util.LinkedList

/**
 * Inicia el servidor
 * @param args El primer elemento será el puerto en el que escuchará
 */
fun main(args: Array<String>){
    val serverPort = args[0].toInt()
    val server = HttpServer.create(InetSocketAddress(serverPort), 0)
    server.createContext("/api/hello") { //Crea un punto de entrada con url. Por ej: http://127.0.0.1/api/hello
        exchange: HttpExchange -> GlobalScope.launch { //En vez de ejecutarlo en el hilo princial, lanzamos una corrutina
            handleResponseAsync(exchange)
        }
    }
    /*
    *
    * Aquí creamos todos los puntos de acceso con sus manejadores
    *
    * */
    server.executor = null
    server.start()
}

/**
 * Manejador para el endpoint /api/hello
 * */
fun handleResponseAsync(exchange : HttpExchange){
    when(exchange.requestMethod){
        "GET" -> {
            lateinit var response : String
            println(exchange.requestURI.toString())
            //Si hay parámetros, procesarlos
            if(exchange.requestURI.toString() != "/api/hello/"){
                val url = "http://"+ exchange.requestHeaders.getFirst("Host") + exchange.requestURI;
                val processedParameters = processMap2(splitQuery2(URL(url)))
                /*
                * Buscar en la bd el objeto filtrando por los parámetros
                * */
                response = Gson().toJson(processedParameters).toString() //Parametros como objeto JSON porque no hay db para devolver
            }
            else{
                /*
                * Buscar en la bd sin filtrar
                * */
                response = "No parameters were added"
            }
            exchange.sendResponseHeaders(200, response.toByteArray(Charsets.UTF_8).size.toLong())
            val outputStream = exchange.responseBody
            outputStream.write(response.toByteArray())
            outputStream.flush()
        }
        "POST" -> {
            val body = exchange.requestBody
            /*
            * Pasar cuerpo de petición (en JSON) a objeto
            * guardarlo en base de datos
            * */
            val responseJson = JsonObject()
            responseJson.addProperty("201", "Added Successfully")
            val response = responseJson.toString()
            exchange.sendResponseHeaders(201, response.toByteArray(Charsets.UTF_8).size.toLong())
            val outputStream = exchange.responseBody
            outputStream.write(response.toByteArray())
            outputStream.flush()
        }
        "PUT" -> {
            val body = exchange.requestBody
            /*
            * Pasar cuerpo de petición (en JSON) a objeto
            * Actualizar objeto de mismo ID en la base de datos
            * */
            val responseJson = JsonObject() //Cambiar objeto JSON vacío por el objeto actualizado
            val response = responseJson.toString()
            exchange.sendResponseHeaders(201, response.toByteArray(Charsets.UTF_8).size.toLong())
            val outputStream = exchange.responseBody
            outputStream.write(response.toByteArray())
            outputStream.flush()
        }
        "OPTIONS" -> {
            /**
             * Endpoint que devuelve un objeto que define los
             * métodos de petición
             * Por ejemplo:
             * Para este endpoint devolvería "GET" "POST" Y "PUT", con los parámetros necesarios de cada uno
             */
        }
        else -> {
            //405 Método no soportado
            exchange.sendResponseHeaders(405, -1)
        }
    }
    exchange.close()
}

/**
 * Procesa la url para obtener un map con los parámetros que se hayan enviado
 *
 * @param url La url a procesar
 * @return Un map con el nombre del parámetro y una lista de valores (por si se repite el parámetro en la url)
 */
fun splitQuery2(url: URL): Map<String, MutableList<String?>> {
    val queryPairs: MutableMap<String, MutableList<String?>> = LinkedHashMap()
    val pairs = url.query.split("&").toTypedArray()
    for (pair in pairs) {
        val idx = pair.indexOf("=")
        val key = if (idx > 0) URLDecoder.decode(pair.substring(0, idx), "UTF-8") else pair
        if (!queryPairs.containsKey(key)) {
            queryPairs[key] = LinkedList()
        }
        val value = if (idx > 0 && pair.length > idx + 1) URLDecoder.decode(pair.substring(idx + 1), "UTF-8") else null
        queryPairs[key]?.add(value)
    }
    return queryPairs
}

/**
 * Elimina las listas innecesarias en procesamiento
 *
 * @param queryMap El resultado de splitQuery
 * @return Un map con solo las listas necesarias y valores individuales, listo para convertir en JSON o en objeto
 */
fun processMap2(queryMap : Map<String, MutableList<String?>>) : MutableMap<String, Any?>{
    val result : MutableMap<String, Any?> = LinkedHashMap()
    val keys = queryMap.keys
    for(key in keys){
        if (queryMap[key]?.size == 1)
            result[key] = queryMap[key]?.first()
        else
            result[key] = queryMap[key]
    }
    return result
}


*/