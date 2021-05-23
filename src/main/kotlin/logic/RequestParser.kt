package logic

import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.util.*
import kotlin.collections.LinkedHashMap

class RequestParser {
    companion object{

        fun getQueryParameters(request: URL) : Map<String, Any?>{
            try {
                val initialScan = initialQueryScan(request)
                return processMap(initialScan)
            } catch (e: Exception) {
                return mutableMapOf<String, Any?>()
            }
        }

        //Devuelve una lista por si hay parámetros repetidos en la url
        private fun initialQueryScan(url: URL): Map<String, MutableList<String?>> {
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



        fun processMap(queryMap : Map<String, MutableList<String?>>) : MutableMap<String, Any?>{
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


    }
}