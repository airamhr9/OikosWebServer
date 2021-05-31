package persistence

import objects.persistence.Inmueble
import objects.persistence.TipoInmueble
import objects.persistence.Usuario
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import objects.JsonExportVisitante

class DatabaseConnectionTest {
    private val databaseConnection = DatabaseConnection.getInstance()

    @Before
    fun setUp() {
        databaseConnection.vaciarTablas()
        val usuario = Usuario(1,"Antonio Gabinete","antoniogabinete@mail.com","123456789","default_user.png")
        databaseConnection.crearUsuario(usuario)
    }

    @Test
    fun getUsuarioById() {
        val result = databaseConnection.getUsuarioById(1)
        assertEquals(usuario.toString(), result.toString())
    }

    @Test
    fun getNuevoIdDeInmueble() {
        var res=databaseConnection!!.getNuevoIdDeInmueble()
        assertEquals(res,15)
    }

    @Test
    fun emailRepetidoVerdadero() {
        var res1 =databaseConnection.revisarEmail(usuario)
        assertEquals(true,res1)
    }
    @Test
    fun emailRepetidoFalso() {
        var usuario1 =Usuario(1,"Antonio Gabinete","a@mail.com","123456789","default_user.png")
        var res1 =databaseConnection.revisarEmail(usuario1)
        assertEquals(false, res1)
    }

    @Test
    fun comprobarUsuarioExiste() {
        var resUsuario=databaseConnection.comprobarUsuario("antoniogabinete@mail.com","123456789")
        assertEquals(usuario.toJson().toString(),resUsuario?.toJson().toString())
    }
    @Test
    fun comprobarUsuarioNoExiste() {
        var usuario:Usuario? = null
        var resUsuario=databaseConnection.comprobarUsuario("antoniogabinete@mail.com","12")
        assertEquals(usuario?.toJson().toString(),resUsuario?.toJson().toString())
    }

    @Test
    fun getInmuebleById() {
        databaseConnection.crearGaraje(garaje)

        val jsonExportEsperado = JsonExportVisitante()
        garaje.accept(jsonExportEsperado)
        val resultadoEsperado = jsonExportEsperado.obtenerResultado().toString()

        var resInmueble = databaseConnection.getInmuebleById(7)
        val jsonExportObtenido = JsonExportVisitante()
        resInmueble.accept(jsonExportObtenido)
        val resultadoObtenido = jsonExportObtenido.obtenerResultado().toString()

        assertEquals(resultadoEsperado, resultadoObtenido)
    }

    @Test
    fun getFavoritosDeUsuario() {
    }

    @Test
    fun actualizarInmueble() {
    }

}