package es.jperez2532.controllers;

import es.jperez2532.entities.Film;
import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.repositories.FilmRepo;
import es.jperez2532.repositories.VoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;
import java.security.Principal;

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

}
