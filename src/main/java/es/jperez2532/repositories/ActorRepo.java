package es.jperez2532.repositories;

import es.jperez2532.entities.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la entidad {@link ActorRepo}.
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre la
 * aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen, Spring Data se encarga
 * de procesarlos y de generar las consultas necesarias.
 */
@Repository
public interface ActorRepo extends JpaRepository<Actor, Long> {
    /**
     * Busca un Actor por su nombre.
     * @param name el nombre a buscar
     * @return el Actor encontrado
     */
    Actor findByName(String name);

    /**
     * Devuelve todos los Actores existentes ordenados alfabéticamente.
     * @return lista de Actores ordenados alfabéticamente
     */
    List<Actor> findAllByOrderByNameAsc();
}