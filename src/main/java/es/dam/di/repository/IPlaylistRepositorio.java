package es.dam.di.repository;

import es.dam.di.model.Playlist;

import java.util.List;

public interface IPlaylistRepositorio extends IRepositorioExtend<Playlist, Long> {

    /**
     * Devuelve las playlists creadas por un usuario.
     */
    List<Playlist> findByUsuarioId(Long usuarioId);

    /**
     * Devuelve las playlists cuyo nombre contiene el fragmento (sin distinguir mayúsculas).
     */
    List<Playlist> findByNombreContaining(String fragmento);
}