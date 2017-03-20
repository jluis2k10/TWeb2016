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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class MyUserService implements UserService {

    @Autowired private AccountRepo accountRepo;
    @Autowired private RoleRepo roleRepo;
    @Autowired private SessionHandle sessionHandle;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private FilmService filmService;

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
    public void update(Account account, ChangePassword changePassword) {
        // Sólo si el nuevo password no está vacío
        if (changePassword.getNewPassword() != "")
            account.setPassword(changePassword.getNewPassword());
        if(account.getAccountRoles().isEmpty())
            account.setAccountRoles(accountRepo.findOne(account.getId()).getAccountRoles());
        this.save(account);
    }

    @CacheEvict(value = "account", key = "#account.userName")
    public void delete(Account account) {
        // TODO: no entiendo por qué tengo que hacer antes esto. Hibernate falla al intentar borrar
        // una cuenta con roles (intenta borrar los roles asociados a la cuenta). Y lo mismo con la watchlist.
        account.setAccountRoles(null);
        account.setWatchlist(null);

        accountRepo.delete(account);
        filmService.reDoVotes(account.getAccountVotes());
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
}
