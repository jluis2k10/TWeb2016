package es.jperez2532.repositories;

import es.jperez2532.entities.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la entidad {@link Director}.
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre la
 * aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen, Spring Data se encarga
 * de procesarlos y de generar las consultas necesarias.
 */
@Repository
public interface DirectorRepo extends JpaRepository<Director, Long> {
    /**
     * Busca un Director por su nombre.
     * @param name el nombre a buscar
     * @return el Director encontrado
     */
    Director findByName(String name);

    /**
     * Devuelve todos los Directores existentes ordenados alfabéticamente.
     * @return lista de Directores ordenados alfabéticamente
     */
    List<Director> findAllByOrderByNameAsc();
}
