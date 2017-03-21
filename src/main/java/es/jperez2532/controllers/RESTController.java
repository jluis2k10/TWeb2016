package es.jperez2532.controllers;


import es.jperez2532.entities.*;
import es.jperez2532.repositories.ActorRepo;
import es.jperez2532.repositories.CountryRepo;
import es.jperez2532.repositories.DirectorRepo;
import es.jperez2532.services.VotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;

/**
 * Controlador para manejar ciertas peticiones asíncronas que se realizan a lo
 * largo del sitio.
 */
@RestController
@RequestMapping(value = "/rest")
public class RESTController {

    @Autowired private VotesService votesService;
    @Autowired private ActorRepo actorRepo;
    @Autowired private DirectorRepo directorRepo;
    @Autowired private CountryRepo countryRepo;

    /**
     * Resupuesta Ajax (json) a la acción de votar/calificar una película por parte de un usuario.
     *
     * @param vote El voto emitido
     * @param request Información sobre la petición HTTP a éste controlador
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

        return ResponseEntity.ok(jsonResponse);
    }

    /**
     * Genera un string compatible con formato json que contiene una lista de todos
     * los directores de la BBDD
     *
     * @return String JSON con la lista de todos los directores
     */
    @RequestMapping(value = "/directoresJSON", method = RequestMethod.GET, produces = "text/plain")
    public String directorsInJSON() {
        List<Director> directorsList = directorRepo.findAllByOrderByNameAsc();
        Iterator it = directorsList.iterator();
        return doJSON(it);
    }

    /**
     * Genera un string compatible con formato json que contiene una lista de todos
     * los actores de la BBDD
     *
     * @return String JSON con la lista de todos los actores
     */
    @RequestMapping(value = "/actoresJSON", method = RequestMethod.GET, produces = "text/plain")
    public String actorsInJSON() {
        List<Actor> actorsList = actorRepo.findAllByOrderByNameAsc();
        Iterator it = actorsList.iterator();
        return doJSON(it);
    }

    /**
     * Genera un string compatible con formato json que contiene una lista de todos
     * los países (de las películas) de la BBDD
     *
     * @return String JSON con la lista de todos los países
     */
    @RequestMapping(value = "/paisesJSON", method = RequestMethod.GET, produces = "text/plain")
    public String countriesInJSON() {
        List<Country> countriesList = countryRepo.findAllByOrderByNameAsc();
        Iterator it = countriesList.iterator();
        return doJSON(it);
    }

    /**
     * Construir el string apropiado para poder aplicarlo directamente a los <code>chips</code>
     * de la vista.
     *
     * @param it Objeto iterable de {@link AbstractEntity} a partir del cual se genera el String
     * @return String en formato JSON y adaptado a las necesidades de los <code>chips</code> de la Vista
     */
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
}
