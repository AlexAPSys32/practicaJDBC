package es.dam.di.Model;

import java.util.Objects;

public class PlaylistCancion {

    private Long id;
    private int posicion;
    private Long cancionId;

    public PlaylistCancion(int posicion, Long cancionId) {
        this(null, posicion, cancionId);
    }

    public PlaylistCancion(Long id, int posicion, Long cancionId) {
        this.id = id;
        setPosicion(posicion);
        setCancionId(cancionId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        if (posicion <= 0) {
            throw new IllegalArgumentException("La posición debe ser mayor que 0");
        }
        this.posicion = posicion;
    }

    public Long getCancionId() {
        return cancionId;
    }

    public void setCancionId(Long cancionId) {
        if (cancionId == null) {
            throw new IllegalArgumentException("La canción no puede ser nula");
        }
        this.cancionId = cancionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaylistCancion)) return false;
        PlaylistCancion otra = (PlaylistCancion) o;
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PlaylistCancion{id=" + id + ", posicion=" + posicion
                + ", cancionId=" + cancionId + "}";
    }
}