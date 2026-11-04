package es.dam.di.repository;

import es.dam.di.model.Artista;

import java.util.List;

public interface IArtistaRepositorio extends IRepositorioExtend<Artista, Long> {

    /**
     * Devuelve los artistas de un país.
     */
    List<Artista> findByPais(String pais);

    /**
     * Devuelve los artistas cuyo nombre contiene el fragmento (sin distinguir mayúsculas).
     */
    List<Artista> findByNombreContaining(String fragmento);
}