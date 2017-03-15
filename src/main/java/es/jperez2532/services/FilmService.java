package es.jperez2532.services;

import es.jperez2532.entities.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.ServletContext;
import java.math.BigDecimal;

/**
 * Created by Jose Luis on 07/03/2017.
 */
public interface FilmService {
    void save(Film film);
    boolean delete(Film film, ServletContext servletContext) throws RuntimeException;
    BigDecimal reDoVotes(Film film);
    Page<Film> search(String term, Pageable pageable);
}
