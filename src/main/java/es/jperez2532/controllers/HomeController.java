package es.jperez2532.controllers;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.repositories.FilmRepo;
import es.jperez2532.services.FilmService;
import es.jperez2532.services.UserService;
import es.jperez2532.validator.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
public class HomeController extends MainController {

    @Autowired private UserService userService;
    @Autowired private FilmService filmService;
    @Autowired private AccountValidator accountValidator;
    @Autowired private FilmRepo filmRepo;

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

    @RequestMapping(value = "/login",  method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        String ref = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", ref);
        model.addAttribute("title", "PelisUNED - Acceso");
        return "login";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.GET)
    public String registro(Model model) {
        model.addAttribute("registroForm", new Account());
        model.addAttribute("provincias", userService.getProvincias());
        model.addAttribute("title", "PelisUNED - Registro");
        return "registro";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public String registro(@ModelAttribute("registroForm") Account accountForm,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes,
                           Model model) {
        accountValidator.validate(accountForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("provincias", userService.getProvincias());
            model.addAttribute("title", "PelisUNED - Registro");
            return "registro";
        }
        userService.save(accountForm);
        redirectAttributes.addFlashAttribute("infoMsg",
                "Cuenta registrada. Ya puedes iniciar sesión con ella.");
        return "redirect:/";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error(Model model) {
        return "_errors";
    }

}
