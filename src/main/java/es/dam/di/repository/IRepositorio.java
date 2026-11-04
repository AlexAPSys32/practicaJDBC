package es.dam.di.repository;

public interface IRepositorio<T, ID> {

    /**
     * Devuelve el número de entidades.
     *
     * @return número de entidades
     */
    long count();

    /**
     * Borra la entidad con identificador id.
     * Si no se encuentra no realiza ninguna acción
     *
     * @param id identificador de la entidad. No debe ser nulo
     * @throws IllegalArgumentException En caso de ser id nulo
     */
    void deleteById(ID id);

    /**
     * Borra todas las entidades del repositorio.
     */
    void deleteAll();

    /**
     * Devuelve true si existe la entidad con identificador id.
     *
     * @param id Identificador de la entidad
     * @return true si existe la entidad con el identificador id
     * @throws IllegalArgumentException En caso de ser id nulo
     */
    boolean existsById(ID id);

    /**
     * Devuelve la entidad T con identificador id.
     *
     * @param id Identificador de la entidad
     * @return Entidad que tiene como identificador id.
     * @throws IllegalArgumentException En caso de ser id nulo
     */
    T findById(ID id);

    /**
     * Devuelve todas las instancias
     *
     * @return Todas las instancias
     */
    Iterable<T> findAll();

    /**
     * Guarda la entidad entity.
     *
     * @param entity entidad a guardar. No debe ser nulo
     * @return entidad guardada
     * @throws IllegalArgumentException En caso de ser entity nulo
     */
    <S extends T> S save(S entity);
}