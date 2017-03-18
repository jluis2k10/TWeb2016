package es.jperez2532.services;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.AccountRole;
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

@Service("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = userService.findByUserName(username);
        System.err.println("loadUser: " + account);
        if(account == null)
            throw new UsernameNotFoundException("Username not found");
        // TODO: estudiar llamada al constructor User()
        return new User(account.getUserName(), account.getPassword(), account.isActive(),
                true, true, true, getGrantedAuthorities(account));
    }

    private List<GrantedAuthority> getGrantedAuthorities(Account account) {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        for(AccountRole accountRole : account.getAccountRoles()) {
            System.err.println("AccountRoles: " + accountRole);
            auths.add(new SimpleGrantedAuthority("ROLE_" + accountRole.getRole()));
        }
        System.err.println("Authorities: " + auths);
        return auths;

    }
}
