package es.jperez2532.repositories;

import es.jperez2532.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la entidad {@link Country}.
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre la
 * aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen, Spring Data se encarga
 * de procesarlos y de generar las consultas necesarias.
 */
@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
    /**
     * Busca un País por su nombre.
     * @param name el nombre a buscar
     * @return el País encontrado
     */
    Country findByName(String name);

    /**
     * Devuelve todos los Países existentes ordenados alfabéticamente.
     * @return lista de Países ordenados alfabéticamente
     */
    List<Country> findAllByOrderByNameAsc();
}
