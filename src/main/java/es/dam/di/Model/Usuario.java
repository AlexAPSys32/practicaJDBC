package es.dam.di.Model;

import java.util.Objects;

public class Usuario {

    private Long id;
    private String nombre;
    private String email;

    public Usuario(String nombre, String email) {
        this(null, nombre, email);
    }

    public Usuario(Long id, String nombre, String email) {
        this.id = id;
        setNombre(nombre);
        setEmail(email);
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
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email del usuario no puede estar vacío");
        }
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario otro = (Usuario) o;
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', email='" + email + "'}";
    }
}
