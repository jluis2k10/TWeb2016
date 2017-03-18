package es.jperez2532.services;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.entities.Vote;
import es.jperez2532.repositories.VoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class MyVotesService implements VotesService {

    @Autowired private UserService userService;
    @Autowired private VoteRepo voteRepo;
    @Autowired private FilmService filmService;

    /**
     * Aseguramos que el voto recibido es válido.
     *
     * Un voto es válido si la información enviada mediante la petición json (ID película,
     * ID usuario y puntuación) coincide con lo que se obtiene del propio contexto de la
     * petición (ID película desde la URL donde se hace la petición, ID usuario que hace
     * la petición).
     * @param vote
     * @param urlPath
     * @param username
     * @return
     */
    public boolean isValid(Vote vote, String urlPath, String username) {
        // Recuperar ID de la película según URL desde donde se ha hecho la petición
        String pathFormat = "/{context}/pelicula/{id}/{title}";
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Map<String, String> pathVariables = antPathMatcher.extractUriTemplateVariables(pathFormat, urlPath);

        // Recuperar usuario que ha hecho la petición
        Account account = userService.findByUserName(username);

        // Comprobar que el voto coincide
        if (vote.getId().getFilmId() != Long.parseLong(pathVariables.get("id")) ||
                vote.getId().getAccountId() != account.getId())
            return false;
        return true;
    }

    /**
     * Contabilizar el voto.
     *
     * Comprobamos si el usuario ya había votado anteriormente la misma película
     * y actuamos en consecuencia para actualizar la puntuación total.
     *
     * @param newVote
     * @return
     */
    public String doVote(Vote newVote) {
        Vote oldVote = voteRepo.findOne(newVote.getId());
        Film film = filmService.findOne(newVote.getId().getFilmId());
        if (oldVote != null) {
            // Restamos el voto antiguo y sumamos el nuevo
            BigDecimal fScore = film.getScore().multiply(new BigDecimal(film.getNvotes()));
            fScore = fScore.subtract(new BigDecimal(oldVote.getScore()));
            fScore = fScore.add(new BigDecimal(newVote.getScore()));
            BigDecimal nScore = fScore.divide(new BigDecimal(film.getNvotes()), 2, BigDecimal.ROUND_HALF_UP);
            film.setScore(nScore);
        } else {
            // Sumamos el nuevo voto
            int oldNvotes = film.getNvotes();
            BigDecimal fScore = film.getScore().multiply(new BigDecimal(oldNvotes));
            fScore = fScore.add(new BigDecimal(newVote.getScore()));
            BigDecimal nScore = fScore.divide(new BigDecimal(oldNvotes + 1), 2, BigDecimal.ROUND_HALF_UP);
            film.setScore(nScore);
            film.setNvotes(oldNvotes + 1);
        }
        BigDecimal scaled = film.getScore().setScale(0, BigDecimal.ROUND_HALF_UP);
        filmService.update(film);
        voteRepo.save(newVote);

        String response = "{\"myScore\": \"" + newVote.getScore() + "\", " +
                "\"globalScore\": \"" + scaled.intValueExact() + "\"}";
        return response;
    }

}
