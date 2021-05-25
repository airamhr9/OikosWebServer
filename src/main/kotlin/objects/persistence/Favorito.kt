package objects.persistence

import com.google.gson.JsonObject
import objects.ElementoVisitado
import objects.Visitante

class Favorito(
    var usuario: Usuario,
    var inmueble: Inmueble,
    var notas: String,
    var orden: Int
) : ElementoVisitado {

    companion object {
        fun fromJson(jsonObject: JsonObject): Favorito {
            val usuario = Usuario.fromJson(jsonObject.getAsJsonObject("usuario"))

            val inmuebleJsonObject = jsonObject.getAsJsonObject("inmueble")
            val modeloInmueble = ModeloInmueble.fromString(inmuebleJsonObject.get("modelo").asString)
            val inmueble = when (modeloInmueble) {
                ModeloInmueble.Piso -> Piso.fromJson(inmuebleJsonObject)
                ModeloInmueble.Local -> Local.fromJson(inmuebleJsonObject)
                ModeloInmueble.Garaje -> Garaje.fromJson(inmuebleJsonObject)
                ModeloInmueble.Habitacion -> Habitacion.fromJson(inmuebleJsonObject)
            }

            val notas = jsonObject.get("notas").asString
            val orden = jsonObject.get("orden").asInt

            return Favorito(usuario, inmueble, notas, orden)
        }
    }

    override fun accept(v: Visitante) {
        v.visitFavorito(this)
    }
}