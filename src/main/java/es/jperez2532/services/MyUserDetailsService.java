package es.jperez2532.services;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.AccountRole;
import es.jperez2532.repositories.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio utilizado por Spring como un DAO (Data Access Object) del usuario
 * ({@link User}).
 * <p>
 * La necesidad de una implementación propia se debe a que cada proyecto recupera
 * las cuentas de usuarios de forma diferente.
 */
@Service("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    private final AccountRepo accountRepo;

    /**
     * Constructor de la clase con las inyecciones de dependencia apropiadas.
     * @param accountRepo inyección de {@link AccountRepo}
     */
    @Autowired
    public MyUserDetailsService(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepo.findByUserName(username);
        if(account == null)
            throw new UsernameNotFoundException("Username not found");
        return new User(account.getUserName(), account.getPassword(), account.isActive(),
                true, true, true, getGrantedAuthorities(account));
    }

    /**
     * Devuelve las <em>Authorities</em> o roles asociados a una cuenta.
     * @param account Cuenta de usuario
     * @return Lista con los roles de la cuenta
     */
    private List<GrantedAuthority> getGrantedAuthorities(Account account) {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        for(AccountRole accountRole : account.getAccountRoles())
            auths.add(new SimpleGrantedAuthority("ROLE_" + accountRole.getRole()));
        return auths;
    }
}
