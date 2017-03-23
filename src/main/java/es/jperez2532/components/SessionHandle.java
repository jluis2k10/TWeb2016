package es.jperez2532.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Manejador de sesiones, necesario para consiguer expirar manualmente la sesión
 * de un usuario concreto tras haberlo editado por parte de la administración.
 * <p>
 * Credits: http://mtyurt.net/2015/10/27/spring-expiring-all-sessions-of-a-user/
 */
@Component
public class SessionHandle {

    private final SessionRegistry sessionRegistry;

    /**
     * Constructor de la clase con las inyecciones de dependencia necesarias.
     * @param sessionRegistry inyección {@link SessionRegistry}
     */
    @Autowired
    public SessionHandle(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Exirar la sesión de un usuario manualmente.
     * @param username nombre de usuario de la Cuenta a la que se le deben expirar las sesiones
     */
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