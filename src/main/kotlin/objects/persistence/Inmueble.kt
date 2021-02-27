package objects.persistence

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
data class Inmueble(
    @Id @GeneratedValue
    val id: Int? = null,
    val disponible : Boolean,
    val superficie: Int,
    val precio : Double,
    val habitaciones : Int,
    val baños : Int,
    val garaje: Boolean,
    val descripcion: String,
)
