package persistence

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
        mutableListOf("garaje.png").toTypedArray(), "2021-03-05T11:30:00.380", 0)

    private val local = Local(9, true, TipoInmueble.Alquiler, 40, 700.0, usuario,
        "descripcion", "direccion", "ciudad", 0.0, 0.0,
        mutableListOf("local.png").toTypedArray(), "2021-03-05T11:30:00.380", 0, 1)

    private val piso = Piso(9, true, TipoInmueble.Venta, 90, 120000.0, usuario,
        "descripcion", "direccion", "ciudad", 0.0, 0.0,
        mutableListOf("piso.png").toTypedArray(), "2021-03-05T11:30:00.380", 0, 3, 2, true)

    private val habitacion = Habitacion(9, true, TipoInmueble.Alquiler, 90, 250.0, usuario,
        "descripcion", "direccion", "ciudad", 0.0, 0.0,
        mutableListOf("habitacion.png").toTypedArray(), "2021-03-05T11:30:00.380", 0, 3, 2,
        false, 2)

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
        databaseConnection.crearLocal(local)
        val result = databaseConnection.getNuevoIdDeInmueble()
        assertEquals(local.id + 1, result)
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
        val result = databaseConnection.comprobarUsuario("antoniogabinete@mail.com","123456789")
        assertEquals(usuario.toString(), result?.toString())
    }

    @Test
    fun comprobarUsuarioNoExiste() {
        val result = databaseConnection.comprobarUsuario("antoniogabinete@mail.com","12")
        assertEquals(null, result?.toString())
    }

    @Test
    fun getGarajeById() {
        databaseConnection.crearGaraje(garaje)
        val result = databaseConnection.getInmuebleById(garaje.id)
        assertEquals(garaje.toString(), result.toString())
    }

    @Test
    fun getLocalById() {
        databaseConnection.crearLocal(local)
        val result = databaseConnection.getInmuebleById(local.id)
        assertEquals(local.toString(), result.toString())
    }

    @Test
    fun getPisoById() {
        databaseConnection.crearPiso(piso)
        val result = databaseConnection.getInmuebleById(piso.id)
        assertEquals(piso.toString(), result.toString())
    }

    @Test
    fun getHabitacionById() {
        databaseConnection.crearHabitacion(habitacion)
        val result = databaseConnection.getInmuebleById(habitacion.id)
        assertEquals(habitacion.toString(), result.toString())
    }

    @Test
    fun getModeloInmuebleByIdGaraje() {
        databaseConnection.crearGaraje(garaje)
        val result = databaseConnection.getModeloInmuebleById(garaje.id)
        assertEquals(ModeloInmueble.Garaje, result)
    }

    @Test
    fun getModeloInmuebleByIdLocal() {
        databaseConnection.crearLocal(local)
        val result = databaseConnection.getModeloInmuebleById(local.id)
        assertEquals(ModeloInmueble.Local, result)
    }

    @Test
    fun getModeloInmuebleByIdPiso() {
        databaseConnection.crearPiso(piso)
        val result = databaseConnection.getModeloInmuebleById(piso.id)
        assertEquals(ModeloInmueble.Piso, result)
    }

    @Test
    fun getModeloInmuebleByIdHabitacion() {
        databaseConnection.crearHabitacion(habitacion)
        val result = databaseConnection.getModeloInmuebleById(habitacion.id)
        assertEquals(ModeloInmueble.Habitacion, result)
    }
}