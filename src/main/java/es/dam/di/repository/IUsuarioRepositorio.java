package es.dam.di.repository;

import es.dam.di.model.Usuario;

import java.util.Optional;

public interface IUsuarioRepositorio extends IRepositorioExtend<Usuario, Long> {

    /**
     * Devuelve el usuario con ese email, o Optional.empty() si no existe.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Indica si ya hay un usuario registrado con ese email.
     */
    boolean existsByEmail(String email);
}