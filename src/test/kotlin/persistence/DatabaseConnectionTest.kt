package persistence

import objects.JsonExportVisitante
import objects.persistence.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DatabaseConnectionTest {
    private val databaseConnection = DatabaseConnection.getInstance()

    private val usuario = Usuario(1,"Antonio Gabinete","antoniogabinete@mail.com","123456789",
        "default_user.png")

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
        val result = databaseConnection.revisarEmail(usuario)
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
        val resUsuario = databaseConnection.comprobarUsuario("antoniogabinete@mail.com","12")
        assertEquals(null, resUsuario?.toString())
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
    fun getModeloInmuebleByIdGaraje() {
        databaseConnection.crearGaraje(garaje)
        val result = databaseConnection.getModeloInmuebleById(garaje.id)
        assertEquals(ModeloInmueble.Garaje, result)
    }

    @Test
    fun getModeloInmuebleByIdLocal() {
        val local = Local(7, true, TipoInmueble.Alquiler, 12, 120.0, usuario,
            "descripcion", "direccion", "ciudad", 0.0, 0.0,
            mutableListOf("foto").toTypedArray(), "2021-03-05T11:30:00.380", 0, 1)
        databaseConnection.crearLocal(local)
        val result = databaseConnection.getModeloInmuebleById(local.id)
        assertEquals(ModeloInmueble.Local, result)
    }

    @Test
    fun getModeloInmuebleByIdPiso() {
        val piso = Piso(7, true, TipoInmueble.Alquiler, 12, 120.0, usuario,
            "descripcion", "direccion", "ciudad", 0.0, 0.0,
            mutableListOf("foto").toTypedArray(), "2021-03-05T11:30:00.380", 0, 2, 1, true)
        databaseConnection.crearPiso(piso)
        val result = databaseConnection.getModeloInmuebleById(piso.id)
        assertEquals(ModeloInmueble.Piso, result)
    }

    @Test
    fun getModeloInmuebleByIdHabitacion() {
        val habitacion = Habitacion(7, true, TipoInmueble.Alquiler, 12, 120.0, usuario,
            "descripcion", "direccion", "ciudad", 0.0, 0.0,
            mutableListOf("foto").toTypedArray(), "2021-03-05T11:30:00.380", 0, 3,
            1, true, 2)
        databaseConnection.crearHabitacion(habitacion)
        val result = databaseConnection.getModeloInmuebleById(habitacion.id)
        assertEquals(ModeloInmueble.Habitacion, result)
    }

    @Test
    fun getFavoritosDeUsuario() {
    }

    @Test
    fun actualizarInmueble() {
    }

}