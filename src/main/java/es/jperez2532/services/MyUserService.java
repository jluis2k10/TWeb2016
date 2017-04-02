package es.jperez2532.services;

import es.jperez2532.components.SessionHandle;
import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Servicios para operaciones con Usuarios.
 */
@Service
public class MyUserService implements UserService {

    private final UserDetailsService userDetailsService;
    private final AccountRepo accountRepo;
    private final RoleRepo roleRepo;
    private final SessionHandle sessionHandle;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    /**
     * Constructor de la clase con las inyecciones de dependencia apropiadas.
     * @param userDetailsService    inyección de {@link UserDetailsService}
     * @param accountRepo           inyección de {@link AccountRepo}
     * @param roleRepo              inyección de {@link RoleRepo}
     * @param sessionHandle         inyección de {@link SessionHandle}
     * @param bCryptPasswordEncoder inyección de {@link BCryptPasswordEncoder}
     */
    @Autowired
    public MyUserService(UserDetailsService userDetailsService, AccountRepo accountRepo,
            RoleRepo roleRepo, SessionHandle sessionHandle,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.accountRepo = accountRepo;
        this.roleRepo = roleRepo;
        this.sessionHandle = sessionHandle;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    public Page<Account> findAll(Pageable pageable) {
        return accountRepo.findAll(pageable);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Ojo no está cacheado
     */
    public Account findOne(Long accountId) {
        return accountRepo.findOne(accountId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "account", key = "#userName")
    public Account findByUserName(String userName) {
        return accountRepo.findByUserName(userName);
    }

    /**
     * {@inheritDoc}
     */
    public Account findByUserName(String userName, boolean cacheable) {
        if(cacheable)
            return this.findByUserName(userName);
        return accountRepo.findByUserName(userName);
    }

    /**
     * {@inheritDoc}
     */
    public Page<Account> findUsersByUserName(String userName, Pageable pageable) {
        return accountRepo.findByUserNameIgnoreCaseContaining(userName, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countByRole(String role) {
        return accountRepo.countByAccountRoles_RoleIgnoreCase(role);
    }

    /**
     * {@inheritDoc}
     */
    public Account findByEmail(String email) {
        return accountRepo.findByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = "account", key = "#account.userName")
    public void save(Account account) {
        // Si el password está vacío viene de updateOwn() (sin modificar) y debemos mantener el anterior
        if (account.getPassword() == null)
            account.setPassword(accountRepo.findOne(account.getId()).getPassword());
        else
            account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        if (account.getAccountRoles().isEmpty())
            account.getAccountRoles().add(roleRepo.findByRole("USER"));
        accountRepo.save(account);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public String getPrincipal() {
        String userName;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails)
            userName = ((UserDetails) principal).getUsername();
        else
            userName = principal.toString();
        return userName;
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = "account", key = "#account.userName")
    public String update(Account account, String modify, String action) {
        String response = "";

        // Cambiar estado admin
        if (modify.equals("admin")) {
            if (action.equals("add")) {
                account.getAccountRoles().add(roleRepo.findByRole("ADMIN"));
                response = account.getUserName() + " añadido al grupo de Administradores";
            }
            else if (action.equals("delete")) {
                account.getAccountRoles().remove(roleRepo.findByRole("ADMIN"));
                response = account.getUserName() + " eliminado del grupo de Administradores";
            }
        }
        // Cambiar estado activo
        else if (modify.equals("active")) {
            if (action.equals("add")) {
                account.setActive(true);
                response = account.getUserName() + " activado.";
            }
            else if (action.equals("delete")) {
                account.setActive(false);
                response = account.getUserName() + " desactivado.";
            }
        }
        accountRepo.save(account);
        // Expiramos las sesiones del usuario recién editado
        sessionHandle.expireUserSessions(account.getUserName());
        return response;
    }

    /**
     * {@inheritDoc}
     */
    public void updateOwn(Account account) {
        Account oldAccount = accountRepo.findOne(account.getId());
        // Sólo si el nuevo password no está vacío
        if (!account.getNewPassword().equals(""))
            account.setPassword(account.getNewPassword());
        account.setAccountRoles(oldAccount.getAccountRoles());
        account.setWatchlist(oldAccount.getWatchlist());
        account.setActive(oldAccount.isActive());
        this.save(account);
        /* Recargar contexto de seguridad con la info actualizada (en este caso no es necesario
        exipirar la sesión del usuario), basta con regenerarla con la nueva info */
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUserName());
        Authentication auth = new PreAuthenticatedAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Necesitamos eliminar de forma manual todas las asociaciones que se generan
     * con otras entidades mediante las tablas "join", que en este caso son asociaciones
     * con Roles y Películas. De este modo Hibernate eliminará las entradas correspondientes
     * a estas asociaciones de forma automática.
     * <p>
     * También hay que acordarse de eliminar todos los posibles Votos emitidos por
     * el usuario que se está borrando, y consecuentemente actualizar las puntuaciones
     * de las Películas afectadas.
     */
    @CacheEvict(value = "account", key = "#account.userName")
    public void delete(Account account) {
        account.getAccountRoles().clear();
        account.getWatchlist().clear();
        accountRepo.delete(account);
        sessionHandle.expireUserSessions(account.getUserName());
    }

    /**
     * {@inheritDoc}
     * <p>
     * OJO: si no se elimina aquí la caché que almacena la película antes de
     * insertarla en la watchlist surgen conflictos
     */
    @Caching(evict = {
            @CacheEvict(value = "film", key = "#film.id"),
            @CacheEvict(value = "account", key = "#username")})
    public void addFilmToWatchlist(String username, Film film) {
        Account account = this.findByUserName(username);
        account.getWatchlist().add(film);
        accountRepo.save(account);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = "account", key = "#username")
    public void deleteFilmFromWatchlist(String username, Long filmId) {
        Account account = this.findByUserName(username);
        Iterator<Film> it = account.getWatchlist().iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(filmId)) {
                it.remove();
                break;
            }
        }
        accountRepo.save(account);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", accountRepo.count());
        stats.put("adminUsers", accountRepo.countByAccountRoles_RoleIgnoreCase("admin"));
        stats.put("inactiveUsers", accountRepo.countByActive(false));
        return stats;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public Set<Long> makeWatchlistSet (List<Film> watchlist) {
        Set<Long> watchlistSet = new HashSet<>();
        for(Film film: watchlist)
            watchlistSet.add(film.getId());
        return watchlistSet;
    }

    /**
     * {@inheritDoc}
     */
    public LinkedList<String> getProvincias() {
        LinkedList<String> provincias = new LinkedList<>();
        provincias.add("Álava");
        provincias.add("Albacete");
        provincias.add("Alicante");
        provincias.add("Almería");
        provincias.add("Asturias");
        provincias.add("Ávila");
        provincias.add("Badajoz");
        provincias.add("Barcelona");
        provincias.add("Burgos");
        provincias.add("Cáceres");
        provincias.add("Cádiz");
        provincias.add("Cantabria");
        provincias.add("Castellón");
        provincias.add("Ciudad Real");
        provincias.add("Córdoba");
        provincias.add("La Coruña");
        provincias.add("Cuenca");
        provincias.add("Gerona");
        provincias.add("Granada");
        provincias.add("Guadalajara");
        provincias.add("Guipúzcoa");
        provincias.add("Huelva");
        provincias.add("Huesca");
        provincias.add("Baleares");
        provincias.add("Jaén");
        provincias.add("León");
        provincias.add("Lérida");
        provincias.add("Lugo");
        provincias.add("Comunidad de Madrid");
        provincias.add("Málaga");
        provincias.add("Región de Murcia");
        provincias.add("Navarra");
        provincias.add("Orense");
        provincias.add("Palencia");
        provincias.add("Las Palmas");
        provincias.add("Pontevedra");
        provincias.add("La Rioja");
        provincias.add("Salamanca");
        provincias.add("Segovia");
        provincias.add("Sevilla");
        provincias.add("Soria");
        provincias.add("Tarragona");
        provincias.add("Santa Cruz de Tenerife");
        provincias.add("Teruel");
        provincias.add("Toledo");
        provincias.add("Valencia");
        provincias.add("Valladolid");
        provincias.add("Vizcaya");
        provincias.add("Zamora");
        provincias.add("Zaragoza");
        provincias.add("Ceuta");
        provincias.add("Melilla");
        return provincias;
    }
}
