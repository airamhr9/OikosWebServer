package objects.persistence

enum class ModeloInmueble(val value:String) {
    Piso("piso"),
    Local("local"),
    Garjaje("garaje"),
    Habitacion("habitacion");

    companion object {
        fun fromString(value: String) = ModeloInmueble.values().first { it.value == value }
    }
}