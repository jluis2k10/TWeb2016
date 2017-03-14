package es.jperez2532.services;

import es.jperez2532.entities.Film;

import javax.servlet.ServletContext;
import java.math.BigDecimal;

/**
 * Created by Jose Luis on 07/03/2017.
 */
public interface FilmService {
    void save(Film film);
    void delete(Film film, ServletContext servletContext);
    BigDecimal reDoVotes(Film film);
}
