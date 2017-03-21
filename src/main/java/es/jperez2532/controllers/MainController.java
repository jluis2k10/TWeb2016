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

    /**
     * Genera una lista con todos los géneros disponibles y Spring se encarga de
     * almacenarla en la sesión del usuario. De este modo conseguimos que no sea
     * necesario realizar una llamada a algún método cada vez que se necesite la
     * lista (que es en cada página del frontend del sitio, para el menú principal
     * de navegación).
     * <p>
     * Cuando se añade un nuevo género, se debe eliminar la lista de la sesión para
     * que ésta no contenga información obsoleta. Se regenerará cautomáticamente en
     * la próxima llamada a cualquier página.
     *
     * @return Lista que contiene los géneros disponibles
     */
    @ModelAttribute("genresList")
    public List<Genre> getGenres() {
        return genreRepo.findAllByOrderByNameAsc();
    }

}
