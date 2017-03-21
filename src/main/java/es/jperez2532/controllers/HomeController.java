package es.jperez2532.controllers;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.services.FilmService;
import es.jperez2532.services.UserService;
import es.jperez2532.validator.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

/**
 * Controlador para la portada del sitio y otras páginas que no tienen cabida
 * en alguno de los otros controladores.
 */
@Controller
public class HomeController extends MainController {

    @Autowired private UserService userService;
    @Autowired private FilmService filmService;
    @Autowired private AccountValidator accountValidator;

    /**
     * Muestra la portada del sitio.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @param principal Token de autenticación del usuario
     * @return La Vista a mostrar
     */
    @RequestMapping("/")
    public String home(Model model, Principal principal) {
        Account account;
        List<Film> watchlistFilms = null;
        Map<String, Collection<Film>> homePageFilms;

        if (principal != null) {
            account = userService.findByUserName(principal.getName());
            watchlistFilms = account.getWatchlist();
        }
        Set<String> randomGenres = filmService.getRandomGenres(2);
        homePageFilms = filmService.findHomePageFilms(15, randomGenres);
        List<String> idsCarousel = Arrays.asList("#one!", "#two!", "#three!", "#four!", "#five!", "#six!");

        model.addAttribute("watchlistFilms", watchlistFilms);
        model.addAttribute("randomGenres", randomGenres);
        model.addAttribute("homePageFilms", homePageFilms);
        model.addAttribute("idsCarousel", idsCarousel);
        model.addAttribute("title", "PelisUNED");
        return "index";
    }

    /**
     * Controlador para las peticiones de loguearse en el sitio.
     *
     * @param request Información sobre la petición HTTP a éste controlador
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return La Vista a mostrar
     */
    @RequestMapping(value = "/login",  method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        String ref = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", ref);
        model.addAttribute("title", "Acceso - PelisUNED");
        return "login";
    }

    /**
     * Muestra el formulario de registro de un nuevo usuario.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return La Vista a mostrar
     */
    @RequestMapping(value = "/registro", method = RequestMethod.GET)
    public String register(Model model) {
        model.addAttribute("registroForm", new Account());
        model.addAttribute("provincias", userService.getProvincias());
        model.addAttribute("title", "Registro - PelisUNED");
        return "registro";
    }

    /**
     * Recoge el formulario (<code>POST</code>) de registro de nuevo usuario.
     *
     * @param accountForm Contenedor de los datos introducidos en el formulario recogido
     * @param bindingResult Errores en el formulario <code>accountForm</code>
     * @param redirectAttributes Interfaz/contenedor para pasar datos a una Redirección
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return Vista a mostrar o Redirección a efectuar
     */
    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public String register(@ModelAttribute("registroForm") Account accountForm,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes,
                           Model model) {
        accountValidator.validate(accountForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("provincias", userService.getProvincias());
            model.addAttribute("title", "Registro - PelisUNED");
            return "registro";
        }
        userService.save(accountForm);
        redirectAttributes.addFlashAttribute("infoMsg",
                "Cuenta registrada. Ya puedes iniciar sesión con ella.");
        return "redirect:/";
    }

    /**
     * Redirecciona a portada a los usuarios a los que se les ha expirado la sesión de
     * forma manual.
     * <p>
     * Se accede debido a la configuración de <code>http.sessionManagement()</code> que
     * encontramos en {@link es.jperez2532.config.SecurityConfig#configure(HttpSecurity)}.
     * <p>
     * Nota: para expirar una sesión de forma manual hay que llamar al componente#método
     * {@link es.jperez2532.components.SessionHandle#expireUserSessions(String)}.
     *
     * @param redirectAttributes Interfaz/contenedor para pasar datos a una Redirección
     * @return La redirección a efectuar
     */
    @RequestMapping(value = "/expiredSession")
    public String expiredSession(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("infoMsg",
                "Tu cuenta ha sido actualizada o eliminada. Intenta iniciar sesión nuevamente.");
        return "redirect:/login";
    }

    /**
     * Muestra una página con información sobre algún error que se haya producido.
     *
     * @return La Vista a mostrar
     */
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error() {
        return "_errors";
    }

}
