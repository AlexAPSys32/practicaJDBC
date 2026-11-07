package es.dam.di.infrastructure;

import es.dam.di.model.Artista;
import es.dam.di.repository.IArtistaRepositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArtistaRepositorioJdbc implements IArtistaRepositorio {

    private static final String SELECT = "SELECT id, nombre, pais FROM artista";

    // ---------- IRepositorio ----------

    @Override
    public long count() {
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM artista");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar los artistas", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        comprobarId(id);
        ejecutar("DELETE FROM artista WHERE id = ?", id);
    }

    @Override
    public void deleteAll() {
        ejecutar("DELETE FROM artista");
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public Artista findById(Long id) {
        comprobarId(id);
        List<Artista> lista = consultar(SELECT + " WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public Iterable<Artista> findAll() {
        return findAllToList();
    }

    /** Sin id inserta; con id actualiza (buscarlo y volver a guardarlo). */
    @Override
    public <S extends Artista> S save(S artista) {
        if (artista == null) {
            throw new IllegalArgumentException("El artista no puede ser nulo");
        }
        if (artista.getId() == null) {
            artista.setId(insertar("INSERT INTO artista (nombre, pais) VALUES (?, ?)",
                    artista.getNombre(), artista.getPais()));
        } else {
            int filas = ejecutar("UPDATE artista SET nombre = ?, pais = ? WHERE id = ?",
                    artista.getNombre(), artista.getPais(), artista.getId());
            if (filas == 0) {
                throw new IllegalArgumentException("No existe el artista con id " + artista.getId());
            }
        }
        return artista;
    }

    // ---------- IRepositorioExtend ----------

    @Override
    public Optional<Artista> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }

    @Override
    public List<Artista> findAllToList() {
        return consultar(SELECT + " ORDER BY id");
    }

    // ---------- Métodos propios (IArtistaRepositorio) ----------

    @Override
    public List<Artista> findByPais(String pais) {
        return consultar(SELECT + " WHERE pais = ? ORDER BY id", pais);
    }

    @Override
    public List<Artista> findByNombreContaining(String fragmento) {
        return consultar(SELECT + " WHERE LOWER(nombre) LIKE ? ORDER BY id",
                "%" + fragmento.toLowerCase() + "%");
    }

    // ---------- Auxiliares ----------

    private List<Artista> consultar(String sql, Object... parametros) {
        List<Artista> lista = new ArrayList<>();
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Artista(rs.getLong("id"), rs.getString("nombre"), rs.getString("pais")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar artistas", e);
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