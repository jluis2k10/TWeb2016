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

@RestController
@RequestMapping(value = "/rest")
public class RESTController {

    @Autowired private VotesService votesService;
    @Autowired private ActorRepo actorRepo;
    @Autowired private DirectorRepo directorRepo;
    @Autowired private CountryRepo countryRepo;

    /**
     * Resupuesta Ajax (json) a la acción de votar/calificar una película por parte de un usuario
     */
    @RequestMapping(value = "/votar", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<String> processVote(@RequestBody Vote vote, HttpServletRequest request, Principal principal) {
        // Comprobar voto válido
        String refererPath = "";
        try {
            refererPath = (new URL(request.getHeader("referer"))).getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace(); // No debería pasar nunca...
        }
        if (!votesService.isValid(vote, refererPath, principal.getName()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // TODO: ¿más info en la respuesta?

        // Contabilizar el voto
        String jsonResponse = votesService.doVote(vote);

        return ResponseEntity.ok(jsonResponse);
    }

    /**
     * Genera un string compatible con json que contiene una lista de todos los directores de la BBDD
     * @return
     */
    @RequestMapping(value = "/directoresJSON", method = RequestMethod.GET, produces = "text/plain")
    public String directorsInJSON() {
        List<Director> directorsList = directorRepo.findAllByOrderByNameAsc();
        Iterator it = directorsList.iterator();
        return doJSON(it);
    }

    /**
     * Genera un string compatible con json que contiene una lista de todos los actores de la BBDD
     * @return
     */
    @RequestMapping(value = "/actoresJSON", method = RequestMethod.GET, produces = "text/plain")
    public String actorsInJSON() {
        List<Actor> actorsList = actorRepo.findAllByOrderByNameAsc();
        Iterator it = actorsList.iterator();
        return doJSON(it);
    }

    /**
     * Genera un string compatible con json que contiene una lista de todos los países de la BBDD
     * @return
     */
    @RequestMapping(value = "/paisesJSON", method = RequestMethod.GET, produces = "text/plain")
    public String countriesInJSON() {
        List<Country> countriesList = countryRepo.findAllByOrderByNameAsc();
        Iterator it = countriesList.iterator();
        return doJSON(it);
    }

    /**
     * Construir el string apropiado para poder aplicarlo directamente a los chips de la vista.
     * @param it
     * @return
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
