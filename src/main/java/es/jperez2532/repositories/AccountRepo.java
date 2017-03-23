package es.jperez2532.repositories;

import es.jperez2532.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de la entidad {@link Account}.
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre la
 * aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen, Spring Data se encarga
 * de procesarlos y de generar las consultas necesarias.
 */
@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    /**
     * Devuelve todas las cuentas encontradas para la página indicada.
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las Cuentas encontradas
     */
    Page<Account> findAll(Pageable pageable);

    /**
     * Busca una cuenta por su noombre de usuario.
     * @param userName nombre de usuario a buscar
     * @return la Cuenta encontrada
     */
    Account findByUserName(String userName);

    /**
     * Busca una cuenta por su email asociado.
     * @param email el email de la cuenta a buscar
     * @return la Cuenta encontrada
     */
    Account findByEmail(String email);

    /**
     * Busca entre todas las cuentas que contienen todo (o parte) del nombre
     * de usuario indicado. Devuelve una Página con las Cuentas.
     * @param username nombre de usuario a buscar
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las cuentas encontradas
     */
    Page<Account> findByUserNameIgnoreCaseContaining(String username, Pageable pageable);

    /**
     * Contabiliza las cuentas existentes según el rol que tengan asociado.
     * @param roleName rol a buscar
     * @return número de cuentas que tengan asociado el rol indicado
     */
    Long countByAccountRoles_RoleIgnoreCase(String roleName);

    /**
     * Contabiliza las cuentas activas/inactivas.
     * @param status estado de la cuenta (<code>true</code> para cuentas activas,
     *               <code>false</code> para cuentas desactivadas
     * @return número de cuentas encontradas según su estado
     */
    Long countByActive(boolean status);
}
