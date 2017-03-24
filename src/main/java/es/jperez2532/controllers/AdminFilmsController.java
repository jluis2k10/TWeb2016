package es.jperez2532.controllers;

import es.jperez2532.components.UploadPoster;
import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.entities.Genre;
import es.jperez2532.services.FilmService;
import es.jperez2532.services.UserService;
import es.jperez2532.validator.FilmValidator;
import es.jperez2532.validator.GenreValidator;
import es.jperez2532.validator.UploadPosterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador para las páginas que manejan el catálogo de películas en la
 * administración.
 */
@Controller
@RequestMapping("/admin")
public class AdminFilmsController extends MainController {

    private final FilmService filmService;
    private final UserService userService;
    private final FilmValidator filmValidator;
    private final UploadPosterValidator uploadPosterValidator;
    private final GenreValidator genreValidator;
    /* Necesario para obtener el path real del servidor */
    private final ServletContext servletContext;

    /**
     * Constructor de la clase con las inyecciones de dependencias apropiadas.
     * @param filmService           inyección {@link FilmService}
     * @param userService           inyección {@link UserService}
     * @param filmValidator         inyección {@link FilmValidator}
     * @param uploadPosterValidator inyección {@link UploadPosterValidator}
     * @param genreValidator        inyección {@link GenreValidator}
     * @param servletContext        inyección {@link ServletContext}
     */
    @Autowired
    public AdminFilmsController(FilmService filmService, UserService userService,
            FilmValidator filmValidator, UploadPosterValidator uploadPosterValidator,
            GenreValidator genreValidator, ServletContext servletContext) {
        this.filmService = filmService;
        this.userService = userService;
        this.filmValidator = filmValidator;
        this.uploadPosterValidator = uploadPosterValidator;
        this.genreValidator = genreValidator;
        this.servletContext = servletContext;
    }

    /**
     * Muestra el catálogo, paginado, de todas las películas disponibles en el sistema
     * o de aquellas que coinciden con el término indicado en el parámetro <code>buscar</code>.
     *
     * @param model    Interfaz/contenedor para pasar datos a la Vista
     * @param pageable Interfaz con información sobre la paginación
     * @param buscar   Título de película a buscar (opcional)
     * @return La Vista a mostrar
     */
    @RequestMapping("/catalogo")
    public String catalog(Model model, Pageable pageable,
                          @RequestParam(value = "buscar", required = false) String buscar) {
        String url_params = "?";
        Page<Film> page;
        if (buscar != null) {
            page = filmService.findByTitle(buscar, pageable);
            url_params = "?buscar=" + buscar + "&";
            model.addAttribute("buscando", buscar);
        }
        else
            page = filmService.findAll(pageable);

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
        model.addAttribute("title", "Administrar Catálogo - PelisUNED");
        return("admin/catalogo");
    }

    /**
     * Presenta el formulario para añadir una nueva película al sistema.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return Vista a mostrar
     */
    @RequestMapping(value = "/catalogo/nueva", method = RequestMethod.GET)
    public String addFilm(Model model) {
        model.addAttribute("genres", filmService.findGenresAll());
        model.addAttribute("peliculaForm", new Film());
        model.addAttribute("formActionUrl", "catalogo/nueva");
        model.addAttribute("title", "Nueva Película - PelisUNED");
        return("admin/formPelicula");
    }

    /**
     * Recoge el formulario (<code>POST</code>) de añadir una nueva película al sistema.
     *
     * @param filmForm              Contenedor de los datos introducidos en el formulario recogido
     * @param bindingResultPelicula Errores en el formulario <code>filmForm</code>
     * @param uploadPoster          Objeto {@link UploadPoster} recogido del formulario automáticamente
     *                              (formulario multipart)
     * @param model                 Interfaz/contenedor para pasar datos a la Vista
     * @param redirectAttributes    Interfaz/contenedor para pasar datos a una Redirección
     * @return Vista a mostrar o redirección a efectuar
     * @throws IOException Para informar de errores en la carga de la imagen mediante {@link es.jperez2532.components.GlobalExceptionHandler}
     */
    @RequestMapping(value = "/catalogo/nueva", method = RequestMethod.POST)
    public String addFilm(@ModelAttribute("peliculaForm") Film filmForm,
                          BindingResult bindingResultPelicula,
                          @Valid UploadPoster uploadPoster,
                          Model model, RedirectAttributes redirectAttributes) throws IOException {
        filmValidator.validate(filmForm, bindingResultPelicula);
        uploadPosterValidator.validate(uploadPoster, bindingResultPelicula);

        if (!bindingResultPelicula.hasErrors()) {
            try {
                String newFileName = uploadPoster.upload(Integer.toString(filmForm.hashCode()), servletContext);
                filmForm.setPoster(newFileName);
            } catch (RuntimeException e) {
                bindingResultPelicula.rejectValue("poster", e.getMessage());
            }
        }

        if (bindingResultPelicula.hasErrors()) {
            model.addAttribute("genres", filmService.findGenresAll());
            model.addAttribute("formActionUrl", "catalogo/nueva");
            model.addAttribute("title", "Nueva Película - PelisUNED");
            return ("admin/formPelicula");
        }

        filmService.save(filmForm);
        redirectAttributes.addFlashAttribute("infoMsg",
                "Nueva película añadida con éxito: <strong>" + filmForm.getTitle() + "</strong>.");
        return("redirect:/admin/catalogo");
    }

    /**
     * Muestra el formulario para editar una película del catálogo.
     *
     * @param id                 ID de la película a editar
     * @param model              Interfaz/contenedor para pasar datos a la Vista
     * @param redirectAttributes Interfaz/contenedor para pasar datos a una Redirección
     * @return Vista a mostrar o Redirección a efectuar
     */
    @RequestMapping(value = "/pelicula/editar/{id}", method = RequestMethod.GET)
    public String editFilm(@PathVariable("id") Long id, Model model,
                           RedirectAttributes redirectAttributes) {
        Film film = filmService.findOne(id);
        if (film == null) {
            redirectAttributes.addFlashAttribute("infoMsg",
                    "No se encuentra la película con ID = " + id.toString() + ".");
            return ("redirect:/admin/catalogo");
        }
        model.addAttribute("genres", filmService.findGenresAll());
        model.addAttribute("peliculaForm", film);
        model.addAttribute("formActionUrl", "pelicula/editar/" + id.toString());
        model.addAttribute("title", "Editando Película - PelisUNED");
        return ("admin/formPelicula");
    }

    /**
     * Recoge el formulario (<code>POST</code>) de editar una película.
     *
     * @param id                    ID de la película que se trata de editar.
     * @param filmForm              Contenedor de los datos introducidos en el formulario recogido
     * @param bindingResultPelicula Errores en el formulario <code>filmForm</code>
     * @param uploadPoster          Objeto {@link UploadPoster} recogido del formulario automáticamente
     *                              (formulario multipart)
     * @param model                 Interfaz/contenedor para pasar datos a la Vista
     * @param redirectAttributes    Interfaz/contenedor para pasar datos a una Redirección
     * @return Vista a mostrar o Redirección a efectuar
     * @throws IOException Para informar de errores en la carga de la imagen mediante {@link es.jperez2532.components.GlobalExceptionHandler}
     */
    @RequestMapping(value = "/pelicula/editar/{id}", method = RequestMethod.POST)
    public String editFilm(@PathVariable("id") Long id,
                           @ModelAttribute("peliculaForm") Film filmForm,
                           BindingResult bindingResultPelicula,
                           @Valid UploadPoster uploadPoster,
                           Model model, RedirectAttributes redirectAttributes) throws IOException {
        filmValidator.validateUpdate(filmForm, bindingResultPelicula);

        // ¿Se debe actualizar la imagen del poster?
        if (!uploadPoster.getPosterFile().isEmpty()) {
            uploadPosterValidator.validate(uploadPoster, bindingResultPelicula);
            if (!bindingResultPelicula.hasErrors()) {
                try {
                    String newFileName = uploadPoster.upload(Integer.toString(filmForm.hashCode()), servletContext);
                    uploadPoster.delete(filmForm.getPoster(), servletContext); // Borramos la imagen original
                    filmForm.setPoster(newFileName);
                } catch (RuntimeException e) {
                    bindingResultPelicula.rejectValue("poster", e.getMessage());
                }
            }
        } else {
            // Recuperamos el nombre de la imagen original
            filmForm.setPoster(filmService.findOne(id).getPoster());
        }

        if (bindingResultPelicula.hasErrors()) {
            model.addAttribute("genres", filmService.findGenresAll());
            model.addAttribute("formActionUrl", "pelicula/editar/" + id.toString());
            model.addAttribute("title", "Editando Película - PelisUNED");
            return ("admin/formPelicula");
        }

        filmService.save(filmForm);
        redirectAttributes.addFlashAttribute("infoMsg",
                "Nueva película editada con éxito: <strong>" + filmForm.getTitle() + "</strong>.");
        return("redirect:/admin/catalogo");
    }

    /**
     * Recalcular la puntuación de una película a partir de los votos de los usuarios.
     * <p>
     * Una vez recalculada la puntuación se redirecciona automáticamente al usuario a
     * la página desde donde se hizo la petición.
     *
     * @param id                 ID de la película
     * @param redirectAttributes Interfaz/contenedor para pasar datos a una Redirección
     * @param request            Información sobre la petición HTTP a éste controlador
     * @return Redirección a efectuar
     */
    @RequestMapping(value = "/pelicula/recalcular/{id}", method = RequestMethod.GET)
    public String reDoVotes(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        Film film = filmService.findOne(id);
        BigDecimal score = film.getScore();
        if (film.getNvotes() > 0) {
            filmService.calcScore(film);
        }

        String requestPath = request.getHeader("referer");
        redirectAttributes.addFlashAttribute("infoMsg",
                "Recalculada correctamente la puntuación de <strong>" + film.getTitle() + "</strong> (" +
                        score.toString() + " -> " + film.getScore().toString() + ").");
        return("redirect:"+requestPath);
    }

    /**
     * Presenta el formulario para añadir un nuevo Género.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return La Vista a mostrar
     */
    @RequestMapping(value = "/catalogo/nuevoGenero", method = RequestMethod.GET)
    public String addGenre(Model model) {
        model.addAttribute("genreForm", new Genre());
        model.addAttribute("title", "Género Nuevo - PelisUNED");
        return("admin/nuevoGenero");
    }

    /**
     * Recoge el formulario (<code>POST</code>) de añadir un nuevo Género.
     *
     * @param genreForm          Contenedor de los datos introducidos en el formulario recogido
     * @param bindingResultGenre Errores en el formulario <code>genreForm</code>
     * @param model              Interfaz/contenedor para pasar datos a la Vista
     * @param redirectAttributes Interfaz/contenedor para pasar datos a una Redirección
     * @param sessionStatus      Interfaz con datos sobre la sesión del usuario
     * @return Vista a mostrar o Redirección a efectuar
     */
    @RequestMapping(value = "/catalogo/nuevoGenero", method = RequestMethod.POST)
    public String addGenre(@ModelAttribute("genreForm") Genre genreForm,
                           BindingResult bindingResultGenre, Model model,
                           RedirectAttributes redirectAttributes,
                           SessionStatus sessionStatus) {
        genreValidator.validate(genreForm, bindingResultGenre);
        if (bindingResultGenre.hasErrors()) {
            model.addAttribute("title", "Género Nuevo - PelisUNED");
            return("admin/nuevoGenero");
        }
        filmService.saveGenre(genreForm);
        sessionStatus.setComplete(); // Forzar a que se renueve la sesión con la nueva categoría (para el menú)
        redirectAttributes.addFlashAttribute("infoMsg",
                "Nuevo género añadido con éxito: <strong>" + genreForm.getName() + "</strong>.");
        return("redirect:/admin/catalogo");
    }

    /**
     * Maneja la petición de eliminar una película del sistema.
     *
     * @param id                 ID de la película a eliminar
     * @param redirectAttributes Interfaz/contenedor para pasar datos a una Redirección
     * @return Redirección a efectuar
     */
    @RequestMapping(value = "/pelicula/borrar/{id}", method = RequestMethod.GET)
    public String deleteFilm(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {
        Film film = filmService.findOne(id);
        if (film != null) {
            // Borramos votos asociados a la película
            filmService.deleteVotesFromFilm(id);
            // Eliminamos la película de las listas de reproducción de los usuarios
            for (Account account: film.getListedIn())
                userService.deleteFilmFromWatchlist(account.getUserName(), id);
            // Eliminamos la película
            if (!filmService.delete(film, servletContext))
                redirectAttributes.addFlashAttribute("infoMsg",
                        "Película eliminada con éxito: <strong>" + film.getTitle() + "</strong>.<br>" +
                        "ATENCIÓN: no se pudo eliminar la imagen del poster en el servidor: " + film.getPoster());
            else
                redirectAttributes.addFlashAttribute("infoMsg",
                        "Película eliminada con éxito: <strong>" + film.getTitle() + "</strong>.");
        }
        else
            redirectAttributes.addFlashAttribute("infoMsg",
                    "No se ha podido borrar la película con ID = " + id + ".");

        return("redirect:/admin/catalogo");
    }
}
