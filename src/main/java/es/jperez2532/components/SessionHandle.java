package es.jperez2532.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * http://mtyurt.net/2015/10/27/spring-expiring-all-sessions-of-a-user/
 */
@Component
public class SessionHandle {

    @Autowired private SessionRegistry sessionRegistry;

    public void expireUserSessions(String username) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof User) {
                UserDetails userDetails = (UserDetails) principal;
                if (userDetails.getUsername().equals(username)) {
                    for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
                        information.expireNow();
                    }
                }
            }
        }
    }
}