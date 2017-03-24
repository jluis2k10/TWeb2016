package es.jperez2532.controllers;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.jperez2532.entities.*;
import es.jperez2532.services.FilmService;
import es.jperez2532.services.UserService;
import es.jperez2532.services.VotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.List;

/**
 * Controlador para manejar ciertas peticiones asíncronas que se realizan a lo
 * largo del sitio.
 */
@RestController
@RequestMapping(value = "/rest")
public class RESTController {

    private final FilmService filmService;
    private final UserService userService;
    private final VotesService votesService;

    /**
     * Constructor de la clase con las inyecciones de dependencia apropiadas.
     *
     * @param filmService  inyección {@link FilmService}
     * @param userService  inyección {@link UserService}
     * @param votesService inyección {@link VotesService}
     */
    @Autowired
    public RESTController(FilmService filmService, UserService userService, VotesService votesService) {
        this.filmService = filmService;
        this.userService = userService;
        this.votesService = votesService;
    }

    /**
     * Resupuesta Ajax (json) a la acción de votar/calificar una película por parte de un usuario.
     *
     * @param vote      El voto emitido
     * @param request   Información sobre la petición HTTP a éste controlador
     * @param principal Token de autenticación del usuario
     * @return Respuesta del servidor a la petición
     */
    @RequestMapping(value = "/votar", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<String> processVote(@RequestBody Vote vote, HttpServletRequest request,
                                              Principal principal) {
        String refererPath = "";
        // Comprobar voto válido
        try {
            refererPath = (new URL(request.getHeader("referer"))).getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace(); // No debería pasar nunca...
        }
        if (!votesService.isValid(vote, refererPath, principal.getName()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        // Contabilizar el voto
        votesService.populateVote(vote);
        String jsonResponse = votesService.doVote(vote);
        filmService.updateVotes(vote.getFilm());

        return ResponseEntity.ok(jsonResponse);
    }

    /**
     * Recoge una petición para añadir una película a la lista de reproducción del usuario
     * y devuelve confirmación JSON de la operación.
     *
     * @param filmId    ID de la película a añadir a la lista
     * @param principal Token de autenticación del usuario
     * @return Resupuesta JSON (en forma de text/html)
     */
    @RequestMapping(value = "/milista/add", method = RequestMethod.GET)
    public ResponseEntity<String> addToWatchList(@RequestParam("film-id") Long filmId, Principal principal) {
        userService.addFilmToWatchlist(principal.getName(), filmId);
        return ResponseEntity.ok("{}");
    }

    /**
     * Recoge una petición para eliminar una película de la lista de reproducción del usuario
     * y devuelve confirmación JSON de la operación.
     *
     * @param filmId    ID de la película a eliminar de la lista
     * @param principal Token de autenticación del usuario
     * @return Respuesta JSON (en forma de text/html)
     */
    @RequestMapping(value = "/milista/delete", method = RequestMethod.GET)
    public ResponseEntity<String> deleteFromWatchList(@RequestParam("film-id") Long filmId, Principal principal) {
        userService.deleteFilmFromWatchlist(principal.getName(), filmId);
        return ResponseEntity.ok("{}");
    }

    /**
     * Genera un objeto JSON con la lista de todos los directores.
     * @return Respuesta con el objeto JSON que contiene la lista de todos los directores
     */
    @RequestMapping(value = "/directoresJSON", method = RequestMethod.GET)
    public ResponseEntity<ObjectNode> directorsInJSON() {
        List<Director> directorList = filmService.findDirectorsAll();
        ObjectNode response = this.doJSON(directorList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Genera un objeto JSON con la lista de todos los actores.
     * @return Respuesta con el objeto JSON que contiene la lista de todos los actores
     */
    @RequestMapping(value = "/actoresJSON", method = RequestMethod.GET)
    public ResponseEntity<ObjectNode> actorsInJSON() {
        List<Actor> actorsList = filmService.findActorsAll();
        ObjectNode response = this.doJSON(actorsList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Genera un objeto JSON con la lista de todos los países.
     * @return Respuesta con el objeto JSON que contiene la lista de todos los países
     */
    @RequestMapping(value = "/paisesJSON", method = RequestMethod.GET)
    public ResponseEntity<ObjectNode> countriesInJSON() {
        List<Country> countriesList = filmService.findCountriesAll();
        ObjectNode response = this.doJSON(countriesList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Construir el objeto JSON apropiado para poder aplicarlo directamente a los
     * <code>chips</code> de la vista.
     * @param entityList Lista de entidades
     * @return Objeto compatible JSON con la lista de entidades
     */
    private ObjectNode doJSON(List entityList) {
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        ObjectNode childNodes = JsonNodeFactory.instance.objectNode();
        for (Object entity: entityList)
            childNodes.put( ((AbstractEntity)entity).getName(), "null" );
        response.set("autocompleteData", childNodes);
        return response;
    }
}
