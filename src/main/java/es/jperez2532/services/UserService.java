package es.jperez2532.services;

import es.jperez2532.components.ChangePassword;
import es.jperez2532.entities.Account;

import java.util.LinkedList;
import java.util.Set;

/**
 * Proporciona servicio para registrar una cuenta.
 *
 * Necesitamos un método save() personalizado para generar el password encriptado.
 */
public interface UserService {

    void save(Account account);
    String getPrincipal();
    void update(Account account, ChangePassword changePassword);
    void updateWatchlist(Account account);
    Set<Long> makeWatchlistSet (Account account);
    LinkedList<String> getProvincias();
    Account findByUserName(String userName);

}
