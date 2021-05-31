package persistence

import objects.persistence.Usuario
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class DatabaseConnectionTest {
    private var databaseConnection: DatabaseConnection? = null

    @Before
    fun setUp(){
        databaseConnection = DatabaseConnection.getInstance()
    }

    @Test
    fun getUsuarioById() {
        var usuario =databaseConnection!!.getUsuarioById(1)
        var resUsuario = Usuario(1,"Antonio Gabinete","antoniogabinete@mail.com","123456789","default_user.png")
        assertEquals(resUsuario.toJson().toString(),usuario.toJson().toString())
    }

    @Test
    fun getNuevoIdDeInmueble() {
    }

    @Test
    fun revisarEmail() {
        var usuario1 =Usuario(1,"Antonio Gabinete","antoniogabinete@mail.com","123456789","default_user.png")
        var res1 =databaseConnection!!.revisarEmail(usuario1)
        var usuario2 =Usuario(1,"Antonio Gabinete","antoniogabinete@hotmail.com","123456789","default_user.png")
        var res2 =databaseConnection!!.revisarEmail(usuario2)
        assertEquals(res1,true)
        assertEquals(res2,false)
    }

    @Test
    fun comprobarUsuario() {
    }

    @Test
    fun getInmuebleById() {
    }

    @Test
    fun getFavoritosDeUsuario() {
    }

    @Test
    fun actualizarInmueble() {
    }

}