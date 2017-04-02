package es.jperez2532.config;

import es.jperez2532.handlers.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("myUserDetailsService")
    UserDetailsService userDetailsService;

    @Autowired
    DataSource dataSource;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new LoginSuccessHandler("/");
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Filtro para codificar a UTF8 los envíos de formularios
        // Ver http://stackoverflow.com/questions/34404247/tomcat-spring-utf-8
        // Tenía problemas al enviar caracteres con acentos desde los formularios
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        http.addFilterBefore(encodingFilter,CsrfFilter.class);

        // Configuración sobre el manejo de sesiones. Necesitamos el bean sessionRegistry para
        // poder acceder a las sesiones.
        http.sessionManagement()
                .maximumSessions(100)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/expiredSession")
                .sessionRegistry(sessionRegistry());

        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/micuenta/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/pelicula/ver/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/rest/votar").hasAnyRole("USER", "ADMIN")
                .antMatchers("/rest/milista/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/registro").anonymous()
                .antMatchers("/login").anonymous()
                .and().formLogin()
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(successHandler())
                // Ver http://www.concretepage.com/spring-4/spring-4-mvc-security-custom-login-form-and-logout-example-with-csrf-protection-using-annotation-and-xml-configuration
                // para recordar cómo funciona logout con csrf activado
                .and().logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .addLogoutHandler(new CookieClearingLogoutHandler("JSESSIONID", "remember-me"))
                    .permitAll()
                .and().rememberMe()
                    .rememberMeParameter("rememberMe")
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(86400)
                .and().csrf()
                .and().exceptionHandling()
                    .accessDeniedPage("/denegado");

        /* Permitimos frames para poder visualizar javadoc y xref
        OJO: en una aplicación para el "mundo real" esto supone un riesgo de seguridad. */
        http.headers().frameOptions().sameOrigin();
    }

    // Configurar login persistente (recuérdame)
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    // Bean para registrar las sesiones
    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
