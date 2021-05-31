package persistence

import objects.persistence.Inmueble
import objects.persistence.TipoInmueble
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

    /*@Test
    fun getUsuarioById() {
        var usuario =databaseConnection!!.getUsuarioById(1)
        var resUsuario = Usuario(1,"Antonio Gabinete","antoniogabinete@mail.com","123456789","default_user.png")
        assertEquals(resUsuario.toJson().toString(),usuario.toJson().toString())
    }
     */

    @Test
    fun getNuevoIdDeInmueble() {
        var res=databaseConnection!!.getNuevoIdDeInmueble()
        assertEquals(res,15)
    }

    @Test
    fun emailRepetidoVerdadero() {
        var usuario1 =Usuario(1,"Antonio Gabinete","antoniogabinete@mail.com","123456789","default_user.png")
        var res1 =databaseConnection!!.revisarEmail(usuario1)
        assertEquals(res1,true)
    }
    @Test
    fun emailRepetidoFlaso() {
        var usuario1 =Usuario(1,"Antonio Gabinete","a@mail.com","123456789","default_user.png")
        var res1 =databaseConnection!!.revisarEmail(usuario1)
        assertEquals(res1,false)
    }

    @Test
    fun comprobarUsuario() {
        var usuario = Usuario(1,"Antonio Gabinete","antoniogabinete@mail.com","123456789","default_user.png")
        var resUsuario=databaseConnection!!.comprobarUsuario("antoniogabinete@mail.com","123456789")
        assertEquals(usuario.toJson().toString(),resUsuario?.toJson().toString())
    }

    @Test
    fun getInmuebleById() {
        //var inmueble = Inmueble(1,true,TipoInmueble.Alquiler,90, 600.00,)
        var resInmueble = databaseConnection!!.getInmuebleById(1)
        //assertEquals(inmueble.toJson().toString(),resInmueble.toJson().toString())
    }

    @Test
    fun getFavoritosDeUsuario() {
    }

    @Test
    fun actualizarInmueble() {
    }

}