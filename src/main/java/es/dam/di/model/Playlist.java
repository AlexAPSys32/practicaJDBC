package es.dam.di.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Playlist {

    private Long id;
    private String nombre;
    private String descripcion;
    private Long usuarioId;
    private List<PlaylistCancion> canciones = new ArrayList<>();

    public Playlist(String nombre, String descripcion, Long usuarioId) {
        this(null, nombre, descripcion, usuarioId);
    }

    public Playlist(Long id, String nombre, String descripcion, Long usuarioId) {
        this.id = id;
        setNombre(nombre);
        setDescripcion(descripcion);
        setUsuarioId(usuarioId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la playlist no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * La descripción es opcional (puede ser null).
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("La playlist debe pertenecer a un usuario");
        }
        this.usuarioId = usuarioId;
    }

    public List<PlaylistCancion> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<PlaylistCancion> canciones) {
        if (canciones == null) {
            throw new IllegalArgumentException("La lista de canciones no puede ser nula");
        }
        this.canciones = canciones;
    }

    public void agregarCancion(Long cancionId) {
        int siguientePosicion = canciones.size();
        canciones.add(new PlaylistCancion(siguientePosicion, cancionId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;
        Playlist otra = (Playlist) o;
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Playlist{id=" + id + ", nombre='" + nombre + "', descripcion='" + descripcion
                + "', usuarioId=" + usuarioId + ", canciones=" + canciones.size() + "}";
    }
}