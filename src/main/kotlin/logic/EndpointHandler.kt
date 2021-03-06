package logic

import com.sun.net.httpserver.HttpExchange
import objects.SearchableById

abstract class EndpointHandler<T : SearchableById>(val endpoint : String) {

    abstract fun handleExchange(exchange: HttpExchange)
    abstract fun getIndividualById(objectId : Int) : T
    abstract fun getDefaultList(num: Int) : List<T>
    abstract fun getListByIds(idList : List<Int> ) : List<T>
    abstract fun postIndividual(newObject : T) : T
    abstract fun put(modifiedObject : T) : T
    abstract fun responseToJson() : String

}