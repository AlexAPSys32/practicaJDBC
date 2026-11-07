package es.dam.di.infrastructure;

import es.dam.di.model.Playlist;
import es.dam.di.model.PlaylistCancion;
import es.dam.di.repository.IPlaylistRepositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de la raíz Playlist. Gestiona también la tabla playlist_cancion,
 * porque PlaylistCancion forma parte del agregado y no tiene repositorio propio.
 */
public class PlaylistRepositorioJdbc implements IPlaylistRepositorio {

    private static final String SELECT = "SELECT id, nombre, descripcion, usuario_id FROM playlist";

    // ---------- IRepositorio ----------

    @Override
    public long count() {
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM playlist");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar las playlists", e);
        }
    }

    /** Las filas de playlist_cancion se borran solas por el ON DELETE CASCADE. */
    @Override
    public void deleteById(Long id) {
        comprobarId(id);
        ejecutar("DELETE FROM playlist WHERE id = ?", id);
    }

    @Override
    public void deleteAll() {
        ejecutar("DELETE FROM playlist");
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public Playlist findById(Long id) {
        comprobarId(id);
        List<Playlist> lista = consultar(SELECT + " WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public Iterable<Playlist> findAll() {
        return findAllToList();
    }

    /**
     * Sin id inserta; con id actualiza (buscarla y volver a guardarla).
     * Después guarda las canciones de la playlist: borra las que había e inserta las actuales.
     */
    @Override
    public <S extends Playlist> S save(S playlist) {
        if (playlist == null) {
            throw new IllegalArgumentException("La playlist no puede ser nula");
        }
        if (playlist.getId() == null) {
            playlist.setId(insertar(
                    "INSERT INTO playlist (nombre, descripcion, usuario_id) VALUES (?, ?, ?)",
                    playlist.getNombre(), playlist.getDescripcion(), playlist.getUsuarioId()));
        } else {
            int filas = ejecutar(
                    "UPDATE playlist SET nombre = ?, descripcion = ?, usuario_id = ? WHERE id = ?",
                    playlist.getNombre(), playlist.getDescripcion(), playlist.getUsuarioId(), playlist.getId());
            if (filas == 0) {
                throw new IllegalArgumentException("No existe la playlist con id " + playlist.getId());
            }
            ejecutar("DELETE FROM playlist_cancion WHERE playlist_id = ?", playlist.getId());
        }
        for (PlaylistCancion pc : playlist.getCanciones()) {
            pc.setId(insertar(
                    "INSERT INTO playlist_cancion (posicion, playlist_id, cancion_id) VALUES (?, ?, ?)",
                    pc.getPosicion(), playlist.getId(), pc.getCancionId()));
        }
        return playlist;
    }

    // ---------- IRepositorioExtend ----------

    @Override
    public Optional<Playlist> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }

    @Override
    public List<Playlist> findAllToList() {
        return consultar(SELECT + " ORDER BY id");
    }

    // ---------- Métodos propios (IPlaylistRepositorio) ----------

    @Override
    public List<Playlist> findByUsuarioId(Long usuarioId) {
        return consultar(SELECT + " WHERE usuario_id = ? ORDER BY id", usuarioId);
    }

    @Override
    public List<Playlist> findByNombreContaining(String fragmento) {
        return consultar(SELECT + " WHERE LOWER(nombre) LIKE ? ORDER BY id",
                "%" + fragmento.toLowerCase() + "%");
    }

    // ---------- Auxiliares ----------

    /** Lee las playlists del SELECT y rellena las canciones de cada una. */
    private List<Playlist> consultar(String sql, Object... parametros) {
        List<Playlist> lista = new ArrayList<>();
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Playlist(rs.getLong("id"), rs.getString("nombre"),
                            rs.getString("descripcion"), rs.getLong("usuario_id")));
                }
            }
            for (Playlist playlist : lista) {
                playlist.setCanciones(cargarCanciones(con, playlist.getId()));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar playlists", e);
        }
        return lista;
    }

    private List<PlaylistCancion> cargarCanciones(Connection con, Long playlistId) throws SQLException {
        String sql = "SELECT id, posicion, cancion_id FROM playlist_cancion "
                + "WHERE playlist_id = ? ORDER BY posicion";
        List<PlaylistCancion> canciones = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, playlistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    canciones.add(new PlaylistCancion(rs.getLong("id"),
                            rs.getInt("posicion"), rs.getLong("cancion_id")));
                }
            }
        }
        return canciones;
    }

    /** INSERT/UPDATE/DELETE: devuelve las filas afectadas. */
    private int ejecutar(String sql, Object... parametros) {
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al ejecutar: " + sql, e);
        }
    }

    /** INSERT: devuelve el id generado por la base de datos. */
    private long insertar(String sql, Object... parametros) {
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return claves.getLong(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar: " + sql, e);
        }
    }

    private void comprobarId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
    }
}