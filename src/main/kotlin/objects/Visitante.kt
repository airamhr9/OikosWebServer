package objects

import objects.persistence.*

interface Visitante {
    fun visitHabitacion(hab : Habitacion)
    fun visitPiso(piso : Piso)
    fun visitLocal(local : Local)
    fun visitGaraje(garaje : Garaje)
    fun visitFavorito(favorito: Favorito)
}