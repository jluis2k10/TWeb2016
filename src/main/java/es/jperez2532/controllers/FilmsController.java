package es.jperez2532.controllers;

import es.jperez2532.entities.Film;
import es.jperez2532.repositories.FilmRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Controller
@EnableWebMvc
public class FilmsController extends MainController {

    @Autowired private FilmRepo filmRepo;

    @Transactional // TODO: estudiar qué significa transctional para recuperar entity con lazy-loading
    @RequestMapping(value = "/pelicula/{id}/*", method = RequestMethod.GET)
    public String pelicula(@PathVariable("id") Long id, Model model) {
        Film film = filmRepo.getOne(id);
        model.addAttribute("film", film);
        model.addAttribute("title", film.getTitle());
        return "pelicula/pelicula";
    }

}
