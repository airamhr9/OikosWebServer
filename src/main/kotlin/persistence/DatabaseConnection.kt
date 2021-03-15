package persistence
import objects.persistence.Inmueble
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
                //.getConnection("jdbc:postgresql://localhost:5432/testdb",
                .getConnection("jdbc:postgresql://172.17.0.2:5432/Oikos",
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

    fun sqlInmueble(sql:ResultSet,usuario:Usuario): Inmueble{
        return Inmueble(sql.getInt("id"), sql.getBoolean("disponible"),
            TipoInmueble.fromString(sql.getString("tipo")), sql.getInt("superficie"),
            sql.getDouble("precio"), sql.getInt("habitaciones"), sql.getInt("baños"),
            sql.getBoolean("garaje"), usuario, sql.getString("descripcion"),
            sql.getString("direccion"), sql.getString("ciudad"), sql.getDouble("latitud"),
            sql.getDouble("longitud"), /* Imagenes */ )
    }
    fun sqlUser(sqlUsuario: ResultSet):Usuario{
        return Usuario(sqlUsuario.getInt("id"), sqlUsuario.getString("nombre"), sqlUsuario.getString("email"))
    }

    fun listaDeInmueblesPorFiltrado(num:Int,precio: Double,habitaciones: Int,baños: Int,garaje: Boolean,direccion: String): List<Inmueble>{
        val stmt = c.createStatement()
        val list : MutableList<Inmueble> =  mutableListOf()
        val sql =stmt.executeQuery("SELECT * FROM inmueble " +
                "WHERE baños = "+ baños +" AND garaje = "+ garaje +" AND habitaciones = "+habitaciones+" AND disponible = true AND " +
                "( precio <= "+precio+" + 100 AND precio >= "+precio+" - 100) AND direccion = "+direccion
                + " FETCH FIRST $num ROWS ONLY;")

        while ( sql.next() ) {
            val sqlUsuario = stmt.executeQuery("SELECT * FROM usuario WHERE id = " + sql.getInt("propietario").toString() + ";")
            val usuario = sqlUser(sqlUsuario)
            val inmueble = sqlInmueble(sql,usuario)
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
                "( x >= "+x+" - 10 AND x <= "+x+" + 10 ) AND ( y >= "+y+" - 10 AND y <= "+y+" + 10 ) AND disponible = true " +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        while ( sql.next() ) {
            val sqlUsuario = stmt.executeQuery("SELECT * FROM usuario WHERE id = " + sql.getInt("propietario").toString() + ";")
            val usuario = sqlUser(sqlUsuario)
            val inmueble = sqlInmueble(sql,usuario)
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun inmuebleById(num:Int): Inmueble {
        val stmt = c.createStatement()
        val sql = stmt.executeQuery("SELECT * FROM inmueble WHERE id=$num;")
        val sqlUsuario = stmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
        val usuario = sqlUser(sqlUsuario)
        val inmueble = sqlInmueble(sql,usuario)

        sql.close()
        stmt.close()
        return inmueble
    }
}