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
import es.dam.di.repository.IRepositorioExtend;
import es.dam.di.repository.IUsuarioRepositorio;

/**
 * Demostración de la práctica JDBC: usa todos los métodos de IRepositorio e
 * IRepositorioExtend en los cuatro repositorios, más sus métodos propios.
 * Se puede ejecutar varias veces: empieza y termina con la base de datos vacía.
 */
public class Main {

    private static final Long ID_INEXISTENTE = 999_999L;

    public static void main(String[] args) {
        BaseDatos.inicializar();

        // Los repositorios se declaran con el tipo de la interfaz
        IUsuarioRepositorio usuarios = new UsuarioRepositorioJdbc();
        IArtistaRepositorio artistas = new ArtistaRepositorioJdbc();
        ICancionRepositorio canciones = new CancionRepositorioJdbc();
        IPlaylistRepositorio playlists = new PlaylistRepositorioJdbc();

        // Limpieza inicial (por si una ejecución anterior se quedó a medias).
        // El ON DELETE CASCADE borra también playlists, canciones y playlist_cancion.
        usuarios.deleteAll();
        artistas.deleteAll();

        // ===================== 1. INSERCIÓN =====================
        titulo("1. INSERCIÓN: save() con id nulo");
        Artista queen = artistas.save(new Artista("Queen", "Reino Unido"));
        Artista rosalia = artistas.save(new Artista("Rosalía", "España"));
        Artista estopa = artistas.save(new Artista("Estopa", "España"));

        Cancion bohemian = canciones.save(new Cancion("Bohemian Rhapsody", 354, queen.getId()));
        Cancion dontStop = canciones.save(new Cancion("Don't Stop Me Now", 209, queen.getId()));
        Cancion malamente = canciones.save(new Cancion("Malamente", 150, rosalia.getId()));
        Cancion vinoTinto = canciones.save(new Cancion("Vino tinto", 232, estopa.getId()));

        Usuario ana = usuarios.save(new Usuario("Ana", "ana@correo.com"));
        Usuario luis = usuarios.save(new Usuario("Luis", "luis@correo.com"));

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

        System.out.println("Los ids los genera la base de datos:");
        System.out.println("  " + queen);
        System.out.println("  " + bohemian);
        System.out.println("  " + ana);
        System.out.println("  " + rock);

        // ===================== 2. CONSULTAS =====================
        titulo("2. CONSULTAS: count, existsById, findById, findAll, findByIdOptional, findAllToList");
        demostrarConsultas("ARTISTA", artistas, queen.getId());
        demostrarConsultas("CANCION", canciones, bohemian.getId());
        demostrarConsultas("USUARIO", usuarios, ana.getId());
        demostrarConsultas("PLAYLIST", playlists, rock.getId());

        // ===================== 3. ACTUALIZACIÓN =====================
        titulo("3. ACTUALIZACIÓN: buscar y volver a guardar");
        Artista artistaEditado = artistas.findById(estopa.getId());
        artistaEditado.setNombre("Estopa (dúo)");
        artistas.save(artistaEditado);
        System.out.println("Artista tras guardar: " + artistas.findById(estopa.getId()));

        Playlist playlistEditada = playlists.findById(rock.getId());
        playlistEditada.setDescripcion("Rock y algo más");
        playlistEditada.agregarCancion(malamente.getId());
        playlists.save(playlistEditada);
        Playlist recargada = playlists.findById(rock.getId());
        System.out.println("Playlist tras guardar: " + recargada);
        System.out.println("  canciones: " + recargada.getCanciones());

        probar("save de un artista con id que no existe",
                () -> artistas.save(new Artista(ID_INEXISTENTE, "Fantasma", "Ninguno")));

        // ===================== 4. MÉTODOS PROPIOS =====================
        titulo("4. MÉTODOS PROPIOS de cada repositorio");
        System.out.println("artistas.findByPais(\"España\")            -> " + artistas.findByPais("España"));
        System.out.println("artistas.findByNombreContaining(\"QUE\")   -> " + artistas.findByNombreContaining("QUE"));
        System.out.println("canciones.findByArtistaId(Queen)         -> " + canciones.findByArtistaId(queen.getId()));
        System.out.println("canciones.findByTituloContaining(\"vino\") -> " + canciones.findByTituloContaining("vino"));
        System.out.println("usuarios.findByEmail(\"ana@correo.com\")   -> " + usuarios.findByEmail("ana@correo.com"));
        System.out.println("usuarios.findByEmail(\"no@existe.com\")    -> " + usuarios.findByEmail("no@existe.com"));
        System.out.println("usuarios.existsByEmail(\"ana@correo.com\") -> " + usuarios.existsByEmail("ana@correo.com"));
        System.out.println("usuarios.existsByEmail(\"no@existe.com\")  -> " + usuarios.existsByEmail("no@existe.com"));
        System.out.println("playlists.findByUsuarioId(Ana)           -> " + playlists.findByUsuarioId(ana.getId()));
        System.out.println("playlists.findByNombreContaining(\"FAV\")  -> " + playlists.findByNombreContaining("FAV"));

        // ===================== 5. ARGUMENTOS NULOS =====================
        titulo("5. ARGUMENTOS NULOS: IllegalArgumentException (según el Javadoc de las interfaces)");
        demostrarNulos("ARTISTA", artistas);
        demostrarNulos("CANCION", canciones);
        demostrarNulos("USUARIO", usuarios);
        demostrarNulos("PLAYLIST", playlists);

        // ===================== 6. BORRADO =====================
        titulo("6. BORRADO: deleteById y deleteAll");
        playlists.deleteById(correr.getId());
        System.out.println("deleteById(playlist 'Para correr') -> existe: "
                + playlists.existsById(correr.getId()) + ", quedan " + playlists.count() + " playlists");

        playlists.deleteById(ID_INEXISTENTE);
        System.out.println("deleteById(id inexistente) -> no hace nada, quedan " + playlists.count() + " playlists");

        canciones.deleteById(vinoTinto.getId());
        System.out.println("deleteById(cancion 'Vino tinto') -> quedan " + canciones.count() + " canciones");

        usuarios.deleteById(luis.getId());
        System.out.println("deleteById(usuario Luis) -> quedan " + usuarios.count() + " usuarios y "
                + playlists.count() + " playlists (su playlist se borró en cascada)");

        artistas.deleteById(estopa.getId());
        System.out.println("deleteById(artista Estopa) -> quedan " + artistas.count() + " artistas");

        playlists.deleteAll();
        canciones.deleteAll();
        usuarios.deleteAll();
        artistas.deleteAll();
        System.out.println("deleteAll() en los cuatro repositorios -> artistas: " + artistas.count()
                + ", canciones: " + canciones.count() + ", usuarios: " + usuarios.count()
                + ", playlists: " + playlists.count());

        System.out.println("\nDemostración terminada.");
    }

    /** Usa los métodos de consulta de IRepositorio e IRepositorioExtend con cualquier repositorio. */
    private static <T> void demostrarConsultas(String nombre, IRepositorioExtend<T, Long> repo, Long idExistente) {
        System.out.println("\n--- " + nombre + " ---");
        System.out.println("count()                         -> " + repo.count());
        System.out.println("existsById(" + idExistente + ")                -> " + repo.existsById(idExistente));
        System.out.println("existsById(inexistente)         -> " + repo.existsById(ID_INEXISTENTE));
        System.out.println("findById(" + idExistente + ")                  -> " + repo.findById(idExistente));
        System.out.println("findById(inexistente)           -> " + repo.findById(ID_INEXISTENTE));
        System.out.println("findAll():");
        for (T entidad : repo.findAll()) {
            System.out.println("    " + entidad);
        }
        System.out.println("findByIdOptional(" + idExistente + ")          -> " + repo.findByIdOptional(idExistente));
        System.out.println("findByIdOptional(inexistente)   -> " + repo.findByIdOptional(ID_INEXISTENTE));
        System.out.println("findAllToList()                 -> " + repo.findAllToList().size() + " elementos");
    }

    /** Comprueba que los métodos lanzan IllegalArgumentException al recibir null. */
    private static <T> void demostrarNulos(String nombre, IRepositorioExtend<T, Long> repo) {
        System.out.println("\n--- " + nombre + " ---");
        probar("existsById(null)", () -> repo.existsById(null));
        probar("findById(null)", () -> repo.findById(null));
        probar("findByIdOptional(null)", () -> repo.findByIdOptional(null));
        probar("deleteById(null)", () -> repo.deleteById(null));
        probar("save(null)", () -> repo.save(null));
    }

    private static void probar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println(descripcion + " -> NO lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println(descripcion + " -> IllegalArgumentException: " + e.getMessage());
        }
    }

    private static void titulo(String texto) {
        System.out.println("\n===== " + texto + " =====");
    }
}