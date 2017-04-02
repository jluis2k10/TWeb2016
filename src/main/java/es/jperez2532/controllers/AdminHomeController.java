package es.jperez2532.controllers;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.services.FilmService;
import es.jperez2532.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

/**
 * Controlador de la página índice de la administración.
 */
@Controller
@RequestMapping("/admin")
public class AdminHomeController extends MainController {

    private final FilmService filmService;
    private final UserService userService;

    /**
     * Constructor de la clase con las inyecciones de dependencias apropiadas.
     *
     * @param filmService inyección {@link FilmService}
     * @param userService inyección {@link UserService}
     */
    @Autowired
    public AdminHomeController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    // TODO: para debug de la cache, eliminar
    @Resource(name = "cacheManager")
    private CacheManager cacheManager;

    /**
     * Portada de la administración.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return La vista a mostrar
     */
    @RequestMapping("")
    public String home(Model model) {

        Collection<String> caches = cacheManager.getCacheNames();
        Cache filmsById = cacheManager.getCache("film");
        Cache accountsCache = cacheManager.getCache("account");

        Pageable limit = new PageRequest(0,6, Sort.Direction.DESC, "id");
        Page<Film> films = filmService.findAll(limit);
        Page<Account> accounts = userService.findAll(limit);
        model.addAttribute("films", films.getContent());
        model.addAttribute("accounts", accounts.getContent());

        Long totalFilms = filmService.count();
        Map<String, Film> filmStats = filmService.getTopFilms();
        model.addAttribute("totalFilms", totalFilms);
        model.addAttribute("filmStats", filmStats);

        Map<String, Long> userStats = userService.getStats();
        model.addAttribute("userStats", userStats);

        model.addAttribute("title", "Panel de Administración - PelisUNED");
        return("admin/index");
    }
}
