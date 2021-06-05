package persistence

import java.sql.Connection

class Tablas(private val connection: Connection) {

    fun crearTablas() {
        crearTablaUsuario()
        crearTablaInmueble()
        crearTablaImagen()
        crearTablaPiso()
        crearTablaLocal()
        crearTablaGaraje()
        crearTablaHabitacion()
        crearTablaBusqueda()
        crearTablaFavorito()
        connection.commit()
    }

    private fun crearTablaUsuario() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists usuario (   \n" +
                "    id serial,\n" +
                "    nombre varchar(100) not null,\n" +
                "    email varchar(100) not null unique,\n" +
                "    contraseña varchar(30) not null, \n" +
                "    token varchar(100),\n" +
                "    imagen varchar(150),\n" +
                "    constraint pk_usuario primary key (id)\n" +
                ");"
        statement.execute(instruccion)
        statement.close()
    }

    private fun crearTablaInmueble() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists inmueble (\n" +
                "    id integer,\n" +
                "    disponible boolean not null,\n" +
                "    tipo varchar(15) not null,\n" +
                "    superficie integer not null,\n" +
                "    precio real not null,\n" +
                "    propietario integer not null,\n" +
                "    descripcion text not null,    \n" +
                "    direccion varchar(200) not null,\n" +
                "    ciudad varchar(70),\n" +
                "    latitud double precision not null,\n" +
                "    longitud double precision not null,\n" +
                "    fecha varchar(50),\n" +
                "    contadorVisitas integer,\n" +
                "    constraint pk_inmueble primary key (id),\n" +
                "    constraint fk_propietario foreign key (propietario) references usuario(id)\n" +
                ");"
        statement.execute(instruccion)
        statement.close()
    }

    private fun crearTablaImagen() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists imagen (\n" +
                "    id serial,\n" +
                "    inmueble integer not null,\n" +
                "    ruta varchar(150) not null unique,\n" +
                "    constraint pk_imagen primary key (id),\n" +
                "    constraint fk_imagen foreign key (inmueble) references inmueble(id) on delete cascade\n" +
                ");"
        statement.execute(instruccion)
        statement.close()
    }

    private fun crearTablaPiso() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists piso (\n" +
                "    id integer,\n" +
                "    habitaciones integer not null,\n" +
                "    baños integer not null,\n" +
                "    garaje boolean not null,\n" +
                "    constraint pk_piso primary key (id),\n" +
                "    constraint fk_piso_herencia foreign key (id) references inmueble(id) on delete cascade\n" +
                ");"
        statement.execute(instruccion)
        statement.close()
    }

    private fun crearTablaLocal() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists local (\n" +
                "    id integer,\n" +
                "    baños integer not null,\n" +
                "    constraint pk_local primary key (id),\n" +
                "    constraint fk_local_herencia foreign key (id) references inmueble(id) on delete cascade\n" +
                ");"
        statement.execute(instruccion)
        statement.close()
    }

    private fun crearTablaGaraje() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists garaje (\n" +
                "    id integer,\n" +
                "    constraint pk_garaje primary key (id),\n" +
                "    constraint fk_garaje_herencia foreign key (id) references inmueble(id) on delete cascade\n" +
                ");"
        statement.execute(instruccion)
        statement.close()
    }

    private fun crearTablaHabitacion() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists habitacion (\n" +
                "    id integer,\n" +
                "    numCompañeros integer,\n" +
                "    constraint pk_habitacion primary key (id),\n" +
                "    constraint fk_habitacion_herencia foreign key (id) references piso(id) on delete cascade\n" +
                ");"
        statement.execute(instruccion)
        statement.close()
    }

    private fun crearTablaBusqueda() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists busqueda (\n" +
                "    id serial,\n" +
                "    usuario integer not null,\n" +
                "    busqueda text not null,\n" +
                "    constraint pk_busqueda primary key (id),\n" +
                "    constraint fk_busqueda_usuario foreign key (usuario) references usuario(id) on delete cascade\n" +
                ");"
        statement.execute(instruccion)
        statement.close()
    }

    private fun crearTablaFavorito() {
        val statement = connection.createStatement()
        val instruccion = "create table if not exists favorito (\n" +
                "    usuario integer,\n" +
                "    inmueble integer,\n" +
                "    notas text,\n" +
                "    orden integer,\n" +
                "    constraint pk_favorito primary key (usuario, inmueble),\n" +
                "    constraint fk_favorito_usuario foreign key (usuario) references usuario(id) on delete cascade,\n" +
                "    constraint fk_favorito_inmueble foreign key (inmueble) references inmueble(id) on delete cascade\n" +
                ")"
        statement.execute(instruccion)
        statement.close()
    }

}