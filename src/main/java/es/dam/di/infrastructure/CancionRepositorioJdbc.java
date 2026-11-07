package es.dam.di.infrastructure;

import es.dam.di.model.Cancion;
import es.dam.di.repository.ICancionRepositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CancionRepositorioJdbc implements ICancionRepositorio {

    private static final String SELECT = "SELECT id, titulo, duracion, artista_id FROM cancion";

    // ---------- IRepositorio ----------

    @Override
    public long count() {
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM cancion");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar las canciones", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        comprobarId(id);
        ejecutar("DELETE FROM cancion WHERE id = ?", id);
    }

    @Override
    public void deleteAll() {
        ejecutar("DELETE FROM cancion");
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public Cancion findById(Long id) {
        comprobarId(id);
        List<Cancion> lista = consultar(SELECT + " WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public Iterable<Cancion> findAll() {
        return findAllToList();
    }

    /** Sin id inserta; con id actualiza (buscarla y volver a guardarla). */
    @Override
    public <S extends Cancion> S save(S cancion) {
        if (cancion == null) {
            throw new IllegalArgumentException("La canción no puede ser nula");
        }
        if (cancion.getId() == null) {
            cancion.setId(insertar("INSERT INTO cancion (titulo, duracion, artista_id) VALUES (?, ?, ?)",
                    cancion.getTitulo(), cancion.getDuracion(), cancion.getArtistaId()));
        } else {
            int filas = ejecutar("UPDATE cancion SET titulo = ?, duracion = ?, artista_id = ? WHERE id = ?",
                    cancion.getTitulo(), cancion.getDuracion(), cancion.getArtistaId(), cancion.getId());
            if (filas == 0) {
                throw new IllegalArgumentException("No existe la canción con id " + cancion.getId());
            }
        }
        return cancion;
    }

    // ---------- IRepositorioExtend ----------

    @Override
    public Optional<Cancion> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }

    @Override
    public List<Cancion> findAllToList() {
        return consultar(SELECT + " ORDER BY id");
    }

    // ---------- Métodos propios (ICancionRepositorio) ----------

    @Override
    public List<Cancion> findByArtistaId(Long artistaId) {
        return consultar(SELECT + " WHERE artista_id = ? ORDER BY id", artistaId);
    }

    @Override
    public List<Cancion> findByTituloContaining(String fragmento) {
        return consultar(SELECT + " WHERE LOWER(titulo) LIKE ? ORDER BY id",
                "%" + fragmento.toLowerCase() + "%");
    }

    // ---------- Auxiliares ----------

    private List<Cancion> consultar(String sql, Object... parametros) {
        List<Cancion> lista = new ArrayList<>();
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cancion(rs.getLong("id"), rs.getString("titulo"),
                            rs.getInt("duracion"), rs.getLong("artista_id")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar canciones", e);
        }
        return lista;
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
