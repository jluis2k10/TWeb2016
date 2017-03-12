package es.jperez2532.components;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Manejador para redirigir a la última página en la que se estuvo
 * antes de acceder al login.
 */
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public LoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session != null) {
            String redirectUrl = (String) session.getAttribute("url_prior_login");
            if (redirectUrl != null) {
                // we do not forget to clean this attribute from session
                session.removeAttribute("url_prior_login");
                // then we redirect
                try {
                    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                } catch (IOException e) {
                    throw new IOException(e);
                }
            } else {
                try {
                    super.onAuthenticationSuccess(request, response, authentication);
                } catch (ServletException e) {
                    throw new ServletException(e);
                }
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}