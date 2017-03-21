package es.jperez2532.services;

import es.jperez2532.components.ChangePassword;
import es.jperez2532.components.SessionHandle;
import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class MyUserService implements UserService {

    @Autowired private AccountRepo accountRepo;
    @Autowired private RoleRepo roleRepo;
    @Autowired private VotesService votesService;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private SessionHandle sessionHandle;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private FilmService filmService;


    /**
     * Ojo no está cacheado
     *
     * @param accountId
     * @return
     */
    public Account findOne(Long accountId) {
        return accountRepo.findOne(accountId);
    }

    @Cacheable(value = "account", key = "#userName")
    public Account findByUserName(String userName) {
        return accountRepo.findByUserName(userName);
    }

    @CacheEvict(value = "account", key = "#account.userName")
    public void save(Account account) {
        // Si el password está vacío viene de update() (sin modificar) y debemos mantener el anterior
        if (account.getPassword() == null)
            account.setPassword(accountRepo.findOne(account.getId()).getPassword());
        else
            account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        account.setActive(true);
        if (account.getAccountRoles().isEmpty())
            account.getAccountRoles().add(roleRepo.findByRole("USER"));
        accountRepo.save(account);
    }

    @Transactional(readOnly = true)
    public String getPrincipal() {
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        }
        else {
            userName = principal.toString();
        }
        return userName;
    }

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

    @CacheEvict(value = "account", key = "#account.userName")
    public void updateOwn(Account account, ChangePassword changePassword) {
        // Sólo si el nuevo password no está vacío
        if (changePassword.getNewPassword() != "")
            account.setPassword(changePassword.getNewPassword());
        if(account.getAccountRoles().isEmpty())
            account.setAccountRoles(accountRepo.findOne(account.getId()).getAccountRoles());
        this.save(account);
        // Recargar contexto de seguridad con la info actualizada
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUserName());
        Authentication auth = new PreAuthenticatedAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @CacheEvict(value = "account", key = "#account.userName")
    public void delete(Account account) {
        // TODO: no entiendo por qué tengo que hacer antes esto. Hibernate falla al intentar borrar
        // una cuenta con roles (intenta borrar los roles asociados a la cuenta). Y lo mismo con la watchlist.
        account.setAccountRoles(null);
        account.setWatchlist(null);

        // Borrar votos emitidos por este usuario
        votesService.deleteVotesFromAccount(account.getId());
        accountRepo.delete(account);
        sessionHandle.expireUserSessions(account.getUserName());
    }

    /**
     * No se permite eliminar la cuenta del usuario si es la única con el rol de administrador.
     * @param account
     * @return
     */
    @CacheEvict(value = "account", key = "#account.userName")
    public boolean deleteOwn(Account account) {
        if (account.isAdmin() && accountRepo.countByAccountRoles_RoleIgnoreCase("ADMIN") == 1)
            return false;
        this.delete(account);
        return true;
    }

    // OJO: si no se elimina aquí la caché que almacena la película antes de
    // insertarla en la watchlist surgen conflictos
    @Caching(evict = {
            @CacheEvict(value = "film", key = "#filmId"),
            @CacheEvict(value = "account", key = "#username")})
    public void addToWatchlist(String username, Long filmId) {
        Account account = this.findByUserName(username);
        account.getWatchlist().add(filmService.findOne(filmId));
        accountRepo.save(account);
    }

    @CacheEvict(value = "account", key = "#username")
    public void deleteFromWatchlist(String username, Long filmId) {
        Account account = this.findByUserName(username);
        Iterator<Film> it = account.getWatchlist().iterator();
        while (it.hasNext()) {
            if (it.next().getId() == filmId) {
                it.remove();
                break;
            }
        }
        accountRepo.save(account);
    }

    @Transactional
    public Set<Long> makeWatchlistSet (Account account) {
        Set<Long> watchlistSet = new HashSet<Long>();
        for(Film film: account.getWatchlist()) {
            watchlistSet.add(film.getId());
        }
        return watchlistSet;
    }

    public LinkedList<String> getProvincias() {
        LinkedList<String> provincias = new LinkedList<String>();
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

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<String, Long>();
        stats.put("totalUsers", accountRepo.count());
        stats.put("adminUsers", accountRepo.countByAccountRoles_RoleIgnoreCase("admin"));
        stats.put("inactiveUsers", accountRepo.countByActive(false));
        return stats;
    }
}
