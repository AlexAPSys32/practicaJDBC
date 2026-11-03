package es.dam.di.model;

import java.util.Objects;

public class Artista {

    private Long id;
    private String nombre;
    private String pais;

    public Artista(String nombre, String pais) {
        this(null, nombre, pais);
    }

    public Artista(Long id, String nombre, String pais) {
        this.id = id;
        setNombre(nombre);
        setPais(pais);
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
            throw new IllegalArgumentException("El nombre del artista no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        if (pais == null || pais.isBlank()) {
            throw new IllegalArgumentException("El país del artista no puede estar vacío");
        }
        this.pais = pais;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artista)) return false;
        Artista otro = (Artista) o;
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Artista{id=" + id + ", nombre='" + nombre + "', pais='" + pais + "'}";
    }
}
