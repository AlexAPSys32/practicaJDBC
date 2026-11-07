package es.dam.di.infrastructure;

import es.dam.di.model.Usuario;
import es.dam.di.repository.IUsuarioRepositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepositorioJdbc implements IUsuarioRepositorio {

    private static final String SELECT = "SELECT id, nombre, email FROM usuario";

    // ---------- IRepositorio ----------

    @Override
    public long count() {
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM usuario");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar los usuarios", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        comprobarId(id);
        ejecutar("DELETE FROM usuario WHERE id = ?", id);
    }

    @Override
    public void deleteAll() {
        ejecutar("DELETE FROM usuario");
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public Usuario findById(Long id) {
        comprobarId(id);
        List<Usuario> lista = consultar(SELECT + " WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public Iterable<Usuario> findAll() {
        return findAllToList();
    }

    /** Sin id inserta; con id actualiza (buscarlo y volver a guardarlo). */
    @Override
    public <S extends Usuario> S save(S usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getId() == null) {
            usuario.setId(insertar("INSERT INTO usuario (nombre, email) VALUES (?, ?)",
                    usuario.getNombre(), usuario.getEmail()));
        } else {
            int filas = ejecutar("UPDATE usuario SET nombre = ?, email = ? WHERE id = ?",
                    usuario.getNombre(), usuario.getEmail(), usuario.getId());
            if (filas == 0) {
                throw new IllegalArgumentException("No existe el usuario con id " + usuario.getId());
            }
        }
        return usuario;
    }

    // ---------- IRepositorioExtend ----------

    @Override
    public Optional<Usuario> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }

    @Override
    public List<Usuario> findAllToList() {
        return consultar(SELECT + " ORDER BY id");
    }

    // ---------- Métodos propios (IUsuarioRepositorio) ----------

    @Override
    public Optional<Usuario> findByEmail(String email) {
        List<Usuario> lista = consultar(SELECT + " WHERE email = ?", email);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    // ---------- Auxiliares ----------

    private List<Usuario> consultar(String sql, Object... parametros) {
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = BaseDatos.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Usuario(rs.getLong("id"), rs.getString("nombre"), rs.getString("email")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar usuarios", e);
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