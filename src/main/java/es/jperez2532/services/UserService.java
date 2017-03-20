package es.jperez2532.services;

import es.jperez2532.components.ChangePassword;
import es.jperez2532.entities.Account;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Proporciona servicio para registrar una cuenta.
 *
 * Necesitamos un método save() personalizado para generar el password encriptado.
 */
public interface UserService {

    void save(Account account);
    String getPrincipal();
    String update(Account account, String modify, String action);
    void updateOwn(Account account, ChangePassword changePassword);
    void delete(Account account);
    boolean deleteOwn(Account account);
    void addToWatchlist(String username, Long filmId);
    void deleteFromWatchlist(String username, Long filmId);
    Set<Long> makeWatchlistSet (Account account);
    LinkedList<String> getProvincias();
    Account findByUserName(String userName);
    Map<String, Long> getStats();
}
