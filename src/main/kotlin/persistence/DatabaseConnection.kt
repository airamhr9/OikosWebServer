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
    //private val databaseURL = "jdbc:postgresql://172.17.0.2:5432/Oikos" // Airam

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
                                    modelo:ModeloInmueble,numComp:Int?, idUsuario: Int): List<InmuebleSprint2>{
        val stmt = connection.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        var query = if (modelo == ModeloInmueble.Habitacion) "SELECT * FROM inmueble NATURAL JOIN piso NATURAL JOIN habitacion WHERE "
                    else "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE "
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
        val sql = stmt.executeQuery(query)

        val inmueblesFavoritosDeUsuario = getFavoritosDeUsuario(idUsuario).map {it.inmueble}
        while ( sql.next() ) {
            val inmueble = FabricaInmueble.crearInmueble(sql, modelo)
            if (inmueblesFavoritosDeUsuario.contains(inmueble)) {
                inmueble.esFavorito = true
            }
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun listaDeInmueblesPorCordenadas(num:Int, x:Double, y:Double): List<InmuebleSprint2> {
        val result = listaDePisosPorCordenadas(num, x, y)
        result.addAll(listaDeLocalesPorCordenadas(num, x, y))
        result.addAll(listaDeGarajesPorCordenadas(num, x, y))
        result.addAll(listaDeHabitacionesPorCordenadas(num, x, y))
        return result
    }

    private fun listaDePisosPorCordenadas(num:Int, x:Double, y:Double): MutableList<InmuebleSprint2> {
        val stmt = connection.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        val modelo = ModeloInmueble.Piso
        val sql =stmt.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val inmueble = FabricaInmueble.crearInmueble(sql, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    private fun listaDeLocalesPorCordenadas(num:Int, x:Double, y:Double): MutableList<InmuebleSprint2> {
        val stmt = connection.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        val modelo = ModeloInmueble.Local
        val sql =stmt.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val inmueble = FabricaInmueble.crearInmueble(sql, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    private fun listaDeGarajesPorCordenadas(num:Int, x:Double, y:Double): MutableList<InmuebleSprint2> {
        val stmt = connection.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        val modelo = ModeloInmueble.Garaje
        val sql =stmt.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val inmueble = FabricaInmueble.crearInmueble(sql, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    private fun listaDeHabitacionesPorCordenadas(num:Int, x:Double, y:Double): MutableList<InmuebleSprint2> {
        val stmt = connection.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        val modelo = ModeloInmueble.Habitacion
        val sql =stmt.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${ModeloInmueble.Piso.value} "
                + "NATURAL JOIN ${modelo.value} WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val inmueble = FabricaInmueble.crearInmueble(sql, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun borrarInmuebleById(id: Int) {
        val stmt = connection.createStatement()
        val sql = "DELETE from inmueble where id = $id;"
        stmt.executeUpdate(sql)
        connection.commit()
        stmt.close()
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

    private fun insertarImagen(inmueble:InmuebleSprint2) {
        val stmt = connection.createStatement()
        for (imagen in inmueble.imagenes) {
            println(imagen)
            val sql = "INSERT INTO imagen (inmueble, ruta) " +
                    "VALUES (${inmueble.id}, '${imagen}');"
            stmt.executeUpdate(sql)
        }
        connection.commit()
        stmt.close()
    }

    fun crearLocal(l:Local){
        val stmt = connection.createStatement()
        if (l.id==-1){
            l.id = getNuevoIdDeInmueble()
        }
        val sql = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad," +
                "latitud, longitud, propietario, fecha, contadorVisitas) " +
                "VALUES (${l.id}, ${l.disponible}, '${l.tipo}', ${l.superficie},${l.precio},'${l.descripcion}'," +
                " '${l.direccion}','${l.ciudad}',${l.latitud}, ${l.longitud}, ${l.propietario.id}, '${l.fecha}', ${l.contadorVisitas});"
        val sql1="INSERT INTO local (id, baños) VALUES (${l.id}, ${l.baños});"
        stmt.executeUpdate(sql)
        stmt.executeUpdate(sql1)
        connection.commit()
        insertarImagen(l)
        stmt.close()
    }

    fun crearPiso(p:Piso){
        val stmt = connection.createStatement()
        if (p.id==-1) {
            p.id = getNuevoIdDeInmueble()
        }
        val sql = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad," +
                "latitud, longitud, propietario, fecha, contadorVisitas) " +
                "VALUES (${p.id}, ${p.disponible}, '${p.tipo}', ${p.superficie},${p.precio},'${p.descripcion}'," +
                " '${p.direccion}','${p.ciudad}',${p.latitud}, ${p.longitud}, ${p.propietario.id}, '${p.fecha}', ${p.contadorVisitas});"
        val sql1="INSERT INTO piso (id, habitaciones, baños, garaje) VALUES (${p.id}, ${p.habitaciones}, ${p.baños}, ${p.garaje});"
        println(sql)
        stmt.executeUpdate(sql)
        stmt.executeUpdate(sql1)
        connection.commit()
        insertarImagen(p)
        stmt.close()
    }

    fun crearHabitacion(h:Habitacion){
        val stmt = connection.createStatement()
        if (h.id==-1) {
            h.id = getNuevoIdDeInmueble()
        }
        val sql = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad," +
                "latitud, longitud, propietario, fecha, contadorVisitas) " +
                "VALUES (${h.id}, ${h.disponible}, '${h.tipo}', ${h.superficie},${h.precio},'${h.descripcion}'," +
                " '${h.direccion}','${h.ciudad}',${h.latitud}, ${h.longitud}, ${h.propietario.id}, '${h.fecha}', ${h.contadorVisitas});"
        val sql1="INSERT INTO piso (id, habitaciones, baños, garaje) VALUES (${h.id}, ${h.habitaciones}, ${h.baños}, ${h.garaje});"
        val sql2="INSERT INTO habitacion (id, numCompañeros) VALUES (${h.id}, ${h.numCompañeros});"
        stmt.executeUpdate(sql)
        stmt.executeUpdate(sql1)
        stmt.executeUpdate(sql2)
        connection.commit()
        insertarImagen(h)
        stmt.close()
    }

    fun crearGaraje(g:Garaje){
        val stmt = connection.createStatement()
         if (g.id==-1) {
            g.id = getNuevoIdDeInmueble()
        }
        val sql = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad," +
                "latitud, longitud, propietario, fecha, contadorVisitas) " +
                "VALUES (${g.id}, ${g.disponible}, '${g.tipo}', ${g.superficie},${g.precio},'${g.descripcion}'," +
                " '${g.direccion}','${g.ciudad}',${g.latitud}, ${g.longitud}, ${g.propietario.id}, '${g.fecha}', ${g.contadorVisitas});"
        val sql1="INSERT INTO garaje (id) VALUES (${g.id});"
        stmt.executeUpdate(sql)
        stmt.executeUpdate(sql1)
        connection.commit()
        insertarImagen(g)
        stmt.close()
    }

    fun actualizarLocal(l:Local){
        borrarInmuebleById(l.id)
        crearLocal(l)
    }

    fun actualizarHabitacion(h:Habitacion){
        borrarInmuebleById(h.id)
        crearHabitacion(h)
    }

    fun actualizarGaraje(g:Garaje){
        borrarInmuebleById(g.id)
        crearGaraje(g)
    }

    fun actualizarPiso(p:Piso){
        borrarInmuebleById(p.id)
        crearPiso(p)
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

    fun crearUsuario(u:Usuario){
        val stmt = connection.createStatement()
        val sql = "INSERT INTO usuario (nombre, email, contraseña, imagen) " +
                "VALUES ('${u.nombre}', '${u.mail}','${u.contraseña}','${u.imagen}');"
        stmt.executeUpdate(sql)
        connection.commit()
        stmt.close()
    }

    fun revisarEmail(usuario: Usuario): Boolean{
        var result = false
        val statement = connection.createStatement()
        val sql = statement.executeQuery("SELECT email FROM usuario WHERE email = '${usuario.mail}';")
        sql.next()
        try {
            val email = sql.getString("email")
            result = (usuario.mail == email)
        } catch (e: Exception) {
        }
        sql.close()
        statement.close()
        return result
    }

    fun comprobarUsuario(email: String, contraseña: String): Usuario? {
        var user: Usuario?
        val statement = connection.createStatement()
        val sql = statement.executeQuery("SELECT * FROM usuario WHERE email = '$email' AND contraseña = '$contraseña';")
        sql.next()
        try {
            user = getUsuarioFromResultSet(sql)
        } catch (e: Exception) {
            user = null
        }
        sql.close()
        statement.close()
        return user
    }

    fun getUsuarioById(id: Int): Usuario {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM usuario WHERE id=$id;")
        resultSet.next()
        val nombre = resultSet.getString("nombre")
        val email = resultSet.getString("email")
        val contraseña = resultSet.getString("contraseña")
        val imagen = resultSet.getString("imagen")
        val usuario = Usuario(id, nombre, email, contraseña, imagen)
        resultSet.close()
        statement.close()
        return usuario
    }

    fun getInmueblesDeUsuario(idUsuario: Int): List<InmuebleSprint2> {
        val inmuebles = getPisosDeUsuario(idUsuario)
        inmuebles.addAll(getLocalesDeUsuario(idUsuario))
        inmuebles.addAll(getGarajesDeUsuario(idUsuario))
        inmuebles.addAll(getHabitacionesDeUsuario(idUsuario))
        // Para evitar que las habitaciones se repitan (habitacion hereda de piso)
        val map = mutableMapOf<Int, InmuebleSprint2>()
        inmuebles.forEach { map[it.id] = it }
        return map.values.toList()
    }

    private fun getPisosDeUsuario(idUsuario: Int): MutableList<InmuebleSprint2> {
        val statement = connection.createStatement()
        val result = mutableListOf<InmuebleSprint2>()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN piso "
                + "WHERE propietario = $idUsuario")
        while (resultSet.next()) {
            result.add(FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Piso))
        }
        resultSet.close()
        statement.close()
        return result
    }

    private fun getLocalesDeUsuario(idUsuario: Int): MutableList<InmuebleSprint2> {
        val statement = connection.createStatement()
        val result = mutableListOf<InmuebleSprint2>()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN local "
                + "WHERE propietario = $idUsuario")
        while (resultSet.next()) {
            result.add(FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Local))
        }
        resultSet.close()
        statement.close()
        return result
    }

    private fun getGarajesDeUsuario(idUsuario: Int): MutableList<InmuebleSprint2> {
        val statement = connection.createStatement()
        val result = mutableListOf<InmuebleSprint2>()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN garaje "
                + "WHERE propietario = $idUsuario")
        while (resultSet.next()) {
            result.add(FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Garaje))
        }
        resultSet.close()
        statement.close()
        return result
    }

    private fun getHabitacionesDeUsuario(idUsuario: Int): MutableList<InmuebleSprint2> {
        val statement = connection.createStatement()
        val result = mutableListOf<InmuebleSprint2>()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN piso NATURAL JOIN habitacion "
                + "WHERE propietario = $idUsuario")
        while (resultSet.next()) {
            result.add(FabricaInmueble.crearInmueble(resultSet, ModeloInmueble.Habitacion))
        }
        resultSet.close()
        statement.close()
        return result
    }

    fun guardadoById(num:Int): Busqueda {
        val stmt = connection.createStatement()
        val sql = stmt.executeQuery("SELECT * FROM busqueda WHERE id=$num;")
        sql.next()
        val userStmt = connection.createStatement()
        val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("id").toString() + ";")
        sqlUsuario.next()
        val usuario = getUsuarioFromResultSet(sqlUsuario)
        val guardado = Busqueda(sql.getInt("id"), sql.getInt("superficie_min"),sql.getInt("superficie_max"),
            sql.getDouble("precio_min"),sql.getDouble("precio_max"), sql.getInt("habitaciones"),
            sql.getInt("baños"), sql.getBoolean("garaje"),sql.getInt("numCompañeros"),
            sql.getString("ciudad"),usuario, sql.getString("tipo"),sql.getString("modelo"))

        sql.close()
        stmt.close()
        return guardado
    }

    fun crearGuardado(busqueda:String,usuario:Int){
        val stmt = connection.createStatement()
        val sql = "INSERT INTO busqueda (usuario, busqueda) " +
                "VALUES (${usuario}, '${busqueda}');"
        stmt.executeUpdate(sql)
        connection.commit()
        stmt.close()
    }

    /*fun actualizarGuardado(b:Busqueda){
        var stmt = c.createStatement()
        val sql = "DELETE from busqueda where id = ${b.id};"
        stmt.executeUpdate(sql)
    }*/

    fun listaBusqueda(usuario:Int): JsonArray{
        val stmt = connection.createStatement()
        val list = JsonArray()
        val sql =stmt.executeQuery("SELECT * FROM busqueda WHERE usuario = $usuario ;")
        while ( sql.next() ) {
            val string = JsonParser.parseString(sql.getString("busqueda"))
            list.add(string)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun storeFavorito(favorito: Favorito) {
        val statement = connection.createStatement()
        val orden =
            if(favorito.orden==-1) getOrdenFavorito(favorito.usuario.id)
            else favorito.orden
        val instruccion = "INSERT INTO favorito (usuario, inmueble, notas, orden)" +
                "VALUES (${favorito.usuario.id}, ${favorito.inmueble.id}, '${favorito.notas}', ${orden});"
        statement.execute(instruccion)
        connection.commit()
        statement.close()
    }

    private fun getModeloInmuebleById(id: Int): ModeloInmueble {
        val statement = connection.createStatement()
        var result: ModeloInmueble
        try {
            val resultSet = statement.executeQuery("SELECT * FROM habitacion WHERE id = $id;")
            resultSet.next()
            resultSet.getInt("id")
            resultSet.close()
            result = ModeloInmueble.Habitacion
        } catch (e: Exception) {
            try {
                val resultSet = statement.executeQuery("SELECT * FROM piso WHERE id = $id;")
                resultSet.next()
                resultSet.getInt("id")
                resultSet.close()
                result = ModeloInmueble.Piso
            } catch (e: Exception) {
                try {
                    val resultSet = statement.executeQuery("SELECT * FROM local WHERE id = $id;")
                    resultSet.next()
                    resultSet.getInt("id")
                    resultSet.close()
                    result = ModeloInmueble.Local
                } catch (e: Exception) {
                    result = ModeloInmueble.Garaje
                }
            }
        }
        statement.close()
        return result
    }

    fun getInmuebleById(id: Int): InmuebleSprint2 {
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

    private fun getOrdenFavorito(usuario:Int): Int {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT max(orden) FROM favorito WHERE usuario = ${usuario};")
        resultSet.next()
        var result = resultSet.getInt(1)
        result++
        resultSet.close()
        statement.close()
        return result
    }

    fun actualizarInmueble(inmueble: InmuebleSprint2) {
        if (inmueble is Habitacion) {
            actualizarHabitacion(inmueble)
        } else if (inmueble is Piso) {
            actualizarPiso(inmueble)
        } else if (inmueble is Local) {
            actualizarLocal(inmueble)
        } else {
            actualizarGaraje(inmueble as Garaje)
        }
    }

}
