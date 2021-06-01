package persistence

import objects.persistence.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import objects.JsonExportVisitante

class DatabaseConnectionTest {
    private val databaseConnection = DatabaseConnection.getInstance()

    private val usuario = Usuario(1,"Antonio Gabinete","antoniogabinete@mail.com","123456789","default_user.png")

    private val garaje = Garaje(7, true, TipoInmueble.Alquiler, 12, 120.0, usuario,
            "descripcion", "direccion", "ciudad", 0.0, 0.0,
            mutableListOf("foto").toTypedArray(), "2021-03-05T11:30:00.380", 0)

    @Before
    fun setUp() {
        databaseConnection.vaciarTablas()
        databaseConnection.crearUsuarioConId(usuario)
    }

    @Test
    fun getUsuarioById() {
        val result = databaseConnection.getUsuarioById(usuario.id)
        assertEquals(usuario.toString(), result.toString())
    }

    @Test
    fun getNuevoIdDeInmuebleSiNoExisten() {
        val result = databaseConnection.getNuevoIdDeInmueble()
        assertEquals(1, result)
    }

    @Test
    fun getNuevoIdDeInmuebleSiExisten() {
        databaseConnection.crearGaraje(garaje)
        val result = databaseConnection.getNuevoIdDeInmueble()
        assertEquals(garaje.id + 1, result)
    }

    @Test
    fun emailRepetidoVerdadero() {
        val result =databaseConnection.revisarEmail(usuario)
        assertEquals(true, result)
    }
    @Test
    fun emailRepetidoFalso() {
        val usuario1 = Usuario(2,"Antonio Gabinete","a@mail.com","123456789","default_user.png")
        val result = databaseConnection.revisarEmail(usuario1)
        assertEquals(false, result)
    }

    @Test
    fun comprobarUsuarioExiste() {
        val resUsuario = databaseConnection.comprobarUsuario("antoniogabinete@mail.com","123456789")
        assertEquals(usuario.toString(), resUsuario?.toString())
    }
    @Test
    fun comprobarUsuarioNoExiste() {
        val user: Usuario? = null
        val resUsuario = databaseConnection.comprobarUsuario("antoniogabinete@mail.com","12")
        assertEquals(user?.toString(), resUsuario?.toString())
    }

    @Test
    fun getInmuebleById() {
        databaseConnection.crearGaraje(garaje)

        val jsonExportEsperado = JsonExportVisitante()
        garaje.accept(jsonExportEsperado)
        val resultadoEsperado = jsonExportEsperado.obtenerResultado().toString()

        val resInmueble = databaseConnection.getInmuebleById(garaje.id)
        val jsonExportObtenido = JsonExportVisitante()
        resInmueble.accept(jsonExportObtenido)
        val resultadoObtenido = jsonExportObtenido.obtenerResultado().toString()

        assertEquals(resultadoEsperado, resultadoObtenido)
    }

    @Test
    fun getModeloInmuebleById() {
    }

    @Test
    fun getFavoritosDeUsuario() {
    }

    @Test
    fun actualizarInmueble() {
    }

}