package persistence
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import objects.persistence.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.system.exitProcess

class DatabaseConnection private constructor() {
    private val connection: Connection

    private val databaseURL = "jdbc:postgresql://localhost:5432/postgres" // Hector, Javi, Jaime
    // private val databaseURL = "jdbc:postgresql://172.17.0.2:5432/Oikos" // Airam

    // private val databaseURL = "jdbc:postgresql://localhost:5432/oikos" // Hector antiguo
    // private val databaseURL = "jdbc:postgresql://localhost:5432/testdb" // Jaime antiguo

    init {
        try {
            Class.forName("org.postgresql.Driver")
            connection = DriverManager.getConnection(databaseURL, "postgres", "mysecretpassword")
            connection.autoCommit = false
            println("Opened database successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println(e.javaClass.name + ": " + e.message)
            println("URL de la base de datos: $databaseURL")
            exitProcess(-1)
        }
    }

    companion object {
        private var databaseConnection: DatabaseConnection? = null

        fun getInstance(): DatabaseConnection {
            if (databaseConnection == null) {
                databaseConnection = DatabaseConnection()
            }
            return databaseConnection!!
        }
    }

    private fun getUsuarioFromResultSet(resultSet: ResultSet): Usuario {
        return Usuario(resultSet.getInt("id"), resultSet.getString("nombre"),
            resultSet.getString("email"), resultSet.getString("contraseña"),
            resultSet.getString("imagen"))
    }

    fun getImagenesDeInmueble(idInmueble: Int): List<String> {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM imagen WHERE inmueble = $idInmueble")
        val nombresImagenes = mutableListOf<String>()
        while (resultSet.next()) {
            nombresImagenes.add(resultSet.getString("ruta"))
        }
        return nombresImagenes
    }

    fun listaDeInmueblesPorFiltrado(num:Int, precioMin: Double, precioMax: Double?, supMin: Int, supMax: Int?,
                                    habitaciones: Int, baños: Int, garaje: Boolean?, ciudad: String?, tipo: String?,
                                    modelo:ModeloInmueble,numComp:Int?, idUsuario: Int): List<Inmueble> {
        val statement = connection.createStatement()
        val list = mutableListOf<Inmueble>()
        var query = if (modelo == ModeloInmueble.Habitacion) "SELECT * FROM inmueble NATURAL JOIN piso NATURAL JOIN habitacion WHERE "
                    else "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE "
        query += "disponible = true AND "
        query += "precio >= $precioMin AND "
        if (precioMax != null) query += "precio <= $precioMax AND "
        query += "superficie >= $supMin AND "
        if (supMax != null) query += "superficie <= $supMax AND "
        if(habitaciones >= 0)
        query += "habitaciones = $habitaciones AND "
        if(baños >= 0)
        query += "baños = $baños AND "
        if (garaje == true) query += "garaje = true AND "
        if (ciudad != null) query += "lower(ciudad) = lower(\'$ciudad\') AND "
        if (tipo != null) query += "tipo = \'$tipo\' AND "
        if (numComp != null) query += "numCompañeros >= \'$numComp\' AND "
        query = query.substring(0, query.length - 4) // Quitar el ultimo AND
        query += "FETCH FIRST $num ROWS ONLY;"

        println(query)
        val resultSet = statement.executeQuery(query)

        val inmueblesFavoritosDeUsuario = getFavoritosDeUsuario(idUsuario).map {it.inmueble}
        while (resultSet.next()) {
            val inmueble = FabricaInmueble.crearInmueble(resultSet, modelo)
            if (inmueblesFavoritosDeUsuario.contains(inmueble)) {
                inmueble.esFavorito = true
            }
            list.add(inmueble)
        }
        resultSet.close()
        statement.close()
        return list
    }

    fun listaDeInmueblesPorCordenadas(num:Int, x:Double, y:Double): List<Inmueble> {
        val result = listaDePisosPorCordenadas(num, x, y)
        result.addAll(listaDeLocalesPorCordenadas(num, x, y))
        result.addAll(listaDeGarajesPorCordenadas(num, x, y))
        result.addAll(listaDeHabitacionesPorCordenadas(num, x, y))
        return result
    }

    private fun listaDePisosPorCordenadas(num:Int, x:Double, y:Double): MutableList<Inmueble> {
        val statement = connection.createStatement()
        val list: MutableList<Inmueble> =  mutableListOf()
        val modelo = ModeloInmueble.Piso
        val resultSet = statement.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE disponible = true AND " +
                "(latitud >= $x - 0.2 AND latitud <= $x + 0.2) AND (longitud >= $y - 0.2 AND longitud <= $y + 0.2) " +
                "AND disponible = true FETCH FIRST $num ROWS ONLY;")
        while (resultSet.next()) {
            val inmueble = FabricaInmueble.crearInmueble(resultSet, modelo)
            list.add(inmueble)
        }
        resultSet.close()
        statement.close()
        return list
    }

    private fun listaDeLocalesPorCordenadas(num:Int, x:Double, y:Double): MutableList<Inmueble> {
        val statement = connection.createStatement()
        val list: MutableList<Inmueble> =  mutableListOf()
        val modelo = ModeloInmueble.Local
        val resultSet = statement.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE disponible = true AND " +
                "(latitud >= $x - 0.2 AND latitud <= $x + 0.2) AND (longitud >= $y - 0.2 AND longitud <= $y + 0.2) " +
                "AND disponible = true FETCH FIRST $num ROWS ONLY;")
        while (resultSet.next()) {
            val inmueble = FabricaInmueble.crearInmueble(resultSet, modelo)
            list.add(inmueble)
        }
        resultSet.close()
        statement.close()
        return list
    }

    private fun listaDeGarajesPorCordenadas(num:Int, x:Double, y:Double): MutableList<Inmueble> {
        val statement = connection.createStatement()
        val list: MutableList<Inmueble> =  mutableListOf()
        val modelo = ModeloInmueble.Garaje
        val resultSet = statement.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE disponible = true AND " +
                "(latitud >= $x - 0.2 AND latitud <= $x + 0.2) AND (longitud >= $y - 0.2 AND longitud <= $y + 0.2) " +
                "AND disponible = true FETCH FIRST $num ROWS ONLY;")
        while (resultSet.next()) {
            val inmueble = FabricaInmueble.crearInmueble(resultSet, modelo)
            list.add(inmueble)
        }
        resultSet.close()
        statement.close()
        return list
    }

    private fun listaDeHabitacionesPorCordenadas(num:Int, x:Double, y:Double): MutableList<Inmueble> {
        val statement = connection.createStatement()
        val list: MutableList<Inmueble> =  mutableListOf()
        val modelo = ModeloInmueble.Habitacion
        val resultSet = statement.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${ModeloInmueble.Piso.value} "
                + "NATURAL JOIN ${modelo.value} WHERE disponible = true AND " +
                "(latitud >= $x - 0.2 AND latitud <= $x + 0.2) AND (longitud >= $y - 0.2 AND longitud <= $y + 0.2) " +
                "AND disponible = true FETCH FIRST $num ROWS ONLY;")
        while (resultSet.next()) {
            val inmueble = FabricaInmueble.crearInmueble(resultSet, modelo)
            list.add(inmueble)
        }
        resultSet.close()
        statement.close()
        return list
    }

    fun borrarInmuebleById(id: Int) {
        val statement = connection.createStatement()
        val instruccion = "DELETE from inmueble where id = $id;"
        statement.executeUpdate(instruccion)
        connection.commit()
        statement.close()
    }

    fun getPisoById(id: Int): Piso {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN piso WHERE id=$id;")
        resultSet.next()
        val piso = FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Piso) as Piso
        resultSet.close()
        statement.close()
        return piso
    }

    fun getLocalById(id: Int): Local {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN local WHERE id=$id;")
        resultSet.next()
        val local = FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Local) as Local
        resultSet.close()
        statement.close()
        return local
    }

    fun getGarajeById(id: Int): Garaje {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN garaje WHERE id=$id;")
        resultSet.next()
        val garaje = FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Garaje) as Garaje
        resultSet.close()
        statement.close()
        return garaje
    }

    fun getHabitacionById(id: Int): Habitacion {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN habitacion NATURAL JOIN piso WHERE id=$id;")
        resultSet.next()
        val habitacion = FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Habitacion) as Habitacion
        resultSet.close()
        statement.close()
        return habitacion
    }

    private fun insertarImagenesDeInmueble(inmueble: Inmueble) {
        val statement = connection.createStatement()
        inmueble.imagenes.forEach {
            println(it)
            val instruccion = "INSERT INTO imagen (inmueble, ruta) VALUES (${inmueble.id}, '$it');"
            statement.executeUpdate(instruccion)
        }
        connection.commit()
        statement.close()
    }

    private fun crearInmueble(inmueble: Inmueble) {
        if (inmueble.id == -1) {
            inmueble.id = getNuevoIdDeInmueble()
        }
        val statement = connection.createStatement()
        val instruccion = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad," +
                "latitud, longitud, propietario, fecha, contadorVisitas) " +
                "VALUES (${inmueble.id}, ${inmueble.disponible}, '${inmueble.tipo}', ${inmueble.superficie}, ${inmueble.precio}, " +
                "'${inmueble.descripcion}', '${inmueble.direccion}', '${inmueble.ciudad}', ${inmueble.latitud}, " +
                "${inmueble.longitud}, ${inmueble.propietario.id}, '${inmueble.fecha}', ${inmueble.contadorVisitas});"
        statement.execute(instruccion)
        connection.commit()
        insertarImagenesDeInmueble(inmueble)
        statement.close()
    }

    fun crearLocal(local: Local) {
        val statement = connection.createStatement()
        crearInmueble(local)
        val instruccion = "INSERT INTO local (id, baños) VALUES (${local.id}, ${local.baños});"
        statement.executeUpdate(instruccion)
        connection.commit()
        statement.close()
    }

    fun crearPiso(piso: Piso) {
        val statement = connection.createStatement()
        crearInmueble(piso)
        val instruccion = "INSERT INTO piso (id, habitaciones, baños, garaje) " +
                "VALUES (${piso.id}, ${piso.habitaciones}, ${piso.baños}, ${piso.garaje});"
        statement.executeUpdate(instruccion)
        connection.commit()
        statement.close()
    }

    fun crearHabitacion(habitacion: Habitacion) {
        val statement = connection.createStatement()
        crearInmueble(habitacion)
        val instruccion1 = "INSERT INTO piso (id, habitaciones, baños, garaje) " +
                "VALUES (${habitacion.id}, ${habitacion.habitaciones}, ${habitacion.baños}, ${habitacion.garaje});"
        val instruccion2 = "INSERT INTO habitacion (id, numCompañeros) VALUES (${habitacion.id}, ${habitacion.numCompañeros});"
        statement.executeUpdate(instruccion1)
        statement.executeUpdate(instruccion2)
        connection.commit()
        statement.close()
    }

    fun crearGaraje(garaje: Garaje) {
        val statement = connection.createStatement()
        crearInmueble(garaje)
        val instruccion = "INSERT INTO garaje (id) VALUES (${garaje.id});"
        statement.executeUpdate(instruccion)
        connection.commit()
        statement.close()
    }

    private fun getNuevoIdDeInmueble(): Int {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT max(id) FROM inmueble;")
        resultSet.next()
        var result = resultSet.getInt(1)
        result++
        resultSet.close()
        statement.close()
        return result
    }

    fun crearUsuario(usuario: Usuario) {
        val statement = connection.createStatement()
        val instruccion = "INSERT INTO usuario (nombre, email, contraseña, imagen) " +
                "VALUES ('${usuario.nombre}', '${usuario.mail}','${usuario.contraseña}','${usuario.imagen}');"
        statement.executeUpdate(instruccion)
        connection.commit()
        statement.close()
    }

    fun revisarEmail(usuario: Usuario): Boolean {
        var result = false
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT email FROM usuario WHERE email = '${usuario.mail}';")
        resultSet.next()
        try {
            val email = resultSet.getString("email")
            result = (usuario.mail == email)
        } catch (e: Exception) {
        }
        resultSet.close()
        statement.close()
        return result
    }

    fun comprobarUsuario(email: String, contraseña: String): Usuario? {
        var user: Usuario?
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM usuario WHERE email = '$email' AND contraseña = '$contraseña';")
        resultSet.next()
        try {
            user = getUsuarioFromResultSet(resultSet)
        } catch (e: Exception) {
            user = null
        }
        resultSet.close()
        statement.close()
        return user
    }

    fun getUsuarioById(id: Int): Usuario {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM usuario WHERE id=$id;")
        resultSet.next()
        val usuario = getUsuarioFromResultSet(resultSet)
        resultSet.close()
        statement.close()
        return usuario
    }

    fun getInmueblesDeUsuario(idUsuario: Int): List<Inmueble> {
        val inmuebles = getPisosDeUsuario(idUsuario)
        inmuebles.addAll(getLocalesDeUsuario(idUsuario))
        inmuebles.addAll(getGarajesDeUsuario(idUsuario))
        inmuebles.addAll(getHabitacionesDeUsuario(idUsuario))
        // Para evitar que las habitaciones se repitan (habitacion hereda de piso)
        val map = mutableMapOf<Int, Inmueble>()
        inmuebles.forEach { map[it.id] = it }
        return map.values.toList()
    }

    private fun getPisosDeUsuario(idUsuario: Int): MutableList<Inmueble> {
        val statement = connection.createStatement()
        val result = mutableListOf<Inmueble>()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN piso "
                + "WHERE propietario = $idUsuario")
        while (resultSet.next()) {
            result.add(FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Piso))
        }
        resultSet.close()
        statement.close()
        return result
    }

    private fun getLocalesDeUsuario(idUsuario: Int): MutableList<Inmueble> {
        val statement = connection.createStatement()
        val result = mutableListOf<Inmueble>()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN local "
                + "WHERE propietario = $idUsuario")
        while (resultSet.next()) {
            result.add(FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Local))
        }
        resultSet.close()
        statement.close()
        return result
    }

    private fun getGarajesDeUsuario(idUsuario: Int): MutableList<Inmueble> {
        val statement = connection.createStatement()
        val result = mutableListOf<Inmueble>()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN garaje "
                + "WHERE propietario = $idUsuario")
        while (resultSet.next()) {
            result.add(FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Garaje))
        }
        resultSet.close()
        statement.close()
        return result
    }

    private fun getHabitacionesDeUsuario(idUsuario: Int): MutableList<Inmueble> {
        val statement = connection.createStatement()
        val result = mutableListOf<Inmueble>()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN piso NATURAL JOIN habitacion "
                + "WHERE propietario = $idUsuario")
        while (resultSet.next()) {
            result.add(FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Habitacion))
        }
        resultSet.close()
        statement.close()
        return result
    }

    fun crearBusquedaGuardada(busqueda: String, usuario: Int) {
        val statement = connection.createStatement()
        val instruccion = "INSERT INTO busqueda (usuario, busqueda) VALUES ($usuario, '$busqueda');"
        statement.executeUpdate(instruccion)
        connection.commit()
        statement.close()
    }

    fun listaDeBusquedasGuardadas(usuario: Int): JsonArray {
        val statement = connection.createStatement()
        val list = JsonArray()
        val resultSet = statement.executeQuery("SELECT * FROM busqueda WHERE usuario = $usuario ;")
        while (resultSet.next()) {
            val busqueda = JsonParser.parseString(resultSet.getString("busqueda"))
            list.add(busqueda)
        }
        resultSet.close()
        statement.close()
        return list
    }

    fun storeFavorito(favorito: Favorito) {
        val statement = connection.createStatement()
        val instruccion = "INSERT INTO favorito (usuario, inmueble, notas, orden)" +
                "VALUES (${favorito.usuario.id}, ${favorito.inmueble.id}, '${favorito.notas}', ${favorito.orden});"
        statement.execute(instruccion)
        connection.commit()
        statement.close()
    }

    private fun getModeloInmuebleById(idInmueble: Int): ModeloInmueble {
        return when {
            isHabitacion(idInmueble) -> ModeloInmueble.Habitacion
            isPiso(idInmueble) -> ModeloInmueble.Piso
            isLocal(idInmueble) -> ModeloInmueble.Local
            else -> ModeloInmueble.Garaje
        }
    }

    private fun isPiso(idInmueble: Int): Boolean {
        val statement = connection.createStatement()
        var result: Boolean
        try {
            val resultSet = statement.executeQuery("SELECT * FROM piso WHERE id = $idInmueble;")
            resultSet.next()
            resultSet.getInt("id")
            resultSet.close()
            result = true
        } catch (e: Exception) {
            result = false
        }
        statement.close()
        return result
    }

    private fun isHabitacion(idInmueble: Int): Boolean {
        val statement = connection.createStatement()
        var result: Boolean
        try {
            val resultSet = statement.executeQuery("SELECT * FROM habitacion WHERE id = $idInmueble;")
            resultSet.next()
            resultSet.getInt("id")
            resultSet.close()
            result = true
        } catch (e: Exception) {
            result = false
        }
        statement.close()
        return result
    }

    private fun isLocal(idInmueble: Int): Boolean {
        val statement = connection.createStatement()
        var result: Boolean
        try {
            val resultSet = statement.executeQuery("SELECT * FROM local WHERE id = $idInmueble;")
            resultSet.next()
            resultSet.getInt("id")
            resultSet.close()
            result = true
        } catch (e: Exception) {
            result = false
        }
        statement.close()
        return result
    }

    fun getInmuebleById(id: Int): Inmueble {
        return when (getModeloInmuebleById(id)) {
            ModeloInmueble.Piso -> getPisoById(id)
            ModeloInmueble.Local -> getLocalById(id)
            ModeloInmueble.Garaje -> getGarajeById(id)
            ModeloInmueble.Habitacion -> getHabitacionById(id)
        }
    }

    fun getFavoritosDeUsuario(idUsuario: Int): List<Favorito> {
        val statement = connection.createStatement()
        val query = "SELECT * FROM favorito WHERE usuario = $idUsuario ORDER BY orden;"
        val resultSet = statement.executeQuery(query)
        val favoritos = mutableListOf<Favorito>()
        while(resultSet.next()) {
            val usuario = getUsuarioById(idUsuario)
            val inmueble = getInmuebleById(resultSet.getInt("inmueble"))
            val notas = resultSet.getString("notas")
            val orden = resultSet.getInt("orden")
            val favorito = Favorito(usuario, inmueble, notas, orden)
            favoritos.add(favorito)
        }
        return favoritos
    }

    fun eliminarFavorito(favorito: Favorito) {
        val statement = connection.createStatement()
        val instruccion = "DELETE FROM favorito WHERE usuario = ${favorito.usuario.id} AND inmueble = ${favorito.inmueble.id};"
        statement.execute(instruccion)
        connection.commit()
        statement.close()
    }

    fun modificarFavorito(favorito: Favorito) {
        eliminarFavorito(favorito)
        storeFavorito(favorito)
    }

    fun actualizarInmueble(inmueble: Inmueble) {
        borrarInmuebleById(inmueble.id)
        when (inmueble) {
            is Habitacion -> crearHabitacion(inmueble)
            is Piso -> crearPiso(inmueble)
            is Local -> crearLocal(inmueble)
            is Garaje -> crearGaraje(inmueble)
        }
    }

}
