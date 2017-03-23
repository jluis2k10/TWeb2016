package es.jperez2532.repositories;

import es.jperez2532.entities.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de la entidad {@link AccountRole}.
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre la
 * aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen, Spring Data se encarga
 * de procesarlos y de generar las consultas necesarias.
 */
@Repository
public interface RoleRepo extends JpaRepository<AccountRole, Long> {
    /**
     * Busca un Rol por su nombre.
     * @param role nombre del rol a buscar
     * @return el Rol encontrado
     */
    AccountRole findByRole(String role);
}
