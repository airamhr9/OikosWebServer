package persistence
import objects.persistence.Inmueble
import objects.persistence.Usuario
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet




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
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
    //TODO METODOS DE CREACION DE OBJETOS EN CADA TABLA

    fun createExampleTable(){
        val stmt = c.createStatement()
        val sql = "CREATE TABLE COMPANY " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " AGE            INT     NOT NULL, " +
                " ADDRESS        CHAR(50), " +
                " SALARY         REAL)"
        stmt.executeUpdate(sql)
        stmt.close()
    }

    fun listaDeInmuebles(num:Int): List<Inmueble>{
        val stmt = c.createStatement()
        val sql =stmt.executeQuery( "SELECT * FROM inmueble FETCH FIRST " + num.toString() +" ROWS ONLY;")
        val list : MutableList<Inmueble> =  mutableListOf()
        while ( sql.next() ) {
            val sqlUsuario = stmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            val usuario = Usuario(sqlUsuario.getInt("id"), sqlUsuario.getString("nombre"), sqlUsuario.getString("email"))

            val inmueble = Inmueble(sql.getInt("id"), sql.getBoolean("disponible"), sql.getInt("superficie"),
                sql.getDouble("precio"), sql.getInt("habitaciones"), sql.getInt("baños"),
                sql.getBoolean("garaje"), usuario, sql.getString("descripcion"))
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun listaDeInmueblesPorCordenadas(num:Int,x:Double,y:Double): List<Inmueble>{
        val stmt = c.createStatement()
        val sql =stmt.executeQuery( "SELECT * FROM inmueble WHERE (x>="+x+"-10 AND x<="+x+"+10) AND (y>="+y+"-10 AND y<="+y+"+10)" +
                "FETCH FIRST " + num.toString() +" ROWS ONLY;")
        val list : MutableList<Inmueble> =  mutableListOf()
        while ( sql.next() ) {
            val sqlUsuario = stmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")
            val usuario = Usuario(sqlUsuario.getInt("id"), sqlUsuario.getString("nombre"), sqlUsuario.getString("email"))

            val inmueble = Inmueble(sql.getInt("id"), sql.getBoolean("disponible"), sql.getInt("superficie"),
                sql.getDouble("precio"), sql.getInt("habitaciones"), sql.getInt("baños"),
                sql.getBoolean("garaje"), usuario, sql.getString("descripcion"))
            list.add(inmueble)
        }
        sql.close()
        stmt.close()
        return list
    }

    fun inmuebleById(num:Int): Inmueble {

        val stmt = c.createStatement()
        val sql = stmt.executeQuery("SELECT * FROM inmueble WHERE id=" + num.toString() + ";")
        val sqlUsuario = stmt.executeQuery("SELECT * FROM usuario WHERE id=" + sql.getInt("propietario").toString() + ";")

        val usuario = Usuario(sqlUsuario.getInt("id"), sqlUsuario.getString("nombre"), sqlUsuario.getString("email"))
        val inmueble = Inmueble(
            sql.getInt("id"), sql.getBoolean("disponible"), sql.getInt("superficie"),
            sql.getDouble("precio"), sql.getInt("habitaciones"), sql.getInt("baños"),
            sql.getBoolean("garaje"), usuario, sql.getString("descripcion")
        )
        sql.close()
        stmt.close()
        return inmueble
    }
}