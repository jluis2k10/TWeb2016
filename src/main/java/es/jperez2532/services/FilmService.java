package es.jperez2532.services;

import es.jperez2532.entities.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jose Luis on 07/03/2017.
 */
public interface FilmService {
    void save(Film film);
    boolean delete(Film film, ServletContext servletContext) throws RuntimeException;
    void update(Film film);

    Film findOne(Long id);
    Page<Film> findAll(Pageable pageable);
    Page<Film> findByGenre(String genre, Pageable pageable);
    Page<Film> findByDirector(String director, Pageable pageable);
    Page<Film> findByActor(String actor, Pageable pageable);
    Page<Film> findByCountry(String country, Pageable pageable);

    BigDecimal reDoVotes(Film film);
    Page<Film> search(String term, Pageable pageable);
    Set<String> getRandomGenres(int limit);
    Map<String, Collection<Film>> findHomePageFilms(int limit, Set<String> genres);
}
