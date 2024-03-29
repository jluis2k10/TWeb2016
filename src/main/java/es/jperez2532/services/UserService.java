package es.jperez2532.services;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Proporciona servicio para manejar una cuenta de usuario ({@link Account}).
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos (o después de recibirlos) a la capa
 * de persistencia (interfaces en {@link es.jperez2532.repositories}.
 * <p>
 * Los métodos "redundantes" con aquellos ya presentes en el repositorio de
 * las Cuentas, son necesarios para manejar correctamente la caché del sistema.
 */
public interface UserService {
    /**
     * Busca todos los usuarios y devuelve una página ({@link Page}) que contiene
     * aquellos que se correspondan con la página indicada.
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página con los usuarios
     */
    Page<Account> findAll(Pageable pageable);

    /**
     * Busca una cuenta de usuario por nombre.
     * @param userName nombre de usuario a buscar
     * @return resultado encontrado
     */
    Account findByUserName(String userName);

    /**
     * Busca una cuenta de usuario por nombre. Puede cachear el resultado
     * o no hacerlo en función de lo indicado en el parámetro <code>cacheable</code>.
     * @param userName  nombre de usuario a buscar
     * @param cacheable <code>false</code> para no cachear el resultado
     * @return resultado encontrado
     */
    Account findByUserName(String userName, boolean cacheable);

    /**
     * Devuelve una página ({@link Page}) con los usuarios filtrados por
     * nombre de usuario.
     * @param userName nombre de usuario a buscar
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página con los usuarios encontrados
     */
    Page<Account> findUsersByUserName(String userName, Pageable pageable);

    /**
     * Busca una cuenta de usuario por ID.
     * @param accountId ID de usuario a buscar
     * @return resultado encontrado
     */
    Account findOne(Long accountId);

    /**
     * Busca una cuenta de usuario por su dirección email.
     * @param email email a buscar
     * @return la Cuenta de usuario encontrada
     */
    Account findByEmail(String email);

    /**
     * Devuelve el número de usuarios existentes para el rol indicado.
     * @param role rol a contar usuarios
     * @return número de usuarios para un rol
     */
    Long countByRole(String role);

    /**
     * Persiste una Cuenta de usuario.
     * @param account Cuenta a persistir
     */
    void save(Account account);

    /**
     * Devuelve el nombre de usuario de la Cuenta actual (desde donde
     * se hace la petición).
     * @return nombre de usuario de la Cuenta
     */
    String getPrincipal();

    /**
     * Modifica ciertos parámetros de una Cuenta. En concreto puede modificar
     * el estado de cuenta activa/inactiva y el de si es una cuenta con rol de
     * administrador o no.
     * <p>
     * Este método es utilizado por los administradores del sitio para editar
     * las cuentas de otros usuarios. Devuelve un mensaje que se enviará mediante
     * una respuesta JSON.
     * @param account la Cuenta a editar
     * @param modify  parámetro a editar ("active", "admin")
     * @param action  acción a realizar ("add", "delete"), donde "add" significa
     *                añadir al grupo de activos/administradores y "delete" significa
     *                eliminar del grupo de activos/administradores
     * @return un mensaje que indica el resultado de la operación (éxito/fracaso)
     */
    String update(Account account, String modify, String action);

    /**
     * Modifica los parámetros de una Cuenta de usuario.
     * <p>
     * Sólo puede modificar los parámetros de la cuenta del propio usuario que hace
     * la solicitud.
     * @param account Cuenta con los datos modificados que se debe persistir
     */
    void updateOwn(Account account);

    /**
     * Elimina una Cuenta de usuario.
     * @param account la Cuenta a eliminar
     */
    void delete(Account account);

    /**
     * Añadir una Película a la lista de reproducción del usuario.
     * @param username nombre de usuario de la Cuenta
     * @param film     la película a añadir a la lista de reproducción
     */
    void addFilmToWatchlist(String username, Film film);

    /**
     * Eliminar una Película de la lista de reproducción del usuario.
     * @param username nombre de usuario de la Cuenta
     * @param filmId   ID de la película a eliminar de la lista de reproducción
     */
    void deleteFilmFromWatchlist(String username, Long filmId);

    /**
     * Obtiene estadísticas del conjunto de Cuentas de usuarios presentes en
     * todo el sistema.
     * @return las estadísticas generadas
     */
    Map<String, Long> getStats();

    /**
     * Obtiene un conjunto con los IDs de las películas que un usuario tiene
     * en su lista de reproducción.
     * <p>
     * Es útil para manejar la lista de reproducción de los usuarios en la capa
     * de presentación sin tener que andar iterando por la lista de Películas
     * completa.
     * @param watchlist Cuenta desde donde construir el conjunto
     * @return el conjunto con los IDs de las películas en la lista de reproducción
     *         del usuario
     */
    Set<Long> makeWatchlistSet (List<Film> watchlist);

    /**
     * Devuelve una lista enlazada con todas las provincias de España.
     * @return Lista con las provincias
     */
    LinkedList<String> getProvincias();
}
