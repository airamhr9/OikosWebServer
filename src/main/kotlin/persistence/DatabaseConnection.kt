package persistence
import java.sql.Connection;
import java.sql.DriverManager;

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
}