package persistence
import objects.persistence.Inmueble
import objects.persistence.Preferencia
import objects.persistence.TipoInmueble
import objects.persistence.Usuario
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
                //.getConnection("jdbc:postgresql://172.17.0.2:5432/Oikos", // Airam
                .getConnection("jdbc:postgresql://localhost:5432/oikos", // Hector
                    "postgres", "mysecretpassword");
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
    fun sqlUser(sqlUsuario: ResultSet):Usuario{
        return Usuario(sqlUsuario.getInt("id"), sqlUsuario.getString("nombre"), sqlUsuario.getString("email"))
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
                                    habitaciones: Int, baños: Int, garaje: Boolean?, ciudad: String?, tipo: String?): List<Inmueble>{
        val stmt = c.createStatement()
        val list : MutableList<Inmueble> =  mutableListOf()
        var query = "SELECT * FROM inmueble WHERE "
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
            val inmueble = sqlInmueble(sql,usuario, imagenes)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun listaDeInmueblesPorCordenadas(num:Int,x:Double,y:Double): List<Inmueble>{
        val stmt = c.createStatement()
        val list : MutableList<Inmueble> =  mutableListOf()

        val sql =stmt.executeQuery( "SELECT * FROM inmueble WHERE " +
                "( latitud >= "+x+" - 0.2 AND latitud <= "+x+" + 0.2 ) AND ( longitud >= "+y+" - 0.2 AND longitud <= "+y+" + 0.2 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
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
        val sql = "INSERT INTO preferencia (id, superficie_min, superficie_max, precio_min, precio_max, habitaciones, baños, garaje, ciudad, tipo)" +
                "VALUES (${p.id}, ${p.superficie_min}, ${p.superficie_max}, ${p.precio_min},${p.superficie_max},${p.habitaciones}," +
                " ${p.baños},${p.garaje},'${p.ciudad}', '${p.tipo}');"
        stmt.executeUpdate(sql);

        c.commit();
        stmt.close()
        return p
    }

    fun actualizarPreferencias(p:Preferencia): Preferencia {
        var stmt = c.createStatement()
        val sql = "DELETE from preferencia where id = ${p.id};"
        stmt.executeUpdate(sql)

        val pre  = crearPreferencias(p)
        c.commit();
        stmt.close()
        return pre
    }
    fun borrarIn(id:Int){
        var stmt = c.createStatement()
        val sql = "DELETE from inmueble where id = ${id};"
        stmt.executeUpdate(sql)

        c.commit();
        stmt.close()
    }
}
