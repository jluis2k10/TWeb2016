package es.jperez2532.controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.jperez2532.components.UploadPoster;
import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.entities.Genre;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.repositories.FilmRepo;
import es.jperez2532.repositories.GenreRepo;
import es.jperez2532.services.FilmService;
import es.jperez2532.services.UserService;
import es.jperez2532.validator.FilmValidator;
import es.jperez2532.validator.GenreValidator;
import es.jperez2532.validator.UploadPosterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Controlador de las páginas de administración.
 * <p>
 * Se utilizan directamente los repositorios de las entidades en vez de los diferentes servicios
 * de forma deliberada, con objeto de evitar recoger elementos cacheados.
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends MainController {

    @Autowired private FilmService filmService;
    @Autowired private UserService userService;
    @Autowired private FilmRepo filmRepo;
    @Autowired private AccountRepo accountRepo;
    @Autowired private GenreRepo genreRepo;
    @Autowired private FilmValidator filmValidator;
    @Autowired private UploadPosterValidator uploadPosterValidator;
    @Autowired private GenreValidator genreValidator;
    /* Necesario para obtener el path real del servidor */
    @Autowired private ServletContext servletContext;

    // TODO: para debug de la cache, eliminar
    /*@Resource(name = "cacheManager")
    private CacheManager cacheManager;*/

    /**
     * Portada de la administración.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return La vista a mostrar
     */
    @RequestMapping("")
    public String home(Model model) {

        /*Collection<String> caches = cacheManager.getCacheNames();
        Cache filmsById = cacheManager.getCache("film");
        Cache accountsCache = cacheManager.getCache("account");*/

        Pageable limit = new PageRequest(0,6, Sort.Direction.DESC, "id");
        Page<Film> films = filmRepo.findAll(limit);
        Page<Account> accounts = accountRepo.findAll(limit);
        model.addAttribute("films", films.getContent());
        model.addAttribute("accounts", accounts.getContent());

        Long totalFilms = filmRepo.count();
        Map<String, Film> filmStats = filmService.getStats();
        model.addAttribute("totalFilms", totalFilms);
        model.addAttribute("filmStats", filmStats);

        Map<String, Long> userStats = userService.getStats();
        model.addAttribute("userStats", userStats);

        model.addAttribute("title", "Panel de Administración - PelisUNED");
        return("admin/index");
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
        model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
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
            model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
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
        Film film = filmRepo.findOne(id);
        if (film == null) {
            redirectAttributes.addFlashAttribute("infoMsg",
                    "No se encuentra la película con ID = " + id.toString() + ".");
            return ("redirect:/admin/catalogo");
        }
        model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
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
            filmForm.setPoster(filmRepo.findOne(id).getPoster());
        }

        if (bindingResultPelicula.hasErrors()) {
            model.addAttribute("genres", genreRepo.findAllByOrderByNameAsc());
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
        Film film = filmRepo.findOne(id);
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
        genreRepo.save(genreForm);
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
        Film film = filmRepo.findOne(id);
        if (film != null) {
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

    /**
     * Muestra la lista, paginada, de todos los usuarios registrados en el sistema o de
     * aquellos que coinciden con el término indicado en el parámetro <code>buscar</code>.
     *
     * @param model     Interfaz/contenedor para pasar datos a la Vista
     * @param pageable  Interfaz con información sobre la paginación
     * @param principal Token de autenticación del usuario
     * @param buscar    Nombre del usuario a buscar (opcional)
     * @return La Vista a mostrar
     */
    @RequestMapping(value = "/usuarios")
    public String users(Model model, Pageable pageable, Principal principal,
                           @RequestParam(value = "buscar", required = false) String buscar) {
        Account loggedAccount = accountRepo.findByUserName(principal.getName());
        String url_params = "?";
        Page<Account> page;

        if (buscar != null) {
            page = accountRepo.findByUserNameIgnoreCaseContaining(buscar, pageable);
            url_params = "?buscar=" + buscar + "&";
            model.addAttribute("buscando", buscar);
        }
        else {
            page = accountRepo.findAll(pageable);
        }

        if (page.getTotalElements() != 0) {
            List<Account> accounts = page.getContent();
            model.addAttribute("accounts", accounts);
        }
        else {
            model.addAttribute("infoMsg", "No existen resultados para el término: <em>" + buscar + "</em>");
        }

        model.addAttribute("loggedAccount", loggedAccount);
        model.addAttribute("url_params", url_params);
        model.addAttribute("page", page);
        model.addAttribute("title", "Administrar Usuarios - PelisUNED");
        return "admin/usuarios";
    }

    /**
     * Maneja la petición de editar alguna de las propiedades del usuario dado y devuelve
     * una respuesta acorde en formato JSON.
     *
     * @param accountId ID del usuario a editar
     * @param modify    Parámetro a modificar
     * @param action    Acción a realizar sobre el parámetro <code>modify</code> (add, delete)
     * @param principal Token de autenticación del usuario
     * @return Respuesta con objeto JSON de la operación
     */
    @ResponseBody
    @RequestMapping("/usuarios/edit")
    public ResponseEntity<ObjectNode> addAdmin(@RequestParam("accountId") Long accountId,
                                               @RequestParam("modify") String modify,
                                               @RequestParam("action") String action,
                                               Principal principal) {
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        Account accountToEdit = accountRepo.findOne(accountId);

        // Aunque el checkbox aparece desactivado, comprobamos que el administrador no está tratando
        // de editarse a sí mismo (por seguridad, se podría cambiar el estado del checkbox manualmente)
        Account loggedAccount = accountRepo.findByUserName(principal.getName());
        if (loggedAccount.getId() == accountToEdit.getId()) {
            response.put("message", "Error: no puedes editar tu propio usuario!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String message = userService.update(accountToEdit, modify, action);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Maneja la petición de borrar la cuenta de un usuario dado y devuelve
     * una respuesta acorde en formato JSON.
     *
     * @param accountId ID del usuario a borrar
     * @param principal Token de autenticación del usuario
     * @return Respuesta con objeto JSON de la operación
     */
    @ResponseBody
    @RequestMapping(value = "/usuarios/delete")
    public ResponseEntity<ObjectNode> deleteUser(@RequestParam("accountId") Long accountId,
                                                 Principal principal) {
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        Account accountToDelete = accountRepo.findOne(accountId);

        // Comprobamos que el administrador no está intentando borrarse a sí mismo
        Account loggedAccount = accountRepo.findByUserName(principal.getName());
        if (loggedAccount.getId() == accountToDelete.getId()) {
            response.put("message", "Error: no puedes borrar tu propia cuenta!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        userService.delete(accountToDelete);
        response.put("message", "Cuenta de " + accountToDelete.getUserName() + " eliminada.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
