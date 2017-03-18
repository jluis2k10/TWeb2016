package es.jperez2532.controllers;

import es.jperez2532.entities.Film;
import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;
import es.jperez2532.repositories.VoteRepo;
import es.jperez2532.services.FilmService;
import es.jperez2532.services.UserService;
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

@Controller
public class FilmsController extends MainController {

    @Autowired private UserService userService;
    @Autowired private FilmService filmService;
    @Autowired private VoteRepo voteRepo;

    @Transactional // TODO: estudiar qué significa transctional para recuperar entity con lazy-loading
    @RequestMapping(value = "/pelicula/{id}/*", method = RequestMethod.GET)
    public String pelicula(@PathVariable("id") Long id, Principal principal, Model model) {
        Set<Long> userWatchlist = new HashSet<Long>();
        Film film = filmService.findOne(id);
        Long userId = null;
        int myScore = 0;

        if (principal != null) {
            userWatchlist = userService.makeWatchlistSet(userService.findByUserName(principal.getName()));
            userId = userService.findByUserName(principal.getName()).getId();
            Vote vote = voteRepo.findOne(new VotePK(film.getId(), userId));
            if (vote != null)
                myScore = vote.getScore();
        }

        model.addAttribute("film", film);
        model.addAttribute("userWatchlist", userWatchlist);
        model.addAttribute("globalScore", film.getScore().setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact());
        model.addAttribute("myScore", myScore);
        model.addAttribute("userId", userId);
        model.addAttribute("title", film.getTitle());
        return "pelicula/pelicula";
    }

    @RequestMapping("/pelicula/ver/{id}/*")
    public String viewFilm(@PathVariable("id") Long id, Model model) {
        Film film = filmService.findOne(id);
        film.setViews(film.getViews()+1);
        filmService.updateViews(film);
        model.addAttribute("film", film);
        model.addAttribute("title", film.getTitle());
        return "pelicula/reproducir";
    }

    @RequestMapping("/catalogo")
    public String catalogo(Model model, Pageable pageable, Principal principal,
                           @RequestParam(value = "buscar", required = false) String buscar) {
        Set<Long> userWatchlist = new HashSet<Long>();
        Page<Film> page;
        String url_params = "/catalogo?";

        if (principal != null)
            userWatchlist = userService.makeWatchlistSet(userService.findByUserName(principal.getName()));
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

    @RequestMapping("/buscar")
    public String buscar(Model model, Pageable pageable, Principal principal,
                         @RequestParam("ref") String ref, @RequestParam("buscar") String buscar) {
        Set<Long> userWatchlist = new HashSet<Long>();
        String url_params = "/buscar?ref=" + ref + "&buscar=" + buscar + "&";
        Page<Film> page = null;
        List<Film> films = null;

        if (principal != null)
            userWatchlist = userService.makeWatchlistSet(userService.findByUserName(principal.getName()));
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
        return "pelicula/catalogo";
    }

}
