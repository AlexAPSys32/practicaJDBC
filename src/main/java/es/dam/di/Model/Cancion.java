package es.dam.di.Model;

import java.util.Objects;

public class Cancion {

    private Long id;
    private String titulo;
    private int duracion; // en segundos
    private Long artistaId;

    public Cancion(String titulo, int duracion, Long artistaId) {
        this(null, titulo, duracion, artistaId);
    }

    public Cancion(Long id, String titulo, int duracion, Long artistaId) {
        this.id = id;
        setTitulo(titulo);
        setDuracion(duracion);
        setArtistaId(artistaId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la canción no puede estar vacío");
        }
        this.titulo = titulo;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor que 0");
        }
        this.duracion = duracion;
    }

    public Long getArtistaId() {
        return artistaId;
    }

    public void setArtistaId(Long artistaId) {
        if (artistaId == null) {
            throw new IllegalArgumentException("La canción debe tener un artista");
        }
        this.artistaId = artistaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cancion)) return false;
        Cancion otra = (Cancion) o;
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cancion{id=" + id + ", titulo='" + titulo + "', duracion=" + duracion
                + ", artistaId=" + artistaId + "}";
    }
}