package logic

import java.net.URI
import java.net.URLDecoder
import java.util.*
import kotlin.collections.LinkedHashMap

class RequestParser {
    companion object{

        fun getQueryParameters(request: URI) : Map<String, Any?>{
            val initialScan = initialQueryScan(request)
            return removeUselessLists(initialScan)
        }

        //Devuelve una lista por si hay parámetros repetidos en la url
        private fun initialQueryScan(request : URI): Map<String, MutableList<String?>> {
            val queryPairs: MutableMap<String, MutableList<String?>> = LinkedHashMap()
            val pairs = request.toString().split("&").toTypedArray()
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

        private fun removeUselessLists(initialScan : Map<String, MutableList<String?>>) : Map<String, Any?>{
            val result : MutableMap<String, Any?> = LinkedHashMap()
            val keys = initialScan.keys
            for(key in keys){
                if (initialScan[key]?.size == 1)
                    result[key] = initialScan[key]?.first()
                else
                    result[key] = initialScan[key]
            }
            return result
        }

    }
}