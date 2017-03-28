package es.jperez2532.controllers;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;
import es.jperez2532.services.FilmService;
import es.jperez2532.services.UserService;
import es.jperez2532.services.VotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador que maneja aspectos relacionados con la presentación de las películas.
 */
@Controller
public class FilmsController extends MainController {

    private final UserService userService;
    private final FilmService filmService;
    private final VotesService votesService;

    /**
     * Constructor de la clase con las inyecciones de dependencia apropiadas.
     *
     * @param userService  inyección {@link UserService}
     * @param filmService  inyección {@link FilmService}
     * @param votesService inyección {@link VotesService}
     */
    @Autowired
    public FilmsController(UserService userService, FilmService filmService, VotesService votesService) {
        this.userService = userService;
        this.filmService = filmService;
        this.votesService = votesService;
    }

    /**
     * Muestra una película.
     *
     * @param id        ID de la película a mostrar
     * @param principal Token de autenticación del usuario
     * @param model     Interfaz/contenedor para pasar datos a la Vista
     * @return La Vista a mostrar
     */
    @Transactional
    @RequestMapping(value = "/pelicula/{id}/*", method = RequestMethod.GET)
    public String pelicula(@PathVariable("id") Long id, Principal principal, Model model) {
        Set<Long> userWatchlist = new HashSet<>();
        Film film = filmService.findOne(id);
        Long userID = null;
        int myScore = 0;

        if (principal != null) {
            Account account = userService.findByUserName(principal.getName());
            userWatchlist = userService.makeWatchlistSet(account.getWatchlist());
            userID = account.getId();
            Vote vote = votesService.findOne(new VotePK(film.getId(), userID));
            if (vote != null)
                myScore = vote.getScore();
        }

        model.addAttribute("film", film);
        model.addAttribute("userWatchlist", userWatchlist);
        model.addAttribute("globalScore", film.getScore().setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact());
        model.addAttribute("myScore", myScore);
        model.addAttribute("userId", userID);
        model.addAttribute("title", film.getTitle());
        return "pelicula/pelicula";
    }

    /**
     * Muestra la reproducción (simulada) de la película.
     *
     * @param id    ID de la película a reproducir
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return La Vista a mostrar
     */
    @RequestMapping("/pelicula/ver/{id}/*")
    public String viewFilm(@PathVariable("id") Long id, Model model) {
        Film film = filmService.findOne(id);
        film.setViews(film.getViews()+1);
        filmService.updateViews(film);
        model.addAttribute("film", film);
        model.addAttribute("title", film.getTitle());
        return "pelicula/reproducir";
    }

    /**
     * Muestra una lista paginada con todas las películas disponibles o, si existe el
     * término de búsqueda, muestra aquellas películas que lo contengan en alguno de
     * sus campos.
     * <p>
     * La búsqueda que realiza, en caso de hacerse, es diferente a la del método
     * {@link FilmsController#buscar(Model, Pageable, Principal, String, String)}, ya que
     * en este controlador se busca el término en múltiples campos de la película.
     *
     * @param model     Interfaz/contenedor para pasar datos a la Vista
     * @param pageable  Interfaz con información sobre la paginación
     * @param principal Token de autenticación del usuario
     * @param buscar    Término de búsqueda (opcional)
     * @return La Vista a mostrar
     */
    @RequestMapping("/catalogo")
    public String catalogo(Model model, Pageable pageable, Principal principal,
                           @RequestParam(value = "buscar", required = false) String buscar) {
        Set<Long> userWatchlist = new HashSet<>();
        Page<Film> page;
        String url_params = "/catalogo?";

        if (principal != null)
            userWatchlist = userService.makeWatchlistSet(userService.findByUserName(principal.getName()).getWatchlist());
        model.addAttribute("userWatchlist", userWatchlist);

        if (buscar != null) {
            page = filmService.search(buscar, pageable);
            url_params = "/catalogo?buscar=" + buscar + "&";
            model.addAttribute("headTitle", page.getTotalElements() + " resultado" +
                    (page.getTotalElements() > 1 ? "s" : "") + " para: <em>" + buscar + "</em>");
        } else {
            page = filmService.findAll(pageable);
        }

        if (page.getTotalElements() != 0) {
            List<Film> films = page.getContent();
            model.addAttribute("films", films);
        }
        else {
            model.addAttribute("infoMsg", "Sin resultados.");
        }

        model.addAttribute("page", page);
        model.addAttribute("url_params", url_params);
        model.addAttribute("title", "Catálogo - Pelis UNED");
        return "pelicula/catalogo";
    }

    /**
     * Muestra una lista paginada de todas las películas que coincidan con
     * el término de búsqueda.
     * <p>
     * En este caso la búsqueda se realiza en uno solo de los campos de la
     * película. Por ejemplo busca el término dado en el campo Categoría, pero
     * en ninguno más.
     *
     * @param model     Interfaz/contenedor para pasar datos a la Vista
     * @param pageable  Interfaz con información sobre la paginación
     * @param principal Token de autenticación del usuario
     * @param ref       Campo de la Película sobre el que realizar la búsqueda del término
     *                  <code>buscar</code>
     * @param buscar    Término de búsqueda (obligatorio)
     * @return La Vista a mostrar
     */
    @RequestMapping("/buscar")
    public String buscar(Model model, Pageable pageable, Principal principal,
                         @RequestParam("ref") String ref, @RequestParam("buscar") String buscar) {
        Set<Long> userWatchlist = new HashSet<>();
        String url_params = "/buscar?ref=" + ref + "&buscar=" + buscar + "&";
        Page<Film> page = null;
        List<Film> films = null;

        if (principal != null)
            userWatchlist = userService.makeWatchlistSet(userService.findByUserName(principal.getName()).getWatchlist());
        model.addAttribute("userWatchlist", userWatchlist);

        switch (ref) {
            case "genero":
                page = filmService.findByGenre(buscar, pageable);
                break;
            case "director":
                page = filmService.findByDirector(buscar, pageable);
                break;
            case "actor":
                page = filmService.findByActor(buscar, pageable);
                break;
            case "pais":
                page = filmService.findByCountry(buscar, pageable);
                break;
        }

        if (page.getTotalElements() != 0) {
            films = page.getContent();
            model.addAttribute("headTitle", page.getTotalElements() + " resultado" +
                    (page.getTotalElements() > 1 ? "s" : "") + " para: <em>" + buscar + "</em>");
        } else {
            model.addAttribute("infoMsg", "Sin resultados para: <em>" + buscar + "</em>");
        }

        model.addAttribute("films", films);
        model.addAttribute("page", page);
        model.addAttribute("url_params", url_params);
        model.addAttribute("title", "Búsqueda - PelisUNED");
        return "pelicula/catalogo";
    }
}
