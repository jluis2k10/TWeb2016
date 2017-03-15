package es.jperez2532.controllers;

import es.jperez2532.entities.Actor;
import es.jperez2532.entities.Film;
import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.repositories.ActorRepo;
import es.jperez2532.repositories.FilmRepo;
import es.jperez2532.repositories.VoteRepo;
import es.jperez2532.services.FilmService;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@EnableWebMvc
public class FilmsController extends MainController {

    @Autowired private FilmRepo filmRepo;
    @Autowired private AccountRepo accountRepo;
    @Autowired private VoteRepo voteRepo;
    @Autowired private FilmService filmService;
    @Autowired private ActorRepo actorRepo;

    @Transactional // TODO: estudiar qué significa transctional para recuperar entity con lazy-loading
    @RequestMapping(value = "/pelicula/{id}/*", method = RequestMethod.GET)
    public String pelicula(@PathVariable("id") Long id, Principal principal, Model model) {
        Film film = filmRepo.getOne(id);
        Long userId = null;
        int myScore = 0;
        if (principal != null) {
            userId = accountRepo.findByUserName(principal.getName()).getId();
            Vote vote = voteRepo.findOne(new VotePK(film.getId(), userId));
            if (vote != null)
                myScore = vote.getScore();
        }
        model.addAttribute("film", film);
        model.addAttribute("globalScore", film.getScore().setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact());
        model.addAttribute("myScore", myScore);
        model.addAttribute("userId", userId);
        model.addAttribute("title", film.getTitle());
        return "pelicula/pelicula";
    }

    @RequestMapping("/catalogo")
    public String catalogo(Model model, Pageable pageable,
                           @RequestParam(value = "buscar", required = false) String buscar) {
        Page<Film> page;
        String url_params = "/catalogo?";
        if (buscar != null) {
            page = filmService.search(buscar, pageable);
            url_params = "/catalogo?buscar=" + buscar + "&";
            model.addAttribute("headTitle", page.getTotalElements() + " resultado" +
                    (page.getTotalElements() > 1 ? "s" : "") + " para: <em>" + buscar + "</em>");
        } else {
            page = filmRepo.findAll(pageable);
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
    public String buscar(Model model, Pageable pageable,
                         @RequestParam("ref") String ref, @RequestParam("buscar") String buscar) {
        String url_params = "/buscar?ref=" + ref + "&buscar=" + buscar + "&";
        Page<Film> page = null;
        List<Film> films = null;

        Actor actor = actorRepo.findByName("Robert Duvall");

        switch (ref) {
            case "genero":
                page = filmRepo.findByFilmGenres_NameIgnoreCase(buscar, pageable);
                break;
            case "director":
                page = filmRepo.findByFilmDirectors_NameIgnoreCase(buscar, pageable);
                break;
            case "actor":
                page = filmRepo.findDistinctByFilmStars_NameIgnoreCaseOrFilmSupportings_NameIgnoreCase(buscar, buscar, pageable);
                break;
            case "pais":
                page = filmRepo.findByFilmCountries_NameIgnoreCase(buscar, pageable);
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
