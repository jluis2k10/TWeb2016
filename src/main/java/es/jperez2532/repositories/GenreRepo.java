package es.jperez2532.repositories;

import es.jperez2532.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la entidad {@link Genre}.
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre la
 * aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen, Spring Data se encarga
 * de procesarlos y de generar las consultas necesarias.
 */
@Repository
public interface GenreRepo extends JpaRepository<Genre, Long> {
    /**
     * Busca un Género por su nombre.
     * @param name el nombre a buscar
     * @return el Género encontrado
     */
    Genre findByName(String name);

    /**
     * Devuelve todos los Géneros existentes ordenados alfabéticamente.
     * @return lista de Géneros ordenados alfabéticamente
     */
    List<Genre> findAllByOrderByNameAsc();
}
