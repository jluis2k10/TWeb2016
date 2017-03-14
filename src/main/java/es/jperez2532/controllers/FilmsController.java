package es.jperez2532.controllers;

import es.jperez2532.entities.Film;
import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.repositories.FilmRepo;
import es.jperez2532.repositories.VoteRepo;
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
        Page<Film> page = filmRepo.findAll(pageable);
        List<Film> films = page.getContent();
        String url_params = "?";

        model.addAttribute("films", films);
        model.addAttribute("page", page);
        model.addAttribute("url_params", url_params);
        model.addAttribute("title", "Catálogo - Pelis UNED");
        return "pelicula/catalogo";
    }

}
