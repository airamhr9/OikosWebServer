package objects.persistence

import com.google.gson.JsonObject

open class Piso(id: Int,
                disponible: Boolean,
                tipo: TipoInmueble,
                superficie: Int,
                precio: Double,
                propietario: Usuario,
                descripcion: String,
                direccion: String,
                ciudad: String,
                latitud: Double,
                longitud: Double,
                imagenes: Array<String>,
                var habitaciones: Int,
                var baños: Int,
                var garaje: Boolean,
) : InmuebleSprint2(id, disponible, tipo, superficie, precio, propietario, descripcion,
            direccion, ciudad, latitud, longitud, imagenes) {

    override fun toJson(): JsonObject {
        val result = super.toJson()
        result.addProperty("habitaciones", habitaciones)
        result.addProperty("baños", baños)
        result.addProperty("garaje", garaje)
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Piso {
            val id = jsonObject.get("id").asInt
            val disponible = jsonObject.get("disponible").asBoolean
            val tipo = TipoInmueble.fromString(jsonObject.get("tipo").asString)
            val superficie = jsonObject.get("superficie").asInt
            val precio = jsonObject.get("precio").asDouble
            val propietario = Usuario.fromJson(jsonObject.getAsJsonObject("propietario"))
            val descripcion = jsonObject.get("descripcion").asString.toString()
            val direccion = jsonObject.get("direccion").asString
            val ciudad = jsonObject.get("ciudad").asString
            val latitud = jsonObject.get("latitud").asDouble
            val longitud = jsonObject.get("longitud").asDouble

            /* val listaUrlImagenes = jsonObject.getAsJsonArray("imagenes")
            val imagenes:Array<String> = listaUrlImagenes.map { it.asString.split("/")[4] }.toTypedArray() */
            val imagenes = arrayOf("")

            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean

            return Piso(id, disponible, tipo, superficie, precio, propietario, descripcion,
                direccion, ciudad, latitud, longitud, imagenes, habitaciones, baños, garaje)
        }
    }
}