package es.jperez2532.controllers;

import es.jperez2532.components.UploadPoster;
import es.jperez2532.entities.Film;
import es.jperez2532.entities.Genre;
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
        model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
        model.addAttribute("peliculaForm", new Film());
        model.addAttribute("formActionUrl", "catalogo/nueva");
        model.addAttribute("title", "PelisUNED - Nueva Película");
        return("admin/formPelicula");
    }

    @RequestMapping(value = "/catalogo/nueva", method = RequestMethod.POST)
    public String nuevaPelicula(@ModelAttribute("peliculaForm") Film filmForm,
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
            model.addAttribute("formActionUrl", "catalogo/nueva");
            model.addAttribute("title", "PelisUNED - Nueva Película");
            return ("admin/formPelicula");
        }

        filmService.save(filmForm);
        redirectAttributes.addFlashAttribute("infoMsg",
                "Nueva película añadida con éxito: <strong>" + filmForm.getTitle() + "</strong>.");
        return("redirect:/admin/catalogo");
    }

    @RequestMapping(value = "/pelicula/editar/{id}", method = RequestMethod.GET)
    public String editFilm(@PathVariable("id") Long id, Model model,
                           RedirectAttributes redirectAttributes) {
        Film film = filmRepo.findOne(id);
        if (film == null) {
            redirectAttributes.addFlashAttribute("infoMsg",
                    "No se encuentra la película con ID = " + id.toString() + ".");
            return ("redirect:/admin/catalogo");
        }
        model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
        model.addAttribute("peliculaForm", film);
        model.addAttribute("formActionUrl", "pelicula/editar/" + id.toString());
        model.addAttribute("title", "PelisUNED - Editando Película");
        return ("admin/formPelicula");
    }

    @RequestMapping(value = "/pelicula/editar/{id}", method = RequestMethod.POST)
    public String editFilm(@PathVariable("id") Long id,
                             @ModelAttribute("peliculaForm") Film filmForm,
                             BindingResult bindingResultPelicula,
                             @Valid UploadPoster uploadPoster,
                             Model model, RedirectAttributes redirectAttributes) throws IOException {
        filmValidator.validateUpdate(filmForm, bindingResultPelicula);
        // Recuperamos el nombre de la imagen original
        // TODO: no me gusta hacer una query sólo para esto, mirar cómo meterlo en el form
        filmForm.setPoster(filmRepo.findOne(id).getPoster());

        if (!uploadPoster.getPosterFile().isEmpty()) {
            uploadPosterValidator.validate(uploadPoster, bindingResultPelicula);
            if (!bindingResultPelicula.hasErrors()) {
                try {
                    // Primero borramos la imagen original
                    uploadPoster.delete(filmForm.getPoster(), servletContext);
                    filmForm.setPoster(uploadPoster.upload(Integer.toString(filmForm.hashCode()), servletContext));
                } catch (RuntimeException e) {
                    bindingResultPelicula.rejectValue("poster", e.getMessage());
                }
            }
        }

        if (bindingResultPelicula.hasErrors()) {
            model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
            model.addAttribute("formActionUrl", "pelicula/editar/" + id.toString());
            model.addAttribute("title", "PelisUNED - Editando Película");
            return ("admin/formPelicula");
        }

        filmService.save(filmForm);
        redirectAttributes.addFlashAttribute("infoMsg",
                "Nueva película editada con éxito: <strong>" + filmForm.getTitle() + "</strong>.");
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
        Film film = filmRepo.findOne(id);
        if (film != null) {
            filmRepo.delete(id);
            redirectAttributes.addFlashAttribute("infoMsg",
                    "Película eliminada con éxito: <strong>" + film.getTitle() + "</strong>.");
        }
        else
            redirectAttributes.addFlashAttribute("infoMsg",
                    "No se ha podido borrar la película con ID = " + id + ".");
        return("redirect:/admin/catalogo");
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
