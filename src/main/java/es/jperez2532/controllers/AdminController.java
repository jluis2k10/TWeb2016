package es.jperez2532.controllers;

import es.jperez2532.components.UploadPoster;
import es.jperez2532.entities.*;
import es.jperez2532.repositories.*;
import es.jperez2532.services.FilmService;
import es.jperez2532.validator.FilmValidator;
import es.jperez2532.validator.GenreValidator;
import es.jperez2532.validator.UploadPosterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Necesitamos que implemente ServletContextAware para hacer uso de servletContext.getRealPath()
 * y poder guardar el archivo en el directorio correcto.
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends MainController implements ServletContextAware {

    @Autowired private FilmService filmService;
    @Autowired private FilmRepo filmRepo;
    @Autowired private GenreRepo genreRepo;
    @Autowired private ActorRepo actorRepo;
    @Autowired private DirectorRepo directorRepo;
    @Autowired private CountryRepo countryRepo;
    @Autowired private FilmValidator filmValidator;
    @Autowired private UploadPosterValidator uploadPosterValidator;
    @Autowired private GenreValidator genreValidator;
    /* Necesario para pasarle el path del servidor al método que sube el archivo del poster */
    private ServletContext servletContext;

    @Transactional
    @RequestMapping("")
    public String home(Model model) {
        List<Film> films = filmRepo.findAll();
        model.addAttribute("films", films);
        model.addAttribute("title", "PelisUNED - Panel de Administración");
        return("admin/index");
    }

    @Transactional
    @RequestMapping("/catalogo")
    public String catalogo(Model model, Pageable pageable,
                           @RequestParam(value = "buscar", required = false) String buscar) {
        String url_params = "?";
        Page<Film> page;
        if (buscar != null) {
            page = filmRepo.findByTitleIgnoreCaseContaining(buscar, pageable);
            url_params = "?buscar=" + buscar + "&";
            model.addAttribute("buscando", buscar);
        }
        else
            page = filmRepo.findAll(pageable);


        if (page.getTotalElements() != 0) {
            List<Film> films = page.getContent();
            model.addAttribute("films", films);
        }
        else {
            model.addAttribute("infoMsg", "No existen resultados para el término: <strong>" +
                    buscar + "</strong>");
        }

        model.addAttribute("page", page);
        model.addAttribute("url_params", url_params);
        model.addAttribute("title", "PelisUNED - Administrar Catálogo");
        return("admin/catalogo");
    }

    @RequestMapping(value = "/catalogo/nueva", method = RequestMethod.GET)
    public String nuevaPelicula(Model model) {
        model.addAttribute("nuevaPeliculaForm", new Film());
        model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
        model.addAttribute("title", "PelisUNED - Nueva Película");
        return("admin/nuevaPelicula");
    }

    @RequestMapping(value = "/catalogo/nueva", method = RequestMethod.POST)
    public String nuevaPelicula(@ModelAttribute("nuevaPeliculaForm") Film filmForm,
                                BindingResult bindingResultPelicula,
                                @Valid UploadPoster uploadPoster,
                                Model model, RedirectAttributes redirectAttributes) throws IOException {
        filmValidator.validate(filmForm, bindingResultPelicula);
        uploadPosterValidator.validate(uploadPoster, bindingResultPelicula);

        if (!bindingResultPelicula.hasErrors()) {
            try {
                filmForm.setPoster(uploadPoster.upload(Integer.toString(filmForm.hashCode()), servletContext));
            } catch (RuntimeException e) {
                bindingResultPelicula.rejectValue("poster", e.getMessage());
            }
        }

        if (bindingResultPelicula.hasErrors()) {
            model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
            model.addAttribute("title", "PelisUNED - Nueva Película");
            return ("admin/nuevaPelicula");
        }

        filmService.save(filmForm);
        redirectAttributes.addFlashAttribute("infoMsg",
                "Nueva película añadida con éxito: <strong>" + filmForm.getTitle() + "</strong>.");
        return("redirect:/admin/catalogo");
    }

    @RequestMapping(value = "/catalogo/nuevoGenero", method = RequestMethod.GET)
    public String nuevoGenero(Model model) {
        model.addAttribute("genreForm", new Genre());
        model.addAttribute("title", "PelisUNED - Género Nuevo");
        return("admin/nuevoGenero");
    }

    @RequestMapping(value = "/catalogo/nuevoGenero", method = RequestMethod.POST)
    public String nuevoGenero(@ModelAttribute("genreForm") Genre genreForm,
                              BindingResult bindingResultGenre, Model model,
                              RedirectAttributes redirectAttributes,
                              SessionStatus sessionStatus) {
        genreValidator.validate(genreForm, bindingResultGenre);
        if (bindingResultGenre.hasErrors()) {
            model.addAttribute("title", "PelisUNED - Género Nuevo");
            return("admin/nuevoGenero");
        }
        genreRepo.save(genreForm);
        sessionStatus.setComplete(); // Forzar a que se renueve la sesión con la nueva categoría (para el menú)
        redirectAttributes.addFlashAttribute("infoMsg",
                "Nuevo género añadido con éxito: <strong>" + genreForm.getName() + "</strong>.");
        return("redirect:/admin/catalogo");
    }

    @Transactional
    @RequestMapping(value = "/pelicula/borrar/{id}", method = RequestMethod.GET)
    public String removeFilm(@PathVariable("id") Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        String title = "";
        Film film = filmRepo.findOne(id);
        if (film != null) {
            title = film.getTitle();
            filmRepo.delete(id);
            redirectAttributes.addFlashAttribute("infoMsg",
                    "Película eliminada con éxito: <strong>" + title + "</strong>.");
        }
        else {
            redirectAttributes.addFlashAttribute("infoMsg",
                    "No se ha podido borrar la película con ID = " + id + ".");
        }
        return("redirect:/admin/catalogo");
    }

    @RequestMapping(value = "directoresJSON", method = RequestMethod.GET)
    @ResponseBody
    public String directorsInJSON() {
        List<Director> directorsList = directorRepo.findAll();
        Iterator it = directorsList.iterator();
        return doJSON(it);
    }

    @RequestMapping(value = "actoresJSON", method = RequestMethod.GET)
    @ResponseBody
    public String actorsInJSON() {
        List<Actor> actorsList = actorRepo.findAll();
        Iterator it = actorsList.iterator();
        return doJSON(it);
    }

    @RequestMapping(value = "paisesJSON", method = RequestMethod.GET)
    @ResponseBody
    public String countriesInJSON() {
        List<Country> countriesList = countryRepo.findAll();
        Iterator it = countriesList.iterator();
        return doJSON(it);
    }

    private String doJSON(Iterator<AbstractEntity> it) {
        String jsonString = "{ \"autocompleteData\": {";
        while (it.hasNext()) {
            jsonString += "\"" + it.next().getName() + "\": null";
            if (it.hasNext())
                jsonString += ", ";
        }
        jsonString += "}}";
        return jsonString;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
