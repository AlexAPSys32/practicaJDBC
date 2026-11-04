package es.dam.di.repository;

import es.dam.di.model.Cancion;

import java.util.List;

public interface ICancionRepositorio extends IRepositorioExtend<Cancion, Long> {

    /**
     * Devuelve las canciones de un artista.
     */
    List<Cancion> findByArtistaId(Long artistaId);

    /**
     * Devuelve las canciones cuyo título contiene el fragmento (sin distinguir mayúsculas).
     */
    List<Cancion> findByTituloContaining(String fragmento);
}