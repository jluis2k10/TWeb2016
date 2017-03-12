package es.jperez2532.controllers;

import es.jperez2532.entities.Genre;
import es.jperez2532.repositories.GenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

/**
 * Con el resto de controladores extendiendo este controlador, conseguimos
 * que las variables de sesión estén disponibles para todos ellos sin importar
 * cuál sea el primer controlador al que se acceda.
 */
@Controller
@SessionAttributes(value = {"genresList"}, types = {Genre.class})
public class MainController {

    @Autowired private GenreRepo genreRepo;

    @ModelAttribute("genresList")
    public List<Genre> getGenres() {
        return genreRepo.findAllByOrderByNameAsc();
    }

}
