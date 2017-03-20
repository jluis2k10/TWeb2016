package es.jperez2532.controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.jperez2532.components.SessionHandle;
import es.jperez2532.components.UploadPoster;
import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.entities.Genre;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.repositories.FilmRepo;
import es.jperez2532.repositories.GenreRepo;
import es.jperez2532.repositories.RoleRepo;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Necesitamos que implemente ServletContextAware para hacer uso de servletContext.getRealPath()
 * y poder guardar el archivo en el directorio correcto.
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends MainController implements ServletContextAware {

    @Autowired private FilmService filmService;
    @Autowired private UserService userService;
    @Autowired private FilmRepo filmRepo;
    @Autowired private AccountRepo accountRepo;
    @Autowired private SessionHandle sessionHandle;
    @Autowired private RoleRepo roleRepo;
    @Autowired private GenreRepo genreRepo;
    @Autowired private FilmValidator filmValidator;
    @Autowired private UploadPosterValidator uploadPosterValidator;
    @Autowired private GenreValidator genreValidator;
    /* Necesario para pasarle el path del servidor al método que sube el archivo del poster */
    private ServletContext servletContext;

    // TODO: para debug de la cache, eliminar
    /*@Resource(name = "cacheManager")
    private CacheManager cacheManager;*/

    @Transactional
    @RequestMapping("")
    public String home(Model model) {

        /*Collection<String> caches = cacheManager.getCacheNames();
        Cache homePageFilms = cacheManager.getCache("homePageFilms");
        Cache filmsById = cacheManager.getCache("film");
        Cache allFilms = cacheManager.getCache("allFilms");*/

        Pageable limit = new PageRequest(0,6, Sort.Direction.DESC, "id");
        Page<Film> films = filmRepo.findAll(limit);
        Page<Account> accounts = accountRepo.findAll(limit);
        model.addAttribute("films", films.getContent());
        model.addAttribute("accounts", accounts.getContent());

        Long totalFilms = filmRepo.count();
        Map<String, Film> filmStats = new HashMap<>();
        filmStats.put("masVista", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.DESC, "views")).getContent().get(0));
        filmStats.put("menosVista", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.ASC, "views")).getContent().get(0));
        filmStats.put("mejorValorada", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.DESC, "score")).getContent().get(0));
        filmStats.put("peorValorada", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.ASC, "score")).getContent().get(0));
        model.addAttribute("totalFilms", totalFilms);
        model.addAttribute("filmStats", filmStats);

        Map<String, Long> userStats = new HashMap<>();
        userStats.put("totalUsers", accountRepo.count());
        userStats.put("adminUsers", accountRepo.countByAccountRoles_RoleIgnoreCase("admin"));
        userStats.put("inactiveUsers", accountRepo.countByActive(false));
        model.addAttribute("userStats", userStats);

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

        // ¿Se debe actualizar la imagen del poster?
        if (!uploadPoster.getPosterFile().isEmpty()) {
            uploadPosterValidator.validate(uploadPoster, bindingResultPelicula);
            if (!bindingResultPelicula.hasErrors()) {
                try {
                    // Borramos la imagen original
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

    @RequestMapping(value = "/pelicula/recalcular/{id}", method = RequestMethod.GET)
    public String reDoVotes(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        Film film = filmRepo.findOne(id);
        BigDecimal score = film.getScore();
        BigDecimal recalcScore = new BigDecimal(0);
        if (film.getFilmVotes().size() > 0) {
            recalcScore = filmService.reDoVotes(film);
        }
        String requestPath = request.getHeader("referer");
        redirectAttributes.addFlashAttribute("infoMsg",
                "Recalculada correctamente la puntuación de <strong>" + film.getTitle() + "</strong> (" +
                        score.toString() + " -> " + recalcScore.toString() + ").");
        return("redirect:"+requestPath);
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

    @RequestMapping(value = "/usuarios")
    public String usuarios(Model model, Pageable pageable, Principal principal,
                           @RequestParam(value = "buscar", required = false) String buscar) {
        Account loggedAccount = userService.findByUserName(principal.getName());
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

        // Cambiar estado admin
        if (modify.equals("admin")) {
            if (action.equals("add")) {
                accountToEdit.getAccountRoles().add(roleRepo.findByRole("ADMIN"));
                response.put("message", accountToEdit.getUserName() + " añadido al grupo de Administradores");
            }
            else if (action.equals("delete")) {
                accountToEdit.getAccountRoles().remove(roleRepo.findByRole("ADMIN"));
                response.put("message", accountToEdit.getUserName() + " eliminado del grupo de Administradores");
            }
        }
        // Cambiar estado activo
        else if (modify.equals("active")) {
            if (action.equals("add")) {
                accountToEdit.setActive(true);
                response.put("message", accountToEdit.getUserName() + " activado.");
            }
            else if (action.equals("delete")) {
                accountToEdit.setActive(false);
                response.put("message", accountToEdit.getUserName() + " desactivado.");
            }
        }

        accountRepo.save(accountToEdit);
        sessionHandle.expireUserSessions(accountToEdit.getUserName());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
