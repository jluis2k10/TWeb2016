package es.jperez2532.services;

import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.repositories.FilmRepo;
import es.jperez2532.repositories.VoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Servicios para operaciones con Votos.
 */
@Service
public class MyVotesService implements VotesService {

    private final VoteRepo voteRepo;
    private final AccountRepo accountRepo;
    private final FilmRepo filmRepo;

    /**
     * Constructor de la clase con lsa inyecciones de dependencia apropiadas.
     * @param voteRepo    inyección de {@link VoteRepo}
     * @param accountRepo inyección de {@link AccountRepo}
     * @param filmRepo    inyección de {@link FilmRepo}
     */
    @Autowired
    public MyVotesService(VoteRepo voteRepo, AccountRepo accountRepo, FilmRepo filmRepo) {
        this.voteRepo = voteRepo;
        this.accountRepo = accountRepo;
        this.filmRepo = filmRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "vote", key = "#id")
    public Vote findOne(VotePK id) {
        return voteRepo.findOne(id);
    }

    /**
     * {@inheritDoc}
     */
    public List<Vote> findByAccount(Long accountID) {
        return voteRepo.findByIdAccount(accountID);
    }

    /**
     * {@inheritDoc}
     */
   @CacheEvict(value = "vote", key = "#vote.id")
    public void delete(Vote vote) {
        voteRepo.delete(vote);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Un voto es válido si la información enviada mediante la petición json (ID película,
     * ID usuario y puntuación) coincide con lo que se obtiene del propio contexto de la
     * petición (ID película desde la URL donde se hace la petición, ID usuario que hace
     * la petición).
     */
    public boolean isValid(Vote vote, String urlPath, String username) {
        // Recuperar ID de la película según URL desde donde se ha hecho la petición
        String pathFormat = "/{context}/pelicula/{id}/{title}";
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Map<String, String> pathVariables = antPathMatcher.extractUriTemplateVariables(pathFormat, urlPath);

        // Recuperar usuario que ha hecho la petición
        Account account = accountRepo.findByUserName(username);

        // Comprobar que el voto coincide
        return !(vote.getId().getFilmId() != Long.parseLong(pathVariables.get("id")) ||
                vote.getId().getAccountId() != account.getId());
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = "vote", key = "#vote.id")
    public void populateVote(Vote vote) {
        if (vote.getFilm() == null && vote.getAccount() == null) {
            vote.setFilm(filmRepo.findOne(vote.getId().getFilmId()));
            vote.setAccount(accountRepo.findOne(vote.getId().getAccountId()));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Comprobamos si el usuario ya había votado anteriormente la misma película
     * y actuamos en consecuencia para actualizar la puntuación total.
     * <p>
     * La String JSON que se devuelve incluye la puntuación global de la Película
     * tras contabilizar el Voto recién emitido, y el valor del propio Voto emitido.
     */
    @Caching(evict = {
            @CacheEvict(value = "film", key = "#newVote.film.id"),
            @CacheEvict(value = "account", key = "#newVote.account.userName"),
            @CacheEvict(value = "vote", key = "#newVote.id")})
    public String doVote(Vote newVote) {
        Vote oldVote = voteRepo.findOne(newVote.getId());
        Film film = filmRepo.findOne(newVote.getId().getFilmId());
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
        voteRepo.save(newVote);

        return "{\"myScore\": \"" + newVote.getScore() + "\", " +
                "\"globalScore\": \"" + scaled.intValueExact() + "\"}";
    }

}
