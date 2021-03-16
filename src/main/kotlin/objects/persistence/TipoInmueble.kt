package objects.persistence

enum class TipoInmueble(val value:String) {
    Alquiler("Alquiler"),
    Venta("Venta");

    companion object {
        fun fromString(value: String) = TipoInmueble.values().first { it.value == value }
    }
}