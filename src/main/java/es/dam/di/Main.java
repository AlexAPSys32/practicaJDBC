package es.dam.di;

import es.dam.di.infrastructure.ArtistaRepositorioJdbc;
import es.dam.di.infrastructure.BaseDatos;
import es.dam.di.infrastructure.CancionRepositorioJdbc;
import es.dam.di.infrastructure.PlaylistRepositorioJdbc;
import es.dam.di.infrastructure.UsuarioRepositorioJdbc;
import es.dam.di.model.Artista;
import es.dam.di.model.Cancion;
import es.dam.di.model.Playlist;
import es.dam.di.model.Usuario;
import es.dam.di.repository.IArtistaRepositorio;
import es.dam.di.repository.ICancionRepositorio;
import es.dam.di.repository.IPlaylistRepositorio;
import es.dam.di.repository.IUsuarioRepositorio;

/**
 * Demostración de la práctica JDBC (datos guardados en una base de datos H2).
 * Va probando, apartado por apartado, todo lo que hacen los repositorios.
 * Al empezar y al terminar las tablas quedan vacías, así que se puede ejecutar muchas veces.
 */
public class Main {

    public static void main(String[] args) {

        // Crea las tablas si no existen (lee el fichero schema.sql)
        BaseDatos.inicializar();

        // Los repositorios se declaran con el tipo de la interfaz
        IUsuarioRepositorio usuarios = new UsuarioRepositorioJdbc();
        IArtistaRepositorio artistas = new ArtistaRepositorioJdbc();
        ICancionRepositorio canciones = new CancionRepositorioJdbc();
        IPlaylistRepositorio playlists = new PlaylistRepositorioJdbc();

        // Un id que no existe en ningún repositorio, para probar búsquedas que no encuentran nada
        Long idQueNoExiste = 999999L;

        // ============================================================
        System.out.println("===== 0. EMPEZAR DE CERO =====");
        System.out.println("Borramos todo lo que hubiera de otras ejecuciones.");
        usuarios.deleteAll();   // borra los usuarios y, en cascada, sus playlists
        artistas.deleteAll();   // borra los artistas y, en cascada, sus canciones
        System.out.println("Artistas: " + artistas.count() + ", canciones: " + canciones.count() + ", usuarios: " + usuarios.count() + ", playlists: " + playlists.count());

        // ============================================================
        System.out.println("\n===== 1. GUARDAR DATOS NUEVOS (save) =====");
        System.out.println("Creamos objetos nuevos y los guardamos. Al crearlos no tienen id;");
        System.out.println("la base de datos se lo pone al guardarlos (puede que no empiece en 1, porque el contador no se reinicia al borrar).");

        Artista queen = artistas.save(new Artista("Queen", "Reino Unido"));
        Artista rosalia = artistas.save(new Artista("Rosalía", "España"));
        Artista estopa = artistas.save(new Artista("Estopa", "España"));

        // Cada canción dice de qué artista es con el id del artista
        Cancion bohemian = canciones.save(new Cancion("Bohemian Rhapsody", 354, queen.getId()));
        Cancion dontStop = canciones.save(new Cancion("Don't Stop Me Now", 209, queen.getId()));
        Cancion malamente = canciones.save(new Cancion("Malamente", 150, rosalia.getId()));
        Cancion vinoTinto = canciones.save(new Cancion("Vino tinto", 232, estopa.getId()));

        Usuario ana = usuarios.save(new Usuario("Ana", "ana@correo.com"));
        Usuario luis = usuarios.save(new Usuario("Luis", "luis@correo.com"));

        // Cada playlist dice de qué usuario es, y se le van añadiendo canciones
        Playlist rock = new Playlist("Rock clásico", "Lo mejor del rock", ana.getId());
        rock.agregarCancion(bohemian.getId());
        rock.agregarCancion(dontStop.getId());
        playlists.save(rock);

        Playlist correr = new Playlist("Para correr", null, ana.getId());
        correr.agregarCancion(malamente.getId());
        correr.agregarCancion(bohemian.getId());
        playlists.save(correr);

        Playlist favoritas = new Playlist("Favoritas", "Las de siempre", luis.getId());
        favoritas.agregarCancion(malamente.getId());
        favoritas.agregarCancion(vinoTinto.getId());
        playlists.save(favoritas);

        System.out.println("Ya tienen id:");
        System.out.println("  " + queen);
        System.out.println("  " + bohemian);
        System.out.println("  " + ana);
        System.out.println("  " + rock);

        // ============================================================
        System.out.println("\n===== 2. CONSULTAR DATOS (count, existsById, findById, findAll) =====");
        System.out.println("count = cuántos hay | existsById = si existe ese id (true/false)");
        System.out.println("findById = devuelve el objeto con ese id (null si no existe) | findAll = todos");

        System.out.println("\n--- Artistas ---");
        System.out.println("Cuántos hay: " + artistas.count());
        System.out.println("¿Existe el id " + queen.getId() + "? " + artistas.existsById(queen.getId()));
        System.out.println("¿Existe el id " + idQueNoExiste + "? " + artistas.existsById(idQueNoExiste));
        System.out.println("Buscar el id " + queen.getId() + ": " + artistas.findById(queen.getId()));
        System.out.println("Buscar el id " + idQueNoExiste + ": " + artistas.findById(idQueNoExiste));
        System.out.println("Todos:");
        for (Artista a : artistas.findAll()) {
            System.out.println("  " + a);
        }

        System.out.println("\n--- Canciones ---");
        System.out.println("Cuántas hay: " + canciones.count());
        System.out.println("¿Existe el id " + bohemian.getId() + "? " + canciones.existsById(bohemian.getId()));
        System.out.println("¿Existe el id " + idQueNoExiste + "? " + canciones.existsById(idQueNoExiste));
        System.out.println("Buscar el id " + bohemian.getId() + ": " + canciones.findById(bohemian.getId()));
        System.out.println("Buscar el id " + idQueNoExiste + ": " + canciones.findById(idQueNoExiste));
        System.out.println("Todas:");
        for (Cancion c : canciones.findAll()) {
            System.out.println("  " + c);
        }

        System.out.println("\n--- Usuarios ---");
        System.out.println("Cuántos hay: " + usuarios.count());
        System.out.println("¿Existe el id " + ana.getId() + "? " + usuarios.existsById(ana.getId()));
        System.out.println("¿Existe el id " + idQueNoExiste + "? " + usuarios.existsById(idQueNoExiste));
        System.out.println("Buscar el id " + ana.getId() + ": " + usuarios.findById(ana.getId()));
        System.out.println("Buscar el id " + idQueNoExiste + ": " + usuarios.findById(idQueNoExiste));
        System.out.println("Todos:");
        for (Usuario u : usuarios.findAll()) {
            System.out.println("  " + u);
        }

        System.out.println("\n--- Playlists ---");
        System.out.println("Cuántas hay: " + playlists.count());
        System.out.println("¿Existe el id " + rock.getId() + "? " + playlists.existsById(rock.getId()));
        System.out.println("¿Existe el id " + idQueNoExiste + "? " + playlists.existsById(idQueNoExiste));
        System.out.println("Buscar el id " + rock.getId() + ": " + playlists.findById(rock.getId()));
        System.out.println("Buscar el id " + idQueNoExiste + ": " + playlists.findById(idQueNoExiste));
        System.out.println("Todas:");
        for (Playlist p : playlists.findAll()) {
            System.out.println("  " + p);
        }

        // ============================================================
        System.out.println("\n===== 3. CONSULTAR DE OTRA FORMA (findByIdOptional, findAllToList) =====");
        System.out.println("findByIdOptional hace lo mismo que findById, pero en vez de null devuelve una caja:");
        System.out.println("  Optional[...] = ha encontrado el objeto | Optional.empty = no lo ha encontrado");
        System.out.println("findAllToList devuelve todos los objetos en una lista (se puede saber cuántos son).");

        System.out.println("\n--- Artistas ---");
        System.out.println("Con id " + queen.getId() + ": " + artistas.findByIdOptional(queen.getId()));
        System.out.println("Con id " + idQueNoExiste + ": " + artistas.findByIdOptional(idQueNoExiste));
        System.out.println("Lista: " + artistas.findAllToList().size() + " artistas");

        System.out.println("\n--- Canciones ---");
        System.out.println("Con id " + bohemian.getId() + ": " + canciones.findByIdOptional(bohemian.getId()));
        System.out.println("Con id " + idQueNoExiste + ": " + canciones.findByIdOptional(idQueNoExiste));
        System.out.println("Lista: " + canciones.findAllToList().size() + " canciones");

        System.out.println("\n--- Usuarios ---");
        System.out.println("Con id " + ana.getId() + ": " + usuarios.findByIdOptional(ana.getId()));
        System.out.println("Con id " + idQueNoExiste + ": " + usuarios.findByIdOptional(idQueNoExiste));
        System.out.println("Lista: " + usuarios.findAllToList().size() + " usuarios");

        System.out.println("\n--- Playlists ---");
        System.out.println("Con id " + rock.getId() + ": " + playlists.findByIdOptional(rock.getId()));
        System.out.println("Con id " + idQueNoExiste + ": " + playlists.findByIdOptional(idQueNoExiste));
        System.out.println("Lista: " + playlists.findAllToList().size() + " playlists");

        // ============================================================
        System.out.println("\n===== 4. CAMBIAR DATOS QUE YA EXISTEN =====");
        System.out.println("Para cambiar algo: se busca, se cambia y se vuelve a guardar con save.");

        System.out.println("\n--- Artista ---");
        Artista estopaCambiado = artistas.findById(estopa.getId());
        estopaCambiado.setNombre("Estopa (dúo)");
        System.out.println("Hemos cambiado el nombre pero todavía NO hemos guardado.");
        System.out.println("En la base de datos sigue: " + artistas.findById(estopa.getId()).getNombre());
        artistas.save(estopaCambiado);
        System.out.println("Ahora sí hemos guardado. En la base de datos: " + artistas.findById(estopa.getId()).getNombre());

        System.out.println("\n--- Playlist ---");
        Playlist rockCambiada = playlists.findById(rock.getId());
        rockCambiada.setDescripcion("Rock y algo más");
        rockCambiada.agregarCancion(malamente.getId());
        playlists.save(rockCambiada);
        Playlist rockGuardada = playlists.findById(rock.getId());
        System.out.println("Descripción nueva: " + rockGuardada.getDescripcion());
        System.out.println("Canciones que tiene ahora: " + rockGuardada.getCanciones().size());

        // ============================================================
        System.out.println("\n===== 5. BÚSQUEDAS PROPIAS DE CADA REPOSITORIO =====");
        System.out.println("Cada repositorio tiene búsquedas especiales además de las básicas.");

        System.out.println("\n--- Artistas ---");
        System.out.println("Artistas de España: " + artistas.findByPais("España"));
        System.out.println("Artistas con \"que\" en el nombre: " + artistas.findByNombreContaining("que"));

        System.out.println("\n--- Canciones ---");
        System.out.println("Canciones de Queen: " + canciones.findByArtistaId(queen.getId()));
        System.out.println("Canciones con \"vino\" en el título: " + canciones.findByTituloContaining("vino"));

        System.out.println("\n--- Usuarios ---");
        System.out.println("Usuario con el email ana@correo.com: " + usuarios.findByEmail("ana@correo.com"));
        System.out.println("Usuario con el email no@existe.com: " + usuarios.findByEmail("no@existe.com"));
        System.out.println("¿Hay alguien con ana@correo.com? " + usuarios.existsByEmail("ana@correo.com"));
        System.out.println("¿Hay alguien con no@existe.com? " + usuarios.existsByEmail("no@existe.com"));

        System.out.println("\n--- Playlists ---");
        System.out.println("Playlists de Ana: " + playlists.findByUsuarioId(ana.getId()));
        System.out.println("Playlists con \"fav\" en el nombre: " + playlists.findByNombreContaining("fav"));

        // ============================================================
        System.out.println("\n===== 6. ERRORES QUE EL PROGRAMA AVISA =====");
        System.out.println("En estos casos el repositorio no hace nada y lanza un error con un mensaje.");
        System.out.println("(Se prueban con el repositorio de artistas; los demás funcionan igual.)");

        try {
            artistas.findById(null);
        } catch (IllegalArgumentException e) {
            System.out.println("Buscar con id vacío (null): " + e.getMessage());
        }

        try {
            artistas.existsById(null);
        } catch (IllegalArgumentException e) {
            System.out.println("Preguntar si existe con id vacío (null): " + e.getMessage());
        }

        try {
            artistas.findByIdOptional(null);
        } catch (IllegalArgumentException e) {
            System.out.println("Buscar (Optional) con id vacío (null): " + e.getMessage());
        }

        try {
            artistas.deleteById(null);
        } catch (IllegalArgumentException e) {
            System.out.println("Borrar con id vacío (null): " + e.getMessage());
        }

        try {
            artistas.save(null);
        } catch (IllegalArgumentException e) {
            System.out.println("Guardar un artista vacío (null): " + e.getMessage());
        }

        try {
            artistas.save(new Artista(idQueNoExiste, "Fantasma", "Ninguno"));
        } catch (IllegalArgumentException e) {
            System.out.println("Guardar un artista con un id que no existe: " + e.getMessage());
        }

        // El email es UNIQUE en la base de datos: ella misma rechaza el email repetido
        try {
            usuarios.save(new Usuario("Otra Ana", "ana@correo.com"));
        } catch (IllegalStateException e) {
            System.out.println("Guardar un usuario con un email repetido: la base de datos lo rechaza (" + e.getMessage() + ")");
        }

        // ============================================================
        System.out.println("\n===== 7. BORRAR DATOS (deleteById) =====");
        System.out.println("Algunos borrados arrastran a otros datos (ON DELETE CASCADE): al borrar algo, lo que depende de ello también se borra.");

        System.out.println("\n--- Borrar una playlist ---");
        playlists.deleteById(correr.getId());
        System.out.println("¿Existe todavía \"Para correr\"? " + playlists.existsById(correr.getId()));
        System.out.println("Playlists que quedan: " + playlists.count());

        System.out.println("\n--- Borrar un id que no existe ---");
        playlists.deleteById(idQueNoExiste);
        System.out.println("No pasa nada. Playlists que quedan: " + playlists.count());

        System.out.println("\n--- Borrar una canción ---");
        System.out.println("\"Rock clásico\" tiene " + playlists.findById(rock.getId()).getCanciones().size() + " canciones antes de borrar \"Bohemian Rhapsody\".");
        canciones.deleteById(bohemian.getId());
        System.out.println("Canciones que quedan: " + canciones.count());
        System.out.println("\"Rock clásico\" tiene " + playlists.findById(rock.getId()).getCanciones().size() + " canciones: la canción también se ha quitado de la playlist.");

        System.out.println("\n--- Borrar un artista ---");
        artistas.deleteById(queen.getId());
        System.out.println("Artistas que quedan: " + artistas.count());
        System.out.println("Canciones que quedan: " + canciones.count() + " (se han borrado las de Queen)");

        System.out.println("\n--- Borrar un usuario ---");
        usuarios.deleteById(luis.getId());
        System.out.println("Usuarios que quedan: " + usuarios.count());
        System.out.println("Playlists que quedan: " + playlists.count() + " (se ha borrado la playlist de Luis)");

        // ============================================================
        System.out.println("\n===== 8. BORRAR TODO (deleteAll) =====");
        System.out.println("Dejamos todo vacío otra vez.");
        playlists.deleteAll();
        canciones.deleteAll();
        usuarios.deleteAll();
        artistas.deleteAll();
        System.out.println("Artistas: " + artistas.count() + ", canciones: " + canciones.count() + ", usuarios: " + usuarios.count() + ", playlists: " + playlists.count());

        System.out.println("\nDemostración terminada.");
    }
}