package objects.persistence

import persistence.DatabaseConnection
import java.sql.ResultSet

class FabricaInmueble private constructor() {

    companion object {
        private val databaseConnection = DatabaseConnection()

        fun crearInmueble(resultSet: ResultSet, modelo: ModeloInmueble): InmuebleSprint2 {
            val id = resultSet.getInt("id")
            val disponible = resultSet.getBoolean("disponible")
            val tipo = TipoInmueble.fromString(resultSet.getString("tipo"))
            val superficie = resultSet.getInt("superficie")
            val precio = resultSet.getDouble("precio")
            val propietario = databaseConnection.getUsuarioById(resultSet.getInt("propietario"))
            val descripcion = resultSet.getString("descripcion")
            val direccion = resultSet.getString("direccion")
            val ciudad = resultSet.getString("ciudad")
            val latitud = resultSet.getDouble("latitud")
            val longitud = resultSet.getDouble("longitud")
            val imagenes = databaseConnection.getImagenesDeInmueble(resultSet.getInt("id"))
            val fecha = resultSet.getString("fecha")
            val contadorVisitas = resultSet.getInt("contadorVisitas")

            return when (modelo) {
                ModeloInmueble.Piso -> {
                    val habitaciones = resultSet.getInt("habitaciones")
                    val baños = resultSet.getInt("baños")
                    val garaje = resultSet.getBoolean("garaje")
                    Piso(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                        ciudad, latitud, longitud, imagenes.toTypedArray(), fecha, contadorVisitas, habitaciones, baños, garaje)
                }
                ModeloInmueble.Local -> {
                    val baños = resultSet.getInt("baños")
                    Local(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                        ciudad, latitud, longitud, imagenes.toTypedArray(), fecha, contadorVisitas, baños)
                }
                ModeloInmueble.Garaje -> {
                    Garaje(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                        ciudad, latitud, longitud, imagenes.toTypedArray(), fecha, contadorVisitas)
                }
                ModeloInmueble.Habitacion -> {
                    val habitaciones = resultSet.getInt("habitaciones")
                    val baños = resultSet.getInt("baños")
                    val garaje = resultSet.getBoolean("garaje")
                    val numCompañeros = resultSet.getInt("numCompañeros")
                    Habitacion(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                        ciudad, latitud, longitud, imagenes.toTypedArray(), fecha, contadorVisitas, habitaciones, baños,
                        garaje, numCompañeros)
                }
            }
        }
    }

}