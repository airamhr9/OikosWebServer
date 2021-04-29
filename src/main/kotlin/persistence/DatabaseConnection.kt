package persistence
import objects.persistence.*
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet
import kotlin.system.exitProcess


class DatabaseConnection {
    private lateinit var c : Connection
    init {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                //.getConnection("jdbc:postgresql://localhost:5432/testdb",  // ¿Jaime?
                .getConnection("jdbc:postgresql://172.17.0.2:5432/Oikos", // Airam
                //.getConnection("jdbc:postgresql://localhost:5432/oikos", // Hector
                //.getConnection("jdbc:postgresql://localhost:5432/postgres", // Hector Pruebas
                    "postgres", "mysecretpassword");

            c.autoCommit = false;
        } catch (e : Exception) {
            e.printStackTrace();
            System.err.println(e.javaClass.name+": "+e.message);
            exitProcess(0);
        }
        println("Opened database successfully");
    }
    //TODO METODOS DE CREACION DE OBJETOS EN CADA TABLA

    //companion object{}

    fun sqlInmueble(sql:ResultSet,usuario:Usuario, imagenes: List<String>): Inmueble{
        return Inmueble(sql.getInt("id"), sql.getBoolean("disponible"),
            TipoInmueble.fromString(sql.getString("tipo")), sql.getInt("superficie"),
            sql.getDouble("precio"), sql.getInt("habitaciones"), sql.getInt("baños"),
            sql.getBoolean("garaje"), usuario, sql.getString("descripcion"),
            sql.getString("direccion"), sql.getString("ciudad"), sql.getDouble("latitud"),
            sql.getDouble("longitud"), imagenes.toTypedArray())
    }

    fun sqlInmuebleSprint2(sql:ResultSet,usuario:Usuario, imagenes: List<String>, modelo: ModeloInmueble): InmuebleSprint2{
        return when (modelo) {
            ModeloInmueble.Piso -> sqlPiso(sql, usuario, imagenes)
            ModeloInmueble.Local -> sqlLocal(sql, usuario, imagenes)
            ModeloInmueble.Garjaje -> sqlGaraje(sql, usuario, imagenes)
            ModeloInmueble.Habitacion -> sqlHabitacion(sql, usuario, imagenes)
        }
    }

    fun sqlPiso(sql:ResultSet,usuario:Usuario, imagenes: List<String>): Piso {
        return Piso(sql.getInt("id"), sql.getBoolean("disponible"),
            TipoInmueble.fromString(sql.getString("tipo")), sql.getInt("superficie"),
            sql.getDouble("precio"), usuario, sql.getString("descripcion"),
            sql.getString("direccion"), sql.getString("ciudad"), sql.getDouble("latitud"),
            sql.getDouble("longitud"), imagenes.toTypedArray(), sql.getInt("habitaciones"),
            sql.getInt("baños"), sql.getBoolean("garaje"))
    }

    fun sqlLocal(sql:ResultSet,usuario:Usuario, imagenes: List<String>): Local {
        return Local(sql.getInt("id"), sql.getBoolean("disponible"),
            TipoInmueble.fromString(sql.getString("tipo")), sql.getInt("superficie"),
            sql.getDouble("precio"), usuario, sql.getString("descripcion"),
            sql.getString("direccion"), sql.getString("ciudad"), sql.getDouble("latitud"),
            sql.getDouble("longitud"), imagenes.toTypedArray(), sql.getInt("baños"))
    }

    fun sqlGaraje(sql:ResultSet,usuario:Usuario, imagenes: List<String>): Garaje {
        return Garaje(sql.getInt("id"), sql.getBoolean("disponible"),
            TipoInmueble.fromString(sql.getString("tipo")), sql.getInt("superficie"),
            sql.getDouble("precio"), usuario, sql.getString("descripcion"),
            sql.getString("direccion"), sql.getString("ciudad"), sql.getDouble("latitud"),
            sql.getDouble("longitud"), imagenes.toTypedArray())
    }

    fun sqlHabitacion(sql:ResultSet,usuario:Usuario, imagenes: List<String>): Habitacion {
        return Habitacion(sql.getInt("id"), sql.getBoolean("disponible"),
            TipoInmueble.fromString(sql.getString("tipo")), sql.getInt("superficie"),
            sql.getDouble("precio"), usuario, sql.getString("descripcion"),
            sql.getString("direccion"), sql.getString("ciudad"), sql.getDouble("latitud"),
            sql.getDouble("longitud"), imagenes.toTypedArray(), sql.getInt("habitaciones"),
            sql.getInt("baños"), sql.getBoolean("garaje"), sql.getInt("numCompañeros"))
    }

    fun sqlUser(sqlUsuario: ResultSet):Usuario{
        return Usuario(sqlUsuario.getInt("id"), sqlUsuario.getString("nombre"), sqlUsuario.getString("email")
            ,sqlUsuario.getString("contraseña"),sqlUsuario.getString("imagen"))
    }

    fun sqlImagenes(idInmueble: Int): List<String> {
        val stmt2 = c.createStatement()
        val sql2 = stmt2.executeQuery("SELECT * FROM imagen WHERE inmueble = $idInmueble")
        val nombresImagenes = mutableListOf<String>()
        while ( sql2.next() ) {
            nombresImagenes.add(sql2.getString("ruta"))
        }
        return nombresImagenes;
    }

    fun listaDeInmueblesPorFiltrado(num:Int, precioMin: Double, precioMax: Double?, supMin: Int, supMax: Int?,
                                    habitaciones: Int, baños: Int, garaje: Boolean?, ciudad: String?, tipo: String?,
                                    modelo:ModeloInmueble,numComp:Int?): List<InmuebleSprint2>{
        val stmt = c.createStatement()
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

        /*val sql =stmt.executeQuery("SELECT * FROM inmueble " +
                "WHERE baños = "+ baños +" AND garaje = "+ garaje +" AND habitaciones = "+habitaciones+" AND disponible = true AND " +
                "( precio >= "+precioMin +" AND precio <= "+precioMax +") AND " +
                "( superficie >= "+supMin +" AND superficie <= "+supMax +") AND tipo = "+tipo+ " AND direccion = "+ciudad
                + " FETCH FIRST $num ROWS ONLY;")
         */

        while ( sql.next() ) {
            val userStmt = c.createStatement()
            val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            sqlUsuario.next()
            val usuario = sqlUser(sqlUsuario)
            val imagenes = sqlImagenes(sql.getInt("id"))
            val inmueble = sqlInmuebleSprint2(sql,usuario, imagenes, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    /*fun listaDeInmueblesPorCordenadas(num:Int, x:Double, y:Double): List<InmuebleSprint2> {
        val stmt = c.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()

        val sql =stmt.executeQuery( "SELECT * FROM inmueble WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val userStmt = c.createStatement()
            val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            sqlUsuario.next()
            val usuario = sqlUser(sqlUsuario)
            val imagenes = sqlImagenes(sql.getInt("id"))
            val inmueble = sqlInmuebleSprint2(sql, usuario, imagenes, )
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }*/

    fun listaDeInmueblesPorCordenadas(num:Int, x:Double, y:Double): List<InmuebleSprint2> {
        val result = listaDePisosPorCordenadas(num, x, y)
        result.addAll(listaDeLocalesPorCordenadas(num, x, y))
        result.addAll(listaDeGarajesPorCordenadas(num, x, y))
        result.addAll(listaDeHabitacionesPorCordenadas(num, x, y))
        return result
    }

    private fun listaDePisosPorCordenadas(num:Int, x:Double, y:Double): MutableList<InmuebleSprint2> {
        val stmt = c.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        val modelo = ModeloInmueble.Piso
        val sql =stmt.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val userStmt = c.createStatement()
            val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            sqlUsuario.next()
            val usuario = sqlUser(sqlUsuario)
            val imagenes = sqlImagenes(sql.getInt("id"))
            val inmueble = sqlInmuebleSprint2(sql, usuario, imagenes, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    private fun listaDeLocalesPorCordenadas(num:Int, x:Double, y:Double): MutableList<InmuebleSprint2> {
        val stmt = c.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        val modelo = ModeloInmueble.Local
        val sql =stmt.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val userStmt = c.createStatement()
            val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            sqlUsuario.next()
            val usuario = sqlUser(sqlUsuario)
            val imagenes = sqlImagenes(sql.getInt("id"))
            val inmueble = sqlInmuebleSprint2(sql, usuario, imagenes, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    private fun listaDeGarajesPorCordenadas(num:Int, x:Double, y:Double): MutableList<InmuebleSprint2> {
        val stmt = c.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        val modelo = ModeloInmueble.Garjaje
        val sql =stmt.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${modelo.value} WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val userStmt = c.createStatement()
            val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            sqlUsuario.next()
            val usuario = sqlUser(sqlUsuario)
            val imagenes = sqlImagenes(sql.getInt("id"))
            val inmueble = sqlInmuebleSprint2(sql, usuario, imagenes, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    private fun listaDeHabitacionesPorCordenadas(num:Int, x:Double, y:Double): MutableList<InmuebleSprint2> {
        val stmt = c.createStatement()
        val list : MutableList<InmuebleSprint2> =  mutableListOf()
        val modelo = ModeloInmueble.Habitacion
        val sql =stmt.executeQuery( "SELECT * FROM inmueble NATURAL JOIN ${ModeloInmueble.Piso.value} "
                + "NATURAL JOIN ${modelo.value} WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val userStmt = c.createStatement()
            val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            sqlUsuario.next()
            val usuario = sqlUser(sqlUsuario)
            val imagenes = sqlImagenes(sql.getInt("id"))
            val inmueble = sqlInmuebleSprint2(sql, usuario, imagenes, modelo)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun listaDeInmueblesPorDefecto(num:Int): List<Inmueble>{
        val stmt = c.createStatement()
        val list : MutableList<Inmueble> =  mutableListOf()

        val sql =stmt.executeQuery("SELECT * FROM inmueble FETCH FIRST $num ROWS ONLY;")
        while ( sql.next() ) {
            val userStmt = c.createStatement()
            val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            sqlUsuario.next()
            val usuario = sqlUser(sqlUsuario)
            val imagenes = sqlImagenes(sql.getInt("id"))
            val inmueble = sqlInmueble(sql,usuario, imagenes)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun inmuebleById(num:Int): Inmueble {
        val stmt = c.createStatement()
        val sql = stmt.executeQuery("SELECT * FROM inmueble WHERE id=$num;")
        sql.next()
        val userStmt = c.createStatement()
        val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
        sqlUsuario.next()
        val usuario = sqlUser(sqlUsuario)
        val imagenes = sqlImagenes(sql.getInt("id"))
        val inmueble = sqlInmueble(sql,usuario, imagenes)

        sql.close()
        stmt.close()
        return inmueble
    }

    fun preferenciasById(num:Int): Preferencia {
        val stmt = c.createStatement()
        val sql = stmt.executeQuery("SELECT * FROM preferencia WHERE id=$num;")
        sql.next()
        val userStmt = c.createStatement()
        val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("id").toString() + ";")
        sqlUsuario.next()
        val usuario = sqlUser(sqlUsuario)
        val preferencia = Preferencia(sql.getInt("id"), sql.getInt("superficie_min"),sql.getInt("superficie_max"),
            sql.getDouble("precio_min"),sql.getDouble("precio_max"), sql.getInt("habitaciones"),
            sql.getInt("baños"), sql.getBoolean("garaje"),sql.getString("ciudad"),usuario, sql.getString("tipo"))

        sql.close()
        stmt.close()
        return preferencia
    }

    fun crearPreferencias(p:Preferencia): Preferencia {
        val stmt = c.createStatement()
        val sql = "INSERT INTO preferencia (id, superficie_min, superficie_max, precio_min, precio_max, habitaciones, baños, garaje, ciudad, tipo) " +
                "VALUES (${p.id}, ${p.superficie_min}, ${p.superficie_max}, ${p.precio_min},${p.superficie_max},${p.habitaciones}," +
                " ${p.baños},${p.garaje},'${p.ciudad}', '${p.tipo}');"
        stmt.executeUpdate(sql);

        c.commit();
        stmt.close()
        return p
    }

    fun actualizarPreferencias(p: Preferencia): Preferencia {
        var stmt = c.createStatement()
        val sql = "DELETE from preferencia where id = ${p.id};"
        stmt.executeUpdate(sql)

        return crearPreferencias(p)
    }
    fun borrarIn(id:Int){
        var stmt = c.createStatement()
        val sql = "DELETE from inmueble where id = ${id};"
        stmt.executeUpdate(sql)

        c.commit();
        stmt.close()
    }

    fun getPisoById(id: Int): Piso {
        val statement = c.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN piso WHERE id=$id;")
        resultSet.next()
        val piso = getInmuebleFromResultSet(resultSet, ModeloInmueble.Piso) as Piso
        resultSet.close()
        statement.close()
        return piso
    }

    fun getLocalById(id: Int): Local {
        val statement = c.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN local WHERE id=$id;")
        resultSet.next()
        val local = getInmuebleFromResultSet(resultSet, ModeloInmueble.Local) as Local
        resultSet.close()
        statement.close()
        return local
    }

    fun getGarajeById(id: Int): Garaje {
        val statement = c.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN garaje WHERE id=$id;")
        resultSet.next()
        val garaje = getInmuebleFromResultSet(resultSet, ModeloInmueble.Garjaje) as Garaje
        resultSet.close()
        statement.close()
        return garaje
    }

    fun getHabitacionById(id: Int): Habitacion {
        val statement = c.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM inmueble NATURAL JOIN habitacion WHERE id=$id;")
        resultSet.next()
        val habitacion = getInmuebleFromResultSet(resultSet, ModeloInmueble.Habitacion) as Habitacion
        resultSet.close()
        statement.close()
        return habitacion
    }

    private fun getInmuebleFromResultSet(resultSet: ResultSet, modelo: ModeloInmueble): InmuebleSprint2 {
        val id = resultSet.getInt("id");
        val disponible = resultSet.getBoolean("disponible")
        val tipo = TipoInmueble.fromString(resultSet.getString("tipo"))
        val superficie = resultSet.getInt("superficie")
        val precio = resultSet.getDouble("precio")

        // REFACTORIZAR
        // val propietario = getUsuarioById(resultSet.getInt("propietario"))
        val userStmt = c.createStatement()
        val sqlUsuario = userStmt.executeQuery("SELECT * FROM usuario WHERE id="
                + resultSet.getInt("propietario").toString() + ";")
        sqlUsuario.next()
        val propietario = sqlUser(sqlUsuario)

        val descripcion = resultSet.getString("descripcion")
        val direccion = resultSet.getString("direccion")
        val ciudad = resultSet.getString("ciudad")
        val latitud = resultSet.getDouble("latitud")
        val longitud = resultSet.getDouble("longitud")
        val imagenes = sqlImagenes(resultSet.getInt("id"))

        when (modelo) {
            ModeloInmueble.Piso -> {
                val habitaciones = resultSet.getInt("habitaciones")
                val baños = resultSet.getInt("baños")
                val garaje = resultSet.getBoolean("garaje")
                return Piso(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                    ciudad, latitud, longitud, imagenes.toTypedArray(), habitaciones, baños, garaje)
            }
            ModeloInmueble.Local -> {
                val baños = resultSet.getInt("baños")
                return Local(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                    ciudad, latitud, longitud, imagenes.toTypedArray(), baños)
            }
            ModeloInmueble.Garjaje -> {
                return Garaje(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                    ciudad, latitud, longitud, imagenes.toTypedArray())
            }
            ModeloInmueble.Habitacion -> {
                val habitaciones = resultSet.getInt("habitaciones")
                val baños = resultSet.getInt("baños")
                val garaje = resultSet.getBoolean("garaje")
                val numCompañeros = resultSet.getInt("numCompañeros")
                return Habitacion(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                    ciudad, latitud, longitud, imagenes.toTypedArray(), habitaciones, baños, garaje, numCompañeros)
            }
        }
    }

    fun insertarImagen(inmueble:InmuebleSprint2) {
        val stmt = c.createStatement()
        for (imagen in inmueble.imagenes) {
            println(imagen)
            val sql = "INSERT INTO imagen (inmueble, ruta) " +
                    "VALUES (${inmueble.id}, '${imagen}');"
            stmt.executeUpdate(sql)
        }
        c.commit()
        stmt.close()
    }

    fun crearLocal(l:Local){
        val stmt = c.createStatement()
         if (l.id==-1){
            l.id = nuevoId()
        }
        val sql = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad, latitud, longitud, propietario) " +
                "VALUES (${l.id}, ${l.disponible}, '${l.tipo}', ${l.superficie},${l.precio},'${l.descripcion}'," +
                " '${l.direccion}','${l.ciudad}',${l.latitud}, ${l.longitud}, ${l.propietario.id});"
        val sql1="INSERT INTO local (id, baños) VALUES (${l.id}, ${l.baños});"
        stmt.executeUpdate(sql);
        stmt.executeUpdate(sql1);
        c.commit();
        insertarImagen(l)
        stmt.close()
    }
    fun crearPiso(p:Piso){
        val stmt = c.createStatement()
         if (p.id==-1) {
             p.id = nuevoId()
         }
        val sql = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad, latitud, longitud, propietario) " +
                "VALUES (${p.id}, ${p.disponible}, '${p.tipo}', ${p.superficie},${p.precio},'${p.descripcion}'," +
                " '${p.direccion}','${p.ciudad}',${p.latitud}, ${p.longitud}, ${p.propietario.id});"
        val sql1="INSERT INTO piso (id, habitaciones, baños, garaje) VALUES (${p.id}, ${p.habitaciones}, ${p.baños}, ${p.garaje});"
        println(sql)
        stmt.executeUpdate(sql);
        stmt.executeUpdate(sql1);
        c.commit();
        insertarImagen(p)
        stmt.close()
    }
    fun crearHabitacion(h:Habitacion){
        val stmt = c.createStatement()
         if (h.id==-1) {
            h.id = nuevoId()
         }

        val sql = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad, latitud, longitud, propietario) " +
                "VALUES (${h.id}, ${h.disponible}, '${h.tipo}', ${h.superficie},${h.precio},'${h.descripcion}'," +
                " '${h.direccion}','${h.ciudad}',${h.latitud}, ${h.longitud}, ${h.propietario.id});"
        val sql1="INSERT INTO piso (id, habitaciones, baños, garaje) VALUES (${h.id}, ${h.habitaciones}, ${h.baños}, ${h.garaje});"
        val sql2="INSERT INTO habitacion (id, numCompañeros) VALUES (${h.id}, ${h.numCompañeros});"
        stmt.executeUpdate(sql);
        stmt.executeUpdate(sql1);
        stmt.executeUpdate(sql2);
        c.commit();
        insertarImagen(h)
        stmt.close()
    }
    fun crearGaraje(g:Garaje){
        val stmt = c.createStatement()
         if (g.id==-1) {
            g.id = nuevoId()
        }
        val sql = "INSERT INTO inmueble (id, disponible, tipo, superficie, precio, descripcion, direccion, ciudad, latitud, longitud, propietario) " +
                "VALUES (${g.id}, ${g.disponible}, '${g.tipo}', ${g.superficie},${g.precio},'${g.descripcion}'," +
                " '${g.direccion}','${g.ciudad}',${g.latitud}, ${g.longitud}, ${g.propietario.id});"
        val sql1="INSERT INTO garaje (id, baños) VALUES (${g.id});"
        stmt.executeUpdate(sql);
        stmt.executeUpdate(sql1);
        c.commit();
        insertarImagen(g)
        stmt.close()
    }

    fun actualizarLocal(l:Local){
        borrarIn(l.id)
        crearLocal(l)
    }
    fun actualizarHabitacion(h:Habitacion){
        borrarIn(h.id)
        crearHabitacion(h)
    }
    fun actualizarGaraje(g:Garaje){
        borrarIn(g.id)
        crearGaraje(g)
    }
    fun actualizarPiso(p:Piso){
        borrarIn(p.id)
        crearPiso(p)
    }

    fun nuevoId():Int {
        val statement = c.createStatement()
        val sql = statement.executeQuery("SELECT max(id) FROM inmueble;")
        sql.next()
        var res = sql.getInt(1)
        sql.close()
        statement.close()
        res++
        return  res
    }

    fun crearUsuario(u:Usuario){
        val stmt = c.createStatement()
        val sql = "INSERT INTO usuario (id, nombre, email, contraseña, imagen) " +
                "VALUES (${u.id}, ${u.nombre}, ${u.mail},${u.contraseña},${u.imagen});"
        stmt.executeUpdate(sql);
        c.commit();
        stmt.close()
    }
    fun revisarEmail(u:Usuario): Boolean{
        var b=false;
        val statement = c.createStatement()
        val sql = statement.executeQuery("SELECT email FROM usuario WHERE email = ${u.mail};")
        sql.next()
        var res = sql.getString(1)
        b = (u.mail==res)
        sql.close()
        statement.close()

        return b
    }
    fun comprobarUsuario(nombre:String,contraseña:String):Boolean{
        var b=false;
        val statement = c.createStatement()
        val sql = statement.executeQuery("SELECT nombre FROM usuario WHERE nombre = ${nombre} AND contraseña = ${contraseña};")
        sql.next()
        var res = sql.getString(1)
        b = (res==nombre)
        sql.close()
        statement.close()

        return b
    }

    fun getUsuarioById(id: Int): Usuario {
        val statement = c.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM usuario WHERE id=$id;")
        resultSet.next()
        val id = resultSet.getInt("id")
        val nombre = resultSet.getString("nombre")
        val email = resultSet.getString("email")
        val contraseña = resultSet.getString("contraseña")
        val imagen = resultSet.getString("imagen")
        val usuario = Usuario(id, nombre, email, contraseña, imagen)
        resultSet.close()
        statement.close()
        return usuario
    }

}
